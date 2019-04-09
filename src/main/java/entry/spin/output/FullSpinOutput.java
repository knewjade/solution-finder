package entry.spin.output;

import common.buildup.BuildUp;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.SimpleOperation;
import concurrent.LockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.neighbor.SimpleOriginalPiece;
import core.srs.MinoRotationDetail;
import core.srs.RotateDirection;
import core.srs.SpinResult;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import exceptions.FinderExecuteException;
import output.HTMLBuilder;
import searcher.spins.SpinCommons;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;
import searcher.spins.spin.Spin;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FullSpinOutput implements SpinOutput {
    private final OneFumenParser oneFumenParser;
    private final MinoFactory minoFactory;
    private final MinoRotationDetail minoRotationDetail;
    private final LockedReachableThreadLocal lockedReachableThreadLocal;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;

    public FullSpinOutput(
            OneFumenParser oneFumenParser,
            MinoFactory minoFactory, MinoRotationDetail minoRotationDetail,
            LockedReachableThreadLocal lockedReachableThreadLocal,
            RotateReachableThreadLocal rotateReachableThreadLocal
    ) {
        this.oneFumenParser = oneFumenParser;
        this.minoFactory = minoFactory;
        this.minoRotationDetail = minoRotationDetail;
        this.lockedReachableThreadLocal = lockedReachableThreadLocal;
        this.rotateReachableThreadLocal = rotateReachableThreadLocal;
    }

    @Override
    public void output(MyFile myFile, List<Candidate> results, Field initField, int fieldHeight) throws FinderExecuteException {
        HTMLBuilder<FullSpinColumn> htmlBuilder = new HTMLBuilder<>("Spin Result");
        htmlBuilder.addHeader(String.format("%d solutions", results.size()));

        // HTMLを作成する
        for (Candidate candidate : results) {
            add(htmlBuilder, candidate, initField, fieldHeight);
        }

        System.out.println("Found solutions = " + htmlBuilder.getSize());

        // 書き込み
        try (BufferedWriter writer = myFile.newBufferedWriter()) {
            ArrayList<FullSpinColumn> sorted = new ArrayList<>(htmlBuilder.getRegisteredColumns());
            sorted.sort(Comparator.reverseOrder());

            List<String> lines = htmlBuilder.toList(sorted, true);
            for (String line : lines) {
                writer.write(line);
            }

            writer.flush();
        } catch (Exception e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }

    private void add(HTMLBuilder<FullSpinColumn> htmlBuilder, Candidate candidate, Field initField, int fieldHeight) {
        LockedReachable lockedReachable = lockedReachableThreadLocal.get();

        // Tを使って消去されるライン数
        Result result = candidate.getResult();
        SimpleOriginalPiece operationT = candidate.getOperationT();
        int clearedLineOnlyT = Long.bitCount(result.getAllMergedFilledLine() & operationT.getUsingKey());

        // Tミノを除いた地形で揃っているラインを消去する
        Field freeze = candidate.getAllMergedFieldWithoutT().freeze();
        long filledLineWithoutT = candidate.getAllMergedFilledLineWithoutT();

        assert operationT.getNeedDeletedKey() == 0L || (filledLineWithoutT & operationT.getNeedDeletedKey()) != 0L;
        freeze.clearLine();

        // 消去されたラインに合わせてyを移動
        Mino mino = operationT.getMino();
        int y = operationT.getY();
        int slideY = Long.bitCount(filledLineWithoutT & KeyOperators.getMaskForKeyBelowY(y + mino.getMinY()));
        SimpleOperation slideOperation = new SimpleOperation(operationT.getPiece(), operationT.getRotate(), operationT.getX(), y - slideY);

        // 優先度の高いスピンを探索
        Spin maxSpin = null;
        int maxPriority = -1;

        // 左回転, 右回転
        for (RotateDirection direction : RotateDirection.values()) {
            RotateDirection beforeDirection = RotateDirection.reverse(direction);

            Mino before = minoFactory.create(slideOperation.getPiece(), slideOperation.getRotate().get(beforeDirection));
            int[][] patterns = minoRotationDetail.getPatternsFrom(before, direction);

            List<Spin> spins = getSpins(lockedReachable, freeze, slideOperation, before, patterns, direction, fieldHeight, clearedLineOnlyT);
            for (Spin spin : spins) {
                int priority = getSpinPriority(spin);
                if (maxSpin == null || maxPriority < priority) {
                    maxSpin = spin;
                    maxPriority = priority;
                }
            }
        }

        assert maxSpin != null;

        final int finalPriority = maxPriority;
        FullSpinColumn column = new FullSpinColumn(maxSpin, finalPriority, getSpinString(maxSpin));

        // テト譜
        List<MinoOperationWithKey> operations = result.operationStream().collect(Collectors.toList());
        String fumen = oneFumenParser.parse(operations, initField, fieldHeight);

        // 表示されるタイトル
        String name = operations.stream()
                .map(operation -> String.format("%s-%s", operation.getPiece(), operation.getRotate()))
                .collect(Collectors.joining(" "));

        // 解の優先度
        Field freezeForClearedLineAll = result.getAllMergedField().freeze();
        int clearedLineAll = freezeForClearedLineAll.clearLine();

        int numOfHoles = getNumOfHoles(freezeForClearedLineAll);
        int numOfPieces = operations.size();
        int solutionPriority = calcSolutionPriority(operationT, clearedLineOnlyT, clearedLineAll, numOfHoles, numOfPieces);

        // その解をそのまま組み立てられるか
        boolean cansBuildWithoutT = BuildUp.existsValidBuildPattern(initField, operations.stream().filter(op -> !operationT.equals(op)), fieldHeight, lockedReachable);

        // そのままTスピンできるか
        String mark = cansBuildWithoutT ? (
                rotateReachableThreadLocal.get().checks(freeze, mino, operationT.getX(), y - slideY, fieldHeight) ? "O" : "X"
        ) : "-";
        String aLink = String.format(
                "<div>[%s] <a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [clear=%d, hole=%d, piece=%d]</div>",
                mark, fumen, name, clearedLineAll, numOfHoles, numOfPieces
        );

        htmlBuilder.addColumn(column, aLink, solutionPriority);
    }

    private int calcSolutionPriority(SimpleOriginalPiece operationT, int clearedLineOnlyT, int clearedLineAll, int numOfHoles, int numOfPieces) {
        // 優先度高: 使用ミノが少ない -> Tミノのy座標が低い -> Tスピン以外での消去ライン数が小さい -> ホール数が小さい
        return numOfHoles * 100 * 100 * 100
                + (clearedLineAll - clearedLineOnlyT) * 100 * 100
                + operationT.getY() * 100
                + numOfPieces;
    }

    private List<Spin> getSpins(LockedReachable lockedReachable, Field fieldWithoutT, Operation operation, Mino before, int[][] patterns, RotateDirection direction, int maxHeight, int clearedLine) {
        List<Spin> spins = new ArrayList<>();

        for (int[] pattern : patterns) {
            // 開店前の位置に移動
            int beforeX = operation.getX() - pattern[0];
            int beforeY = operation.getY() - pattern[1];

            if (beforeX + before.getMinX() < 0 || 10 <= beforeX + before.getMaxX()) {
                continue;
            }

            if (beforeY + before.getMinY() < 0) {
                continue;
            }

            if (!fieldWithoutT.canPut(before, beforeX, beforeY)) {
                continue;
            }

            SpinResult spinResult = minoRotationDetail.getKicks(fieldWithoutT, direction, before, beforeX, beforeY);

            if (spinResult == SpinResult.NONE) {
                continue;
            }

            // 回転後に元の場所に戻る
            if (spinResult.getToX() != operation.getX() || spinResult.getToY() != operation.getY()) {
                continue;
            }

            // 回転前の位置に移動できる
            if (!lockedReachable.checks(fieldWithoutT, before, beforeX, beforeY, maxHeight)) {
                continue;
            }

            Spin spin = SpinCommons.getSpins(fieldWithoutT, spinResult, clearedLine);
            spins.add(spin);
        }

        return spins;
    }

    private int getSpinPriority(Spin spin) {
        int clearedLine = spin.getClearedLine();

        switch (spin.getSpin()) {
            case Mini: {
                return clearedLine * 10 + 1;
            }
            case Regular: {
                switch (spin.getName()) {
                    case Iso: {
                        return clearedLine * 10 + 2;
                    }
                    case Fin: {
                        return clearedLine * 10 + 3;
                    }
                    case Neo: {
                        return clearedLine * 10 + 4;
                    }
                    case NoName: {
                        return clearedLine * 10 + 5;
                    }
                }
            }
        }

        throw new IllegalStateException();
    }

    private String getSpinString(Spin spin) {
        int clearedLine = spin.getClearedLine();
        String lineString = getSendLineString(clearedLine);
        switch (spin.getSpin()) {
            case Mini: {
                return lineString + " [Mini]";
            }
            case Regular: {
                return lineString + " [" + spin.getName().getName() + "]";
            }
        }

        throw new IllegalStateException();
    }

    private String getSendLineString(int clearedLine) {
        assert 1 <= clearedLine && clearedLine <= 3;
        switch (clearedLine) {
            case 1:
                return "Single";
            case 2:
                return "Double";
            case 3:
                return "Triple";
        }
        throw new IllegalStateException();
    }

    private int getNumOfHoles(Field field) {
        Field freeze = field.freeze();
        freeze.slideDown();
        freeze.reduce(field);
        return freeze.getNumOfAllBlocks();
    }
}

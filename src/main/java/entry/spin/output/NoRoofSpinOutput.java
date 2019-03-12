package entry.spin.output;

import common.buildup.BuildUp;
import common.datastore.MinoOperationWithKey;
import concurrent.LockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import exceptions.FinderExecuteException;
import output.HTMLBuilder;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NoRoofSpinOutput implements SpinOutput {
    private final OneFumenParser oneFumenParser;
    private final LockedReachableThreadLocal lockedReachableThreadLocal;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;

    public NoRoofSpinOutput(
            OneFumenParser oneFumenParser,
            LockedReachableThreadLocal lockedReachableThreadLocal,
            RotateReachableThreadLocal rotateReachableThreadLocal
    ) {
        this.oneFumenParser = oneFumenParser;
        this.lockedReachableThreadLocal = lockedReachableThreadLocal;
        this.rotateReachableThreadLocal = rotateReachableThreadLocal;
    }

    @Override
    public void output(MyFile myFile, List<Candidate> results, Field initField, int fieldHeight) throws FinderExecuteException {
        HTMLBuilder<NoRoofColumn> htmlBuilder = new HTMLBuilder<>("Spin Result");
        htmlBuilder.addHeader(String.format("%d solutions", results.size()));

        // HTMLを作成する
        for (Candidate candidate : results) {
            add(htmlBuilder, candidate, initField, fieldHeight);
        }

        System.out.println("solutions = " + htmlBuilder.getSize());

        // 書き込み
        try (BufferedWriter writer = myFile.newBufferedWriter()) {
            ArrayList<NoRoofColumn> sorted = new ArrayList<>(htmlBuilder.getRegisteredColumns());
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

    private void add(HTMLBuilder<NoRoofColumn> htmlBuilder, Candidate candidate, Field initField, int fieldHeight) {
        LockedReachable lockedReachable = lockedReachableThreadLocal.get();

        // Tを使って消去されるライン数
        Result result = candidate.getResult();
        SimpleOriginalPiece operationT = candidate.getOperationT();
        int clearedLineOnlyT = Long.bitCount(result.getAllMergedFilledLine() & operationT.getUsingKey());

        NoRoofColumn column = new NoRoofColumn(clearedLineOnlyT, getSendLineString(clearedLineOnlyT));

        // テト譜
        List<MinoOperationWithKey> operations = result.operationStream().collect(Collectors.toList());
        String fumen = oneFumenParser.parse(operations, initField, fieldHeight);

        // 表示されるタイトル
        String name = operations.stream()
                .map(operation -> String.format("%s-%s", operation.getPiece(), operation.getRotate()))
                .collect(Collectors.joining(" "));

        // 解の優先度
        int solutionPriority = calcSolutionPriority(result, operationT, clearedLineOnlyT, operations);

        // その解をそのまま組み立てられるか
        boolean cansBuildWithoutT = BuildUp.existsValidBuildPattern(initField, operations.stream().filter(op -> !operationT.equals(op)), fieldHeight, lockedReachable);

        // そのままTスピンできるか
        Field fieldWithoutT = candidate.getAllMergedFieldWithoutT();
        String mark = cansBuildWithoutT ? (
                rotateReachableThreadLocal.get().checks(fieldWithoutT, operationT.getMino(), operationT.getX(), operationT.getY(), fieldHeight) ? "O" : "X"
        ) : " ";
        String aLink = String.format(
                "<div>[%s] <a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a></div>",
                mark, fumen, name
        );

        htmlBuilder.addColumn(column, aLink, solutionPriority);
    }

    private int calcSolutionPriority(Result result, SimpleOriginalPiece operationT, int clearedLineOnlyT, List<MinoOperationWithKey> operations) {
        Field freeze = result.getAllMergedField().freeze();
        int clearedLineAll = freeze.clearLine();

        int numOfHoles = getNumOfHoles(freeze);
        int numOfPieces = operations.size();

        // 優先度高: 使用ミノが少ない -> Tミノのy座標が低い -> Tスピン以外での消去ライン数が小さい -> ホール数が小さい
        return numOfHoles * 100 * 100 * 100
                + (clearedLineAll - clearedLineOnlyT) * 100 * 100
                + operationT.getY() * 100
                + numOfPieces;
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

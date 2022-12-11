package entry.spin.output;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import common.parser.OperationWithKeyInterpreter;
import concurrent.ILockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.ILockedReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.neighbor.SimpleOriginalPiece;
import core.srs.MinoRotationDetail;
import core.srs.RotateDirection;
import core.srs.SpinResult;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import entry.spin.FilterType;
import exceptions.FinderExecuteException;
import lib.AsyncBufferedFileWriter;
import searcher.spins.SpinCommons;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;
import searcher.spins.spin.Spin;
import searcher.spins.spin.SpinDefaultPriority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVSpinOutput implements SpinOutput {
    private final MinoFactory minoFactory;
    private final MinoRotationDetail minoRotationDetail;
    private final ILockedReachableThreadLocal lockedReachableThreadLocal;
    private final boolean isSearchRoof;
    private final Formatter formatter;

    public CSVSpinOutput(
            FumenParser fumenParser,
            MinoFactory minoFactory, MinoRotationDetail minoRotationDetail,
            ILockedReachableThreadLocal lockedReachableThreadLocal,
            RotateReachableThreadLocal rotateReachableThreadLocal,
            FilterType filterType,
            boolean isSearchRoof
    ) {
        this.minoFactory = minoFactory;
        this.minoRotationDetail = minoRotationDetail;
        this.lockedReachableThreadLocal = lockedReachableThreadLocal;
        this.isSearchRoof = isSearchRoof;
        this.formatter = new Formatter(fumenParser, lockedReachableThreadLocal, rotateReachableThreadLocal, filterType);
    }

    @Override
    public int output(MyFile myFile, List<Candidate> results, Field initField, int fieldHeight) throws FinderExecuteException {
        Function<Candidate, Optional<CSVItem>> candidateOptionalFunction = isSearchRoof ?
                candidate -> {
                    Optional<Spin> spinOptional = getSpin(candidate, fieldHeight);
                    if (!spinOptional.isPresent()) {
                        return Optional.empty();
                    }
                    return formatter.getCSVItems(candidate, spinOptional.get(), initField, fieldHeight);
                } :
                candidate -> formatter.getCSVItems(candidate, null, initField, fieldHeight);

        List<CSVItem> lines = results.stream()
                .map(candidateOptionalFunction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingInt(CSVItem::getPriority))
                .collect(Collectors.toList());

        try (AsyncBufferedFileWriter writer = myFile.newAsyncWriter()) {
            writer.writeAndNewLine(toHeaderLine());
            for (CSVItem csvItem : lines) {
                writer.writeAndNewLine(toLine(csvItem));
            }
            writer.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }

        return lines.size();
    }

    private Optional<Spin> getSpin(Candidate candidate, int fieldHeight) {
        ILockedReachable lockedReachable = lockedReachableThreadLocal.get();

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
        for (RotateDirection direction : RotateDirection.valuesNo180()) {
            RotateDirection beforeDirection = RotateDirection.reverse(direction);

            Mino before = minoFactory.create(slideOperation.getPiece(), slideOperation.getRotate().get(beforeDirection));
            int[][] patterns = minoRotationDetail.getPatternsFrom(before, direction);

            List<Spin> spins = getSpins(lockedReachable, freeze, slideOperation, before, patterns, direction, fieldHeight, clearedLineOnlyT);
            for (Spin spin : spins) {
                int priority = SpinDefaultPriority.getSpinPriority(spin);
                if (maxSpin == null || maxPriority < priority) {
                    maxSpin = spin;
                    maxPriority = priority;
                }
            }
        }

        return Optional.ofNullable(maxSpin);
    }

    private List<Spin> getSpins(ILockedReachable lockedReachable, Field fieldWithoutT, Operation operation, Mino before, int[][] patterns, RotateDirection direction, int maxHeight, int clearedLine) {
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

    public String toHeaderLine() {
        return "テト譜,有効マーク,使用ミノ,使用ミノ数,T-Spinライン数,MINI,名前,トータルクリアライン数,hole,t-rotate,t-x,t-y,t-deleted-linekey";
    }

    public String toLine(CSVItem item) {
        String operationT = OperationWithKeyInterpreter.parseToStringSimple(item.getOperationT());
        return String.format(
                "http://fumen.zui.jp/?v115@%s,%s,%s,%d,%d,%s,%s,%d,%d,%s",
                item.getData(), item.getMark(), item.getUsingPieces(), item.getNumOfUsingPieces(),
                item.getClearedLinesTOnly(), item.isMini() ? "O" : "X", item.getSpinName(),
                item.getTotalClearedLines(), item.getNumOfHoles(), operationT
        );
    }
}

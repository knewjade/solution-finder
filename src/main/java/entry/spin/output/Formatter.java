package entry.spin.output;

import common.buildup.BuildUp;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.Pair;
import concurrent.ILockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.ILockedReachable;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import entry.path.output.FumenParser;
import entry.spin.FilterType;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;
import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpinNames;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

enum SolutionType {
    AllOK(10, 'O'),
    InvalidT(5, 'X'),
    InvalidShape(0, '-'),
    ;

    private final int priority;
    private final char mark;

    SolutionType(int priority, char mark) {
        this.priority = priority;
        this.mark = mark;
    }

    public boolean isHighPriorityThanOrEqualTo(SolutionType type) {
        return type.priority <= this.priority;
    }

    public char getMark() {
        return mark;
    }
}

class CSVItem {
    private final MinoOperationWithKey operationT;
    private final String data;
    private final SolutionType solutionType;
    private final List<MinoOperationWithKey> operations;
    private final int clearedLinesTOnly;
    private final boolean isMini;
    private final TSpinNames spinName;
    private final int totalClearedLines;
    private final int hole;

    private final int priority;

    CSVItem(
            MinoOperationWithKey operationT, String data, SolutionType solutionType, List<MinoOperationWithKey> operations, int clearedLinesTOnly,
            Spin spin, int totalClearedLines, int hole, int priority
    ) {
        this.operationT = operationT;
        this.data = data;
        this.solutionType = solutionType;
        this.operations = operations;
        this.clearedLinesTOnly = clearedLinesTOnly;
        this.isMini = Optional.ofNullable(spin).map(Spin::isMini).orElse(false);
        this.spinName = Optional.ofNullable(spin).map(Spin::getName).orElse(TSpinNames.NoName);
        this.totalClearedLines = totalClearedLines;
        this.hole = hole;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getData() {
        return data;
    }

    public String getMark() {
        return String.valueOf(solutionType.getMark());
    }

    public String getUsingPieces() {
        return operations.stream()
                .map(Operation::getPiece)
                .map(Piece::getName)
                .collect(Collectors.joining());
    }

    public int getClearedLinesTOnly() {
        return clearedLinesTOnly;
    }

    public boolean isMini() {
        return isMini;
    }

    public String getSpinName() {
        return spinName != TSpinNames.NoName ? spinName.getName().toUpperCase() : "";
    }

    public int getTotalClearedLines() {
        return totalClearedLines;
    }

    public int getNumOfHoles() {
        return hole;
    }

    public int getNumOfUsingPieces() {
        return operations.size();
    }

    public List<MinoOperationWithKey> getOperations() {
        return operations;
    }

    public String getName() {
        return operations.stream()
                .map(operation -> String.format("%s-%s", operation.getPiece(), operation.getRotate()))
                .collect(Collectors.joining(" "));
    }

    public MinoOperationWithKey getOperationT() {
        return operationT;
    }
}

public class Formatter {
    private final FumenParser fumenParser;
    private final ILockedReachableThreadLocal lockedReachableThreadLocal;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;
    private final SolutionType lowerSolutionType;

    public Formatter(
            FumenParser fumenParser,
            ILockedReachableThreadLocal lockedReachableThreadLocal,
            RotateReachableThreadLocal rotateReachableThreadLocal,
            FilterType filterType
    ) {
        this.fumenParser = fumenParser;
        this.lockedReachableThreadLocal = lockedReachableThreadLocal;
        this.rotateReachableThreadLocal = rotateReachableThreadLocal;
        this.lowerSolutionType = getLowerSolutionType(filterType);
    }

    private SolutionType getLowerSolutionType(FilterType filterType) {
        switch (filterType) {
            case Strict:
                return SolutionType.AllOK;
            case IgnoreT:
                return SolutionType.InvalidT;
            case None:
                return SolutionType.InvalidShape;
        }
        throw new IllegalArgumentException("Unsupported solution type: " + filterType);
    }

    Optional<CSVItem> getCSVItems(Candidate candidate, Spin spin, Field initField, int fieldHeight) {
        Optional<Spin> spinOptional = Optional.ofNullable(spin);

        Result result = candidate.getResult();
        SimpleOriginalPiece operationT = candidate.getOperationT();
        int clearedLineOnlyT = Long.bitCount(result.getAllMergedFilledLine() & operationT.getUsingKey());

        List<MinoOperationWithKey> operations = result.operationStream().collect(Collectors.toList());
        SolutionType solutionType = getSolutionType(candidate, initField, fieldHeight, operations, operationT);
        if (!solutionType.isHighPriorityThanOrEqualTo(lowerSolutionType)) {
            return Optional.empty();
        }

        // テト譜
        String fumen = fumenParser.parse(operations, initField, fieldHeight);

        // 解の優先度
        Field freezeForClearedLineAll = result.getAllMergedField().freeze();
        int clearedLineAll = freezeForClearedLineAll.clearLine();

        int numOfHoles = getNumOfHoles(freezeForClearedLineAll);
        int numOfPieces = operations.size();

        boolean isMini = spinOptional.map(Spin::isMini).orElse(false);
        int solutionPriority = calcSolutionPriority(
                operationT, clearedLineOnlyT, clearedLineAll, numOfHoles, numOfPieces, isMini
        );

        return Optional.of(new CSVItem(
                operationT, fumen, solutionType, operations, clearedLineOnlyT, spin, clearedLineAll, numOfHoles, solutionPriority
        ));
    }

    Optional<Pair<String, Integer>> get(Candidate candidate, Spin spin, Field initField, int fieldHeight) {
        return getCSVItems(candidate, spin, initField, fieldHeight)
                .map(csvItem -> {
                    String aLink = String.format(
                            "<div>[%s] <a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [clear=%d, hole=%d, piece=%d]</div>",
                            csvItem.getMark(), csvItem.getData(), csvItem.getName(),
                            csvItem.getTotalClearedLines(), csvItem.getNumOfHoles(), csvItem.getNumOfUsingPieces()
                    );
                    return new Pair<>(aLink, csvItem.getPriority());
                });
    }

    private SolutionType getSolutionType(
            Candidate candidate, Field initField, int fieldHeight,
            List<MinoOperationWithKey> operations, SimpleOriginalPiece operationT
    ) {
        ILockedReachable lockedReachable = lockedReachableThreadLocal.get();

        // その解をそのまま組み立てられるか
        boolean cansBuildWithoutT = BuildUp.existsValidBuildPattern(
                initField, operations.stream().filter(op -> !operationT.equals(op)), fieldHeight, lockedReachable
        );

        if (!cansBuildWithoutT) {
            return SolutionType.InvalidShape;
        }

        // そのままTスピンできるか
        Field freeze = candidate.getAllMergedFieldWithoutT().freeze();
        long filledLineWithoutT = candidate.getAllMergedFilledLineWithoutT();
        assert operationT.getNeedDeletedKey() == 0L || (filledLineWithoutT & operationT.getNeedDeletedKey()) != 0L;
        freeze.clearLine();

        Mino mino = operationT.getMino();
        int y = operationT.getY();
        int slideY = Long.bitCount(filledLineWithoutT & KeyOperators.getMaskForKeyBelowY(y + mino.getMinY()));

        if (!freeze.isOnGround(mino, operationT.getX(), y - slideY)) {
            return SolutionType.InvalidShape;
        }

        RotateReachable rotateReachable = rotateReachableThreadLocal.get();
        boolean canReachTWithSpin = rotateReachable.checks(freeze, mino, operationT.getX(), y - slideY, fieldHeight);
        return canReachTWithSpin ? SolutionType.AllOK : SolutionType.InvalidT;
    }

    private int calcSolutionPriority(SimpleOriginalPiece operationT, int clearedLineOnlyT, int clearedLineAll, int numOfHoles, int numOfPieces, boolean isMini) {
        // 優先度高: MINIではない -> ホール数が小さい -> Tミノのy座標が低い -> Tスピン以外での消去ライン数が小さい -> 使用ミノが少ない
        return (isMini ? 1 : 0) * 20 * 24 * 24 * 500
                + numOfHoles * 20 * 24 * 24
                + (clearedLineAll - clearedLineOnlyT) * 20 * 24
                + operationT.getY() * 20
                + numOfPieces;
    }

    private int getNumOfHoles(Field field) {
        Field freeze = field.freeze();
        freeze.slideDown();
        freeze.reduce(field);
        return freeze.getNumOfAllBlocks();
    }
}


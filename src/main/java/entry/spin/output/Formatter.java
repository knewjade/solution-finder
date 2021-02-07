package entry.spin.output;

import common.buildup.BuildUp;
import common.datastore.MinoOperationWithKey;
import common.datastore.Pair;
import concurrent.LockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.neighbor.SimpleOriginalPiece;
import entry.path.output.FumenParser;
import entry.spin.FilterType;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;

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

public class Formatter {
    private final FumenParser fumenParser;
    private final LockedReachableThreadLocal lockedReachableThreadLocal;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;
    private final SolutionType lowerSolutionType;

    public Formatter(
            FumenParser fumenParser,
            LockedReachableThreadLocal lockedReachableThreadLocal,
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

    Optional<Pair<String, Integer>> get(Candidate candidate, Field initField, int fieldHeight) {
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

        String aLink = String.format(
                "<div>[%s] <a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [clear=%d, hole=%d, piece=%d]</div>",
                solutionType.getMark(), fumen, name, clearedLineAll, numOfHoles, numOfPieces
        );

        return Optional.of(new Pair<>(aLink, solutionPriority));
    }

    private SolutionType getSolutionType(
            Candidate candidate, Field initField, int fieldHeight,
            List<MinoOperationWithKey> operations, SimpleOriginalPiece operationT
    ) {
        LockedReachable lockedReachable = lockedReachableThreadLocal.get();

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

    private int calcSolutionPriority(SimpleOriginalPiece operationT, int clearedLineOnlyT, int clearedLineAll, int numOfHoles, int numOfPieces) {
        // 優先度高: 使用ミノが少ない -> Tミノのy座標が低い -> Tスピン以外での消去ライン数が小さい -> ホール数が小さい
        return numOfHoles * 100 * 100 * 100
                + (clearedLineAll - clearedLineOnlyT) * 100 * 100
                + operationT.getY() * 100
                + numOfPieces;
    }

    private int getNumOfHoles(Field field) {
        Field freeze = field.freeze();
        freeze.slideDown();
        freeze.reduce(field);
        return freeze.getNumOfAllBlocks();
    }
}

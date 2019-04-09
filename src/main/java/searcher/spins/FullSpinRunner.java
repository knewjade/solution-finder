package searcher.spins;

import concurrent.RotateReachableThreadLocal;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.candidates.Candidate;
import searcher.spins.candidates.SimpleCandidate;
import searcher.spins.results.Result;
import searcher.spins.roof.RoofRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FullSpinRunner implements SpinRunner {
    private final NoRoofSpinRunner noRoofSpinRunner;

    public FullSpinRunner() {
        this.noRoofSpinRunner = new NoRoofSpinRunner();
    }

    @Override
    public Stream<? extends Candidate> search(SecondPreSpinRunner secondPreSpinRunner, int minClearedLine) {
        RoofRunner roofRunner = secondPreSpinRunner.getRoofRunner();
        RotateReachableThreadLocal rotateReachableThreadLocal = secondPreSpinRunner.getRotateReachableThreadLocal();

        Field initField = secondPreSpinRunner.getInitField();
        int fieldHeight = secondPreSpinRunner.getFieldHeight();

        return noRoofSpinRunner.search(secondPreSpinRunner, minClearedLine)
                .flatMap(roofRunner::search)
                .filter(roofResult -> {
                    Result lastResult = roofResult.getLastResult();
                    SimpleOriginalPiece operationT = roofResult.getOperationT();
                    long filledLineByT = operationT.getUsingKey() & lastResult.getAllMergedFilledLine();

                    // Tミノのライン消去に関わらないミノを抽出
                    List<SimpleOriginalPiece> operations = lastResult.operationStream()
                            .filter(operation -> (filledLineByT & operation.getUsingKey()) == 0L)
                            .collect(Collectors.toList());

                    if (operations.isEmpty()) {
                        return true;
                    }

                    RotateReachable rotateReachable = rotateReachableThreadLocal.get();
                    Field allMergedFieldWithoutT = roofResult.getAllMergedFieldWithoutT();
                    long onePieceFilledKeyWithoutT = roofResult.getOnePieceFilledKeyWithoutT();

                    Mino mino = operationT.getMino();
                    int tx = operationT.getX();
                    int ty = operationT.getY();

                    List<SimpleOriginalPiece> allOperations = lastResult.operationStream().collect(Collectors.toList());
                    return operations.stream()
                            .allMatch(operation -> {
                                // operationを抜く
                                Field fieldWithout = allMergedFieldWithoutT.freeze();
                                fieldWithout.reduce(operation.getMinoField());
                                long filledLineWithout = fieldWithout.getFilledLine();
                                long onePieceFilledKeyWithout = onePieceFilledKeyWithoutT & ~operation.getUsingKey();

                                // Tスピンではなくなるとき、必要なミノである
                                if (!SpinCommons.canTSpinWithFilledLine(fieldWithout, operationT)) {
                                    return true;
                                }

                                // Tスピンできる

                                // 宙に浮くミノがあるとき、必要なミノである
                                boolean existsOnGround = allOperations.stream()
                                        .allMatch(operation2 -> SpinCommons.existsOnGround(initField, fieldWithout, filledLineWithout, onePieceFilledKeyWithout, operation2));

                                if (!existsOnGround) {
                                    return true;
                                }

                                // 宙に浮くミノがない

                                // Tの回転入れに影響を与えるとき、必要なミノである
                                Field freeze = fieldWithout.freeze();
                                assert operationT.getNeedDeletedKey() == 0L || (filledLineWithout & operationT.getNeedDeletedKey()) != 0L;
                                freeze.clearLine();

                                int slideY = Long.bitCount(filledLineWithout & KeyOperators.getMaskForKeyBelowY(ty + mino.getMinY()));

                                return !rotateReachable.checks(freeze, mino, tx, ty - slideY, fieldHeight);
                            });
                })
                .map(roofResult -> new SimpleCandidate(roofResult.getLastResult(), roofResult.getOperationT()))
                ;
    }
}
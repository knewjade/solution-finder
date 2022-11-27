package common.cover;

import common.cover.reachable.ReachableForCoverWrapper;
import common.datastore.*;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.reachable.HarddropReachable;
import core.action.reachable.TSpinOrHarddropReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NormalCoverTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();

    private final ClearLinesCover cover = ClearLinesCover.createNormal();

    @Test
    void cansBuild1() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                "XXX_______" +
                "XXX_______" +
                "XXXXX_XXXX" +
                "XXXXX_XXXX"
        );
        List<Operation> operationList = Collections.singletonList(
                new SimpleOperation(Piece.L, Rotate.Reverse, 4, 3)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new HarddropReachable(minoFactory, minoShifter, height));

        {
            List<Piece> pieces = toPieceList("L");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("O");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("IL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    @Test
    void cansBuild2() {
        int height = 4;
        Field field = FieldFactory.createField(height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.I, Rotate.Spawn, 4, 0),
                new SimpleOperation(Piece.O, Rotate.Spawn, 4, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new HarddropReachable(minoFactory, minoShifter, height));

        {
            List<Piece> pieces = toPieceList("I");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("IO");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("OI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("TIO");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("IOTLJZS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("TOI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void dropHarddropOrTSD() {
        int height = 5;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.I, Rotate.Spawn, 4, 0),
                new SimpleOperation(Piece.L, Rotate.Right, 0, 1),
                new SimpleOperation(Piece.Z, Rotate.Spawn, 4, 1),
                new SimpleOperation(Piece.S, Rotate.Right, 6, 1),
                new SimpleOperation(Piece.O, Rotate.Spawn, 8, 0),
                new SimpleOperation(Piece.J, Rotate.Left, 9, 3),
                new SimpleOperation(Piece.T, Rotate.Reverse, 2, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        MinoRotation minoRotation = MinoRotation.create();
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, height, 2, true));

        {
            List<Piece> pieces = toPieceList("TILJSZO");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("TLSIZOJ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("SLIZOJT");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("LISZOTJ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    private List<Piece> toPieceList(String str) {
        return BlockInterpreter.parse(str).collect(Collectors.toList());
    }

    private List<MinoOperationWithKey> toMinoOperationWithKey(List<Operation> operationList, Field field, int height) {
        return OperationTransform.parseToOperationWithKeys(
                field, new Operations(operationList), minoFactory, height
        ).stream().map(m -> {
            Mino mino = minoFactory.create(m.getPiece(), m.getRotate());
            return new MinimalOperationWithKey(mino, m.getX(), m.getY(), m.getNeedDeletedKey());
        }).collect(Collectors.toList());
    }
}
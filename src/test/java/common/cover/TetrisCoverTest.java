package common.cover;

import common.cover.reachable.ReachableForCoverWrapper;
import common.datastore.*;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.reachable.ReachableFacade;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TetrisCoverTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

    @Test
    void cansBuild() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "___XXXXXXX" +
                        "_XXXXXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.Z, Rotate.Left, 3, 2),
                new SimpleOperation(Piece.J, Rotate.Right, 1, 2),
                new SimpleOperation(Piece.I, Rotate.Left, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height));

        TetrisCover cover = new TetrisCover();

        {
            List<Piece> pieces = toPieceList("JZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("ZJI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("JZI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("IJZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("ZJITOIS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    @Test
    void cansBuild2() {
        int height = 10;
        Field field = FieldFactory.createField("" +
                        "___XXXXX__" +
                        "__XXXXXXXX" +
                        "_XXXXXXX__" +
                        "_XXXXXXXXX" +
                        "_XXXXXXXXX" +
                        "_XXXXXXXXX" +
                        "_XXXXXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.S, Rotate.Spawn, 1, 5),
                new SimpleOperation(Piece.O, Rotate.Spawn, 8, 4),
                new SimpleOperation(Piece.I, Rotate.Left, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height));

        TetrisCover cover = new TetrisCover();

        {
            List<Piece> pieces = toPieceList("S");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("SIO");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("ISO");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("SOI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("OIS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
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
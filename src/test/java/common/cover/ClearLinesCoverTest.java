package common.cover;

import common.cover.reachable.ReachableForCoverWrapper;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.reachable.SoftdropTOnlyReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ClearLinesCoverTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

    @Test
    void cansBuildLessPieces() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.J, Rotate.Right, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        ClearLinesCover cover = ClearLinesCover.createEqualToOrGreaterThan(3, false);

        {
            List<Piece> pieces = toPieceList("LS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("LJ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void cansBuildEqualToOrGreaterThan3() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.J, Rotate.Right, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        ClearLinesCover cover = ClearLinesCover.createEqualToOrGreaterThan(3, false);

        {
            List<Piece> pieces = toPieceList("LSJ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("LJS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("OLJS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void cansBuildEqualToOrGreaterThan2() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.L, Rotate.Reverse, 2, 3),
                new SimpleOperation(Piece.I, Rotate.Left, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        ClearLinesCover cover = ClearLinesCover.createEqualToOrGreaterThan(2, false);

        {
            List<Piece> pieces = toPieceList("LSLI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("ILSL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("OILSL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void cansBuildEqualTo2() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.L, Rotate.Reverse, 2, 3),
                new SimpleOperation(Piece.I, Rotate.Left, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        ClearLinesCover cover = ClearLinesCover.createEqualTo(2, false);

        {
            List<Piece> pieces = toPieceList("LSIL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("LSLI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("TLSLI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void cansBuildEqualTo3AllowsPC() {
        int height = 5;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Right, 0, 3),
                new SimpleOperation(Piece.S, Rotate.Right, 1, 3),
                new SimpleOperation(Piece.L, Rotate.Left, 3, 3),
                new SimpleOperation(Piece.T, Rotate.Reverse, 2, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        {
            ClearLinesCover cover = ClearLinesCover.createEqualTo(3, false);

            {
                List<Piece> pieces = toPieceList("LSLT");

                assertThat(
                        cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isFalse();
                assertThat(
                        cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isFalse();
            }
        }

        {
            ClearLinesCover cover = ClearLinesCover.createEqualTo(3, true);

            {
                List<Piece> pieces = toPieceList("LSLT");

                assertThat(
                        cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isTrue();
                assertThat(
                        cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isTrue();
            }
            {
                List<Piece> pieces = toPieceList("LSTL");

                assertThat(
                        cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isFalse();
                assertThat(
                        cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isTrue();
            }
            {
                List<Piece> pieces = toPieceList("ILSTL");

                assertThat(
                        cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isFalse();
                assertThat(
                        cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
                ).isFalse();
            }
        }
    }

    @Test
    void cansBuildNoSoftdop() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.J, Rotate.Right, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        ClearLinesCover cover = ClearLinesCover.createEqualToOrGreaterThan(1, false, 0, Integer.MAX_VALUE);

        {
            List<Piece> pieces = toPieceList("SJL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("LJS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("LSJ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    @Test
    void cansBuildClearLineOnce() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.J, Rotate.Right, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        ClearLinesCover cover = ClearLinesCover.createEqualToOrGreaterThan(1, false, Integer.MAX_VALUE, 1);

        {
            List<Piece> pieces = toPieceList("SJL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("LJS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("LSJ");

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
        return new ArrayList<>(OperationTransform.parseToOperationWithKeys(
                field, new Operations(operationList), minoFactory, height
        ));
    }
}
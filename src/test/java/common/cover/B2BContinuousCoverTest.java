package common.cover;

import common.cover.reachable.ReachableForCoverWrapper;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.reachable.HarddropReachable;
import core.action.reachable.ReachableFacade;
import core.action.reachable.SoftdropTOnlyReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.FileMinoRotationFactory;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class B2BContinuousCoverTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

    private final B2BContinuousCover cover = new B2BContinuousCover(minoRotation, false);

    @Test
    void cansBuild1() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "______XXXX" +
                        "___XXXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Right, 0, 1),
                new SimpleOperation(Piece.Z, Rotate.Spawn, 4, 1),
                new SimpleOperation(Piece.T, Rotate.Reverse, 2, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        {
            List<Piece> pieces = toPieceList("LT");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("LZT");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("TLZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("OLTZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("LZTOIJS");

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
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "______XXXX" +
                        "XXX_XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Reverse, 6, 2),
                new SimpleOperation(Piece.S, Rotate.Spawn, 1, 1),
                new SimpleOperation(Piece.T, Rotate.Reverse, 3, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height, false));

        {
            List<Piece> pieces = toPieceList("LST");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("STL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("OSTL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void cansBuild3() {
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
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new HarddropReachable(minoFactory, minoShifter, height));

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
            List<Piece> pieces = toPieceList("ZIJ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    @Test
    void cansBuildUse180() {
        MinoRotation minoRotation180 = FileMinoRotationFactory.load(Paths.get("kicks/nullpomino180.properties")).create();
        B2BContinuousCover cover = new B2BContinuousCover(minoRotation180, true);

        int height = 5;
        Field field = FieldFactory.createField("" +
                        "_____XXXXX" +
                        "X____XXXXX" +
                        "X____XXXXX" +
                        "XX_____XXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.Z, Rotate.Spawn, 1, 2),
                new SimpleOperation(Piece.I, Rotate.Spawn, 4, 0),
                new SimpleOperation(Piece.T, Rotate.Reverse, 2, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(ReachableFacade.create180Locked(minoFactory, minoShifter, minoRotation180, height));

        {
            List<Piece> pieces = toPieceList("TOZI");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
        {
            List<Piece> pieces = toPieceList("ITZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
        {
            List<Piece> pieces = toPieceList("IZT");

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
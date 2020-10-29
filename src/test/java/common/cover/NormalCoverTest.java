package common.cover;

import common.datastore.*;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.reachable.HarddropReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NormalCoverTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();

    private final NormalCover cover = new NormalCover();

    @Test
    void cansBuild() {
        int height = 4;
        Field field = FieldFactory.createField(height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.I, Rotate.Spawn, 4, 0),
                new SimpleOperation(Piece.O, Rotate.Spawn, 4, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        HarddropReachable reachable = new HarddropReachable(minoFactory, minoShifter, height);

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
package common.parser;

import common.buildup.BuildUp;
import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OperationWithKeyInterpreterTest {
    @Test
    void parseToOperationWithKey() {
        Field initField = FieldFactory.createField(""
                + "____XXXXXX"
                + "____XXXXXX"
                + "____XXXXXX"
                + "____XXXXXX"
        );

        String base = "J,0,1,0,0,3;I,0,1,2,0,4;L,L,3,1,4,11;Z,0,1,1,4,10";
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationWithKeyInterpreter.parseToList(base, minoFactory);

        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, new MinoShifter(), SRSMinoRotationFactory.createDefault(), 8);
        assertThat(BuildUp.cansBuild(initField, operationWithKeys, 8, reachable)).isTrue();

        String line = OperationWithKeyInterpreter.parseToString(operationWithKeys);
        assertThat(line).isEqualTo(base);
    }

    @Test
    void parseRandom() {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        for (int size = 1; size < 20; size++) {
            List<OperationWithKey> operations = Stream.generate(() -> {
                Piece piece = randoms.block();
                Rotate rotate = randoms.rotate();
                int x = randoms.nextIntOpen(10);
                int y = randoms.nextIntOpen(4);
                long deleteKey = randoms.key();
                long usingKey = randoms.key();
                return new FullOperationWithKey(minoFactory.create(piece, rotate), x, y, deleteKey, usingKey);
            }).limit(size).collect(Collectors.toList());

            String str = OperationWithKeyInterpreter.parseToString(operations);
            List<MinoOperationWithKey> actual = OperationWithKeyInterpreter.parseToList(str, minoFactory);

            assertThat(actual).isEqualTo(operations);
        }
    }
}
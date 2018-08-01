package common.parser;

import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OperationWithKeyInterpreterTest {
    @Test
    void parseToOperationWithKey() throws Exception {
        String base = "J,0,1,0,0,1025;I,0,1,2,0,1048576;L,L,3,1,1048576,1073742849;J,0,1,3,0,1100585369600";
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationWithKeyInterpreter.parseToList(base, minoFactory);
        String line = OperationWithKeyInterpreter.parseToString(operationWithKeys);

        assertThat(line).isEqualTo(base);
    }

    @Test
    void parseRandom() throws Exception {
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
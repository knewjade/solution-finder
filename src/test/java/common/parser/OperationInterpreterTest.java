package common.parser;

import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import core.mino.Block;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OperationInterpreterTest {
    @Test
    void parseToOperations() throws Exception {
        String base = "T,0,1,0;L,2,1,2;I,L,3,1;J,2,1,3";
        Operations operations = OperationInterpreter.parseToOperations(base);
        String str = OperationInterpreter.parseToString(operations);
        assertThat(base).isEqualTo(str);
    }

    @Test
    void parseRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size < 20; size++) {
            List<Operation> operationList = Stream.generate(() -> {
                Block block = randoms.block();
                Rotate rotate = randoms.rotate();
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(4);
                return new SimpleOperation(block, rotate, x, y);
            }).limit(size).collect(Collectors.toList());

            Operations operations = new Operations(operationList);
            String str = OperationInterpreter.parseToString(operations);
            Operations actual = OperationInterpreter.parseToOperations(str);

            assertThat(actual).isEqualTo(operations);
        }
    }
}
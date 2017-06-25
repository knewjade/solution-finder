package common.parser;

import common.datastore.OperationWithKey;
import core.mino.MinoFactory;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OperationWithKeyInterpreterTest {
    @Test
    public void parseToOperationWithKey() throws Exception {
        String base = "J,0,1,0,0,1025;I,0,1,2,0,1048576;L,L,3,1,1048576,1073742849;J,0,1,3,0,1100585369600";
        MinoFactory minoFactory = new MinoFactory();
        List<OperationWithKey> operationWithKeys = OperationWithKeyInterpreter.parseToList(base, minoFactory);
        String line = OperationWithKeyInterpreter.parseToString(operationWithKeys);

        assertThat(line, is(base));
    }
}
package common.parser;

import common.datastore.OperationWithKey;
import common.datastore.Operations;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OperationTransformTest {
    @Test
    public void parseToOperationWithKeys() throws Exception {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );

        String base = "L,0,2,0;Z,R,2,2;O,0,0,1;L,2,1,1";
        Operations operations = OperationInterpreter.parseToOperations(base);
        MinoFactory minoFactory = new MinoFactory();
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, 4);

        String line = OperationWithKeyInterpreter.parseToString(operationWithKeys);
        assertThat(line, is("L,0,2,0,0,1025;Z,R,2,2,0,1074791424;O,0,0,1,0,1049600;L,2,1,1,1049600,1073741825"));
    }

    @Test
    public void parseToOperations() throws Exception {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );

        String base = "J,2,2,1;I,0,1,2;J,R,0,1;S,0,2,0";
        Operations operations = OperationInterpreter.parseToOperations(base);
        MinoFactory minoFactory = new MinoFactory();
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, 4);
        Operations restoreOperations = OperationTransform.parseToOperations(field, operationWithKeys, 4);

        assertThat(restoreOperations, is(operations));
    }
}
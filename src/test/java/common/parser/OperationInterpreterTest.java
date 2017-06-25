package common.parser;

import common.datastore.Operations;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class OperationInterpreterTest {
    @Test
    public void parseToOperations() throws Exception {
        String base = "T,0,1,0;L,2,1,2;I,L,3,1;J,2,1,3";
        Operations operations = OperationInterpreter.parseToOperations(base);
        String str = OperationInterpreter.parseToString(operations);
        assertThat(base, is(str));
    }
}
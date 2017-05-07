package entry.path;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import entry.searching_pieces.EnumeratePiecesCore;
import common.pattern.PiecesGenerator;
import org.junit.Test;
import common.datastore.Operations;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static common.OperationsFactory.createOperations;

// TODO: unittest pathを修正次第、内容を見直す
public class PathCoreTest {
    @Test
    public void invokeUsingHoldEasy1() throws Exception {
        // Field
        String marks = "" +
                "X____XXXXX" +
                "XX__XXXXXX" +
                "XX__XXXXXX" +
                "XXXXXXX__X" +
                "XXXXXXX__X" +
                "";
        int maxClearLine = 5;
        int maxDepth = 3;
        boolean isUsingHold = true;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PathCore pathCore = new PathCore(maxClearLine, executorService, core * 10);

        Field field = FieldFactory.createField(marks);

        PiecesGenerator generator = new PiecesGenerator("*p4");
        EnumeratePiecesCore enumeratePiecesCore = PathCore.createEnumeratePiecesCore(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePiecesCore.enumerate();
        pathCore.runForLayer1(field, blocks, maxClearLine, maxDepth);

        // Source: myself 20170423
        TreeSet<Operations> allOperations = pathCore.getAllOperations();
        assertThat(allOperations.size(), is(4));
        assertThat(allOperations, hasItems(
                createOperations("L,L,2,3", "J,R,3,3", "O,0,7,0"),
                createOperations("J,R,3,3", "L,L,2,3", "O,0,7,0"),
                createOperations("S,0,3,3", "Z,0,2,2", "O,0,7,0"),
                createOperations("Z,0,2,3", "S,0,3,2", "O,0,7,0")
        ));

        pathCore.runForLayer2(field, maxClearLine);
        assertThat(pathCore.getUniqueOperations().size(), is(3));
        assertThat(allOperations, hasItems(
                createOperations("L,L,2,3", "J,R,3,3", "O,0,7,0"),
                createOperations("S,0,3,3", "Z,0,2,2", "O,0,7,0"),
                createOperations("Z,0,2,3", "S,0,3,2", "O,0,7,0")
        ));

        pathCore.runForLayer3(field, maxClearLine, generator, isUsingHold);
        assertThat(pathCore.getUniqueOperations().size(), is(3));
        assertThat(allOperations, hasItems(
                createOperations("L,L,2,3", "J,R,3,3", "O,0,7,0"),
                createOperations("S,0,3,3", "Z,0,2,2", "O,0,7,0"),
                createOperations("Z,0,2,3", "S,0,3,2", "O,0,7,0")
        ));
    }

    @Test
    public void invokeUsingHoldWithTemplate() throws Exception {
        // Field
        String marks = "" +
                "XXXX____XX" +
                "XXXX___XXX" +
                "XXXX__XXXX" +
                "XXXX___XXX" +
                "";
        int maxClearLine = 4;
        int maxDepth = 3;
        boolean isUsingHold = true;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PathCore pathCore = new PathCore(maxClearLine, executorService, core * 10);

        Field field = FieldFactory.createField(marks);

        PiecesGenerator generator = new PiecesGenerator("*p4");
        EnumeratePiecesCore enumeratePiecesCore = PathCore.createEnumeratePiecesCore(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePiecesCore.enumerate();
        pathCore.runForLayer1(field, blocks, maxClearLine, maxDepth);
        pathCore.runForLayer2(field, maxClearLine);
        pathCore.runForLayer3(field, maxClearLine, generator, isUsingHold);

        // Source: myself 20170423
        assertThat(pathCore.getAllOperations().size(), is(39));
        assertThat(pathCore.getUniqueOperations().size(), is(18));
        assertThat(pathCore.getMinimalOperations().size(), is(16));
    }

    @Test
    public void invokeUsingHoldWitGraceSystem() throws Exception {
        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        int maxClearLine = 4;
        int maxDepth = 4;
        boolean isUsingHold = true;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PathCore pathCore = new PathCore(maxClearLine, executorService, core * 10);

        Field field = FieldFactory.createField(marks);

        PiecesGenerator generator = new PiecesGenerator("T, *p4");
        EnumeratePiecesCore enumeratePiecesCore = PathCore.createEnumeratePiecesCore(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePiecesCore.enumerate();
        pathCore.runForLayer1(field, blocks, maxClearLine, maxDepth);
        pathCore.runForLayer2(field, maxClearLine);
        pathCore.runForLayer3(field, maxClearLine, generator, isUsingHold);

        // Source: myself 20170426
        assertThat(pathCore.getAllOperations().size(), is(797));
        assertThat(pathCore.getUniqueOperations().size(), is(363));
        assertThat(pathCore.getMinimalOperations().size(), is(102));
    }
}
package entry.percent;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import entry.searching_pieces.NormalEnumeratePieces;
import misc.pattern.PiecesGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PercentCoreTest {
    @Test
    public void invokeUsingHoldJust() throws Exception {
        // Field
        String marks = "" +
                "XX________" +
                "XX________" +
                "XXX______X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "XXXXXXX_XX" +
                "";
        int maxClearLine = 6;
        int maxDepth = 7;
        boolean isUsingHold = true;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PercentCore percentCore = new PercentCore(maxClearLine, executorService, isUsingHold);

        Field field = FieldFactory.createField(marks);

        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces enumeratePieces = new NormalEnumeratePieces(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePieces.enumerate();
        percentCore.run(field, blocks, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(percentCore.getResultTree().getSuccessPercent(), is(5038 / 5040.0));
    }

    @Test
    public void invokeUsingHoldOver() throws Exception {
        // Field
        String marks = "" +
                "XX_____XXX" +
                "XXX____XXX" +
                "XXXX___XXX" +
                "XXX____XXX" +
                "";
        int maxClearLine = 4;
        int maxDepth = 4;
        boolean isUsingHold = true;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PercentCore percentCore = new PercentCore(maxClearLine, executorService, isUsingHold);

        Field field = FieldFactory.createField(marks);

        List<String> patterns = Arrays.asList(
                "I, T, [IOJLSZ]p3",
                "I, I, [TOJLSZ]p3",
                "I, O, [TIJLSZ]p3",
                "I, J, [TIOLSZ]p3",
                "I, L, [TIOJSZ]p3",
                "I, S, [TIOJLZ]p3",
                "I, Z, [TIOJLS]p3"
        );

        PiecesGenerator generator = new PiecesGenerator(patterns);
        NormalEnumeratePieces enumeratePieces = new NormalEnumeratePieces(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePieces.enumerate();
        percentCore.run(field, blocks, maxClearLine, maxDepth);

        // Source: Nilgiri: https://docs.google.com/spreadsheets/d/1bVY3t_X96xRmUL0qdgB9tViSIGenu6RMKX4RW7qWg8Y/edit#gid=0
        assertThat(percentCore.getResultTree().getSuccessPercent(), is(711 / 840.0));
    }

    @Test
    public void invokeNoHoldJust() throws Exception {
        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";
        int maxClearLine = 4;
        int maxDepth = 6;
        boolean isUsingHold = false;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PercentCore percentCore = new PercentCore(maxClearLine, executorService, isUsingHold);

        Field field = FieldFactory.createField(marks);

        PiecesGenerator generator = new PiecesGenerator("*p6");
        NormalEnumeratePieces enumeratePieces = new NormalEnumeratePieces(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePieces.enumerate();
        percentCore.run(field, blocks, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(percentCore.getResultTree().getSuccessPercent(), is(1439 / 5040.0));
    }

    @Test
    public void invokeNoHoldOver() throws Exception {
        // Field
        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        int maxClearLine = 4;
        int maxDepth = 6;
        boolean isUsingHold = false;

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PercentCore percentCore = new PercentCore(maxClearLine, executorService, isUsingHold);

        Field field = FieldFactory.createField(marks);

        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces enumeratePieces = new NormalEnumeratePieces(generator, maxDepth, isUsingHold);
        List<List<Block>> blocks = enumeratePieces.enumerate();
        percentCore.run(field, blocks, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(percentCore.getResultTree().getSuccessPercent(), is(727 / 5040.0));
    }
}
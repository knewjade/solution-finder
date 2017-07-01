package _experimental.unused;

import core.mino.Block;
import core.field.Field;
import core.field.FieldFactory;

import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;

public class CheckmateMain {
    public static void main(String[] args) throws Exception {
//        pattern1();
        pattern2();
    }

    private static void pattern1() {
        // Invoker
        List<Block> blocks = Arrays.asList(Block.I, Block.I, Block.J, Block.S);
        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        String marks = "" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        invoker.measure(field, blocks, 5000);
        invoker.clearStopwatch();

        invoker.measure(field, blocks, 20000);
        invoker.show(true);
    }

    private static void pattern2() {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, L, J, S, Z, J, L, T);
        int maxClearLine = 8;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        String marks = "" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        invoker.measure(field, blocks, 500);
        invoker.show(false);
        invoker.clearStopwatch();

        System.out.println("---");

        invoker.measure(field, blocks, 2000);
        invoker.show(true);
    }
}

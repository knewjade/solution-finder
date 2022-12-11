package entry.percent;

import common.datastore.blocks.LongPieces;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import concurrent.ILockedReachableThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.srs.MinoRotation;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import entry.searching_pieces.NormalEnumeratePieces;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class PercentCoreTest {
    private static class Obj {
        private final String marks;
        private final int maxClearLine;
        private final int maxDepth;
        private final boolean isUsingHold;
        private final List<String> patterns;
        private final boolean isSingleThread;

        private Obj(String marks, int maxClearLine, int maxDepth, boolean isUsingHold, String pattern) {
            this(marks, maxClearLine, maxDepth, isUsingHold, Collections.singletonList(pattern), false);
        }

        private Obj(String marks, int maxClearLine, int maxDepth, boolean isUsingHold, List<String> patterns) {
            this(marks, maxClearLine, maxDepth, isUsingHold, patterns, false);
        }

        private Obj(String marks, int maxClearLine, int maxDepth, boolean isUsingHold, List<String> patterns, boolean isSingleThread) {
            this.marks = marks;
            this.maxClearLine = maxClearLine;
            this.maxDepth = maxDepth;
            this.isUsingHold = isUsingHold;
            this.patterns = patterns;
            this.isSingleThread = isSingleThread;
        }
    }

    private void assertPercentCore(Obj obj, double successPercent) throws Exception {
        PatternGenerator generator = new LoadedPatternGenerator(obj.patterns);
        NormalEnumeratePieces enumeratePieces = new NormalEnumeratePieces(generator, obj.maxDepth, obj.isUsingHold);
        Set<LongPieces> blocks = enumeratePieces.enumerate();

        Optional<ExecutorService> executorService = obj.isSingleThread ? Optional.empty() : Optional.of(Executors.newCachedThreadPool());
        Supplier<MinoRotation> minoRotationSupplier = SRSMinoRotationFactory::createDefault;
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(minoRotationSupplier, obj.maxClearLine, false);
        ILockedReachableThreadLocal reachableThreadLocal = new ILockedReachableThreadLocal(minoRotationSupplier, obj.maxClearLine, false);
        MinoFactory minoFactory = new MinoFactory();

        PercentCore percentCore = getPercentCore(obj, executorService.orElse(null), candidateThreadLocal, reachableThreadLocal, minoFactory);
        Field field = FieldFactory.createField(obj.marks);

        percentCore.run(field, blocks, obj.maxClearLine, obj.maxDepth);

        executorService.ifPresent(ExecutorService::shutdown);

        assertThat(percentCore.getResultTree().getSuccessPercent()).isEqualTo(successPercent);
    }

    private PercentCore getPercentCore(Obj obj, ExecutorService executorService, LockedCandidateThreadLocal candidateThreadLocal, ILockedReachableThreadLocal reachableThreadLocal, MinoFactory minoFactory) {
        if (executorService == null)
            return new PercentCore(candidateThreadLocal, obj.isUsingHold, reachableThreadLocal, minoFactory);
        return new PercentCore(executorService, candidateThreadLocal, obj.isUsingHold, reachableThreadLocal, minoFactory);
    }

    @Test
    void invokeUsingHoldJust() throws Exception {
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
        String pattern = "*p7";

        // Source: myself 20170415
        Obj obj = new Obj(marks, maxClearLine, maxDepth, isUsingHold, pattern);
        assertPercentCore(obj, 5038 / 5040.0);
    }

    @Test
    void invokeUsingHoldOver() throws Exception {
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

        List<String> patterns = Arrays.asList(
                "I, T, [IOJLSZ]p3",
                "I, I, [TOJLSZ]p3",
                "I, O, [TIJLSZ]p3",
                "I, J, [TIOLSZ]p3",
                "I, L, [TIOJSZ]p3",
                "I, S, [TIOJLZ]p3",
                "I, Z, [TIOJLS]p3"
        );

        // Source: Nilgiri: https://docs.google.com/spreadsheets/d/1bVY3t_X96xRmUL0qdgB9tViSIGenu6RMKX4RW7qWg8Y/edit#gid=0
        Obj obj = new Obj(marks, maxClearLine, maxDepth, isUsingHold, patterns);
        assertPercentCore(obj, 711 / 840.0);
    }

    @Test
    void invokeNoHoldJust() throws Exception {
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
        String pattern = "*p6";

        // Source: reply in twitter from @fullfool_14
        Obj obj = new Obj(marks, maxClearLine, maxDepth, isUsingHold, pattern);
        assertPercentCore(obj, 1439 / 5040.0);
    }

    @Test
    void invokeNoHoldOver() throws Exception {
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
        String pattern = "*p7";

        // Source: reply in twitter from @fullfool_14
        Obj obj = new Obj(marks, maxClearLine, maxDepth, isUsingHold, pattern);
        assertPercentCore(obj, 727 / 5040.0);
    }

    @Test
    void invokeNoHoldInputMoreOver() throws Exception {
        // Field
        String marks = "" +
                "X___XXXXXX" +
                "XX_XXXXXXX" +
                "";
        int maxClearLine = 2;
        int maxDepth = 1;
        boolean isUsingHold = false;
        List<String> patterns = Arrays.asList("T,T", "Z,*");

        // 指定した探索は8個だが、必要以上に入れたミノは無視する
        // 警告を表示する代わりに、これは仕様とする
        Obj obj = new Obj(marks, maxClearLine, maxDepth, isUsingHold, patterns, false);
        assertPercentCore(obj, 1 / 2.0);
    }
}
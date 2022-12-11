package concurrent.checker.invoker;

import common.SyntaxException;
import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import common.tree.AnalyzeTree;
import concurrent.ILockedReachableThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.invoker.no_hold.ConcurrentCheckerNoHoldInvoker;
import concurrent.checker.invoker.no_hold.SingleCheckerNoHoldInvoker;
import core.action.candidate.Candidate;
import core.action.candidate.CandidateFacade;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import exceptions.FinderExecuteException;
import lib.Randoms;
import module.LongTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import searcher.checker.CheckerNoHold;
import searcher.common.validator.PerfectValidator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CheckerNoHoldInvokerTest {
    private AnalyzeTree runTestCase(ConcurrentCheckerInvoker invoker, String marks, PatternGenerator blocksGenerator, int maxClearLine, int maxDepth) throws FinderExecuteException {
        Field field = FieldFactory.createField(marks);
        List<Pieces> searchingPieces = blocksGenerator.blocksStream().collect(Collectors.toList());
        return runTestCase(invoker, field, searchingPieces, maxClearLine, maxDepth);
    }

    private AnalyzeTree runTestCase(ConcurrentCheckerInvoker invoker, Field field, List<Pieces> searchingPieces, int maxClearLine, int maxDepth) throws FinderExecuteException {
        List<Pair<Pieces, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
        for (Pair<Pieces, Boolean> resultPair : resultPairs) {
            Pieces pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

//        System.out.println(tree.show());

        return tree;
    }

    private static final ExecutorService executorService;

    static {
        int core = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(core);
    }

    @AfterAll
    static void tearDownAll() {
        executorService.shutdown();
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch1(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 8;
        int maxDepth = 7;

        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXXXX__" +
                "XXXXXXXX__" +
                "";

        // 結果を集計する
        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170616
        assertThat(tree.getSuccessPercent()).isEqualTo(1719 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch2BT4_5(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 6;
        int maxDepth = 7;

        String marks = "" +
                "XX________" +
                "XX________" +
                "XXX______X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "XXXXXXX_XX" +
                "";

        // 結果を集計する
        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170616
        assertThat(tree.getSuccessPercent()).isEqualTo(2228 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch3(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 7;

        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        // 結果を集計する
        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(727 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch4(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p4");
        int maxClearLine = 3;
        int maxDepth = 3;

        String marks = "" +
                "XXX____XXX" +
                "XXX_____XX" +
                "XXXX___XXX" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(120 / 840.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch5(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("J, I, Z, *p4");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "________XX" +
                "XX_____XXX" +
                "XXX_____XX" +
                "XXX______X" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(180 / 840.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch6(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 4;

        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent()).isEqualTo(516 / 2520.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch7(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 4;

        String marks = "" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent()).isEqualTo(396 / 2520.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch8(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 4;

        String marks = "" +
                "XXXXXX____" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent()).isEqualTo(609 / 2520.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch9(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 4;

        String marks = "" +
                "XXXXXX____" +
                "XXXXX_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: twitter by @???
        assertThat(tree.getSuccessPercent()).isEqualTo(324 / 2520.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch10(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent()).isEqualTo(1439 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testCase11(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        // Invoker
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p4");
        int maxClearLine = 5;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "____XXXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "___XXXXXXX" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent()).isEqualTo(477 / 2520.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testCase12(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        // Invoker
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent()).isEqualTo(727 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testCase13(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        // Invoker
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(1902 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testCase14(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        // Invoker
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX_____X" +
                "XXXXXXXXXX" +
                "XXXX_____X" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(309 / 2520.0);
    }

    @LongTest
    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void random(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws FinderExecuteException, SyntaxException {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        PerfectValidator validator = new PerfectValidator();
        CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

        for (int count = 0; count < 10; count++) {
            int maxClearLine = randoms.nextIntOpen(3, 6);
            int maxDepth = randoms.nextIntClosed(3, 5);
            Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
            Field field = randoms.field(maxClearLine, maxDepth);

            PatternGenerator blocksGenerator = createPiecesGenerator(maxDepth);
            List<Pieces> searchingPieces = blocksGenerator.blocksStream().collect(Collectors.toList());

            ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
            AnalyzeTree tree = runTestCase(invoker, field, searchingPieces, maxClearLine, maxDepth);

            for (Pieces pieces : searchingPieces) {
                boolean check = checker.check(field, pieces.getPieces(), candidate, maxClearLine, maxDepth);
                assertThat(tree.isSucceed(pieces)).isEqualTo(check);
            }
        }
    }

    private static class InvokerTestCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            IntFunction<ConcurrentCheckerInvoker> concurrentGenerator = this::createConcurrentCheckerUsingHoldInvoker;
            IntFunction<ConcurrentCheckerInvoker> singleGenerator = this::createSingleCheckerNoHoldInvoker;
            return Stream.of(Arguments.of(concurrentGenerator), Arguments.of(singleGenerator));
        }

        private ConcurrentCheckerInvoker createConcurrentCheckerUsingHoldInvoker(int maxClearLine) {
            MinoFactory minoFactory = new MinoFactory();
            CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
            Supplier<MinoRotation> minoRotationSupplier = SRSMinoRotationFactory::createDefault;
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(minoRotationSupplier, maxClearLine, false);
            ILockedReachableThreadLocal reachableThreadLocal = new ILockedReachableThreadLocal(minoRotationSupplier, maxClearLine, false);
            CheckerCommonObj commonObj = new CheckerCommonObj(minoFactory, candidateThreadLocal, checkerThreadLocal, reachableThreadLocal);
            return new ConcurrentCheckerNoHoldInvoker(executorService, commonObj);
        }

        private ConcurrentCheckerInvoker createSingleCheckerNoHoldInvoker(int maxClearLine) {
            MinoFactory minoFactory = new MinoFactory();
            CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
            Supplier<MinoRotation> minoRotationSupplier = SRSMinoRotationFactory::createDefault;
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(minoRotationSupplier, maxClearLine, false);
            ILockedReachableThreadLocal reachableThreadLocal = new ILockedReachableThreadLocal(minoRotationSupplier, maxClearLine, false);
            CheckerCommonObj commonObj = new CheckerCommonObj(minoFactory, candidateThreadLocal, checkerThreadLocal, reachableThreadLocal);
            return new SingleCheckerNoHoldInvoker(commonObj);
        }
    }

    private PatternGenerator createPiecesGenerator(int maxDepth) throws SyntaxException {
        switch (maxDepth) {
            case 3:
                return new LoadedPatternGenerator("*, *p3");
            case 4:
                return new LoadedPatternGenerator("*, *p4");
            case 5:
                return new LoadedPatternGenerator("*, *p5");
            case 6:
                return new LoadedPatternGenerator("*, *p6");
        }
        throw new UnsupportedOperationException();
    }
}
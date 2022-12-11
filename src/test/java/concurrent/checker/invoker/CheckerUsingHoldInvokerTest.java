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
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import concurrent.checker.invoker.using_hold.SingleCheckerUsingHoldInvoker;
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
import searcher.checker.CheckerUsingHold;
import searcher.common.validator.PerfectValidator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CheckerUsingHoldInvokerTest {
    private AnalyzeTree runTestCase(ConcurrentCheckerInvoker invoker, String marks, PatternGenerator blocksGenerator, int maxClearLine, int maxDepth) throws FinderExecuteException {
        List<Pieces> searchingPieces = blocksGenerator.blocksStream().collect(Collectors.toList());
        Field field = FieldFactory.createField(marks);
        List<Pair<Pieces, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
        for (Pair<Pieces, Boolean> resultPair : resultPairs) {
            Pieces pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

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

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(5032 / 5040.0);
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

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(5038 / 5040.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch3(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(4736 / 5040.0);
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
        assertThat(tree.getSuccessPercent()).isEqualTo(434 / 840.0);
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
        assertThat(tree.getSuccessPercent()).isEqualTo(771 / 840.0);
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
        assertThat(tree.getSuccessPercent()).isEqualTo(1776 / 2520.0);
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
        assertThat(tree.getSuccessPercent()).isEqualTo(1672 / 2520.0);
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
        assertThat(tree.getSuccessPercent()).isEqualTo(1928 / 2520.0);
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
        assertThat(tree.getSuccessPercent()).isEqualTo(1700 / 2520.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch10(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("T, *p4");
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

        // Source: Nilgiri: https://docs.google.com/spreadsheets/d/1bVY3t_X96xRmUL0qdgB9tViSIGenu6RMKX4RW7qWg8Y/edit#gid=0
        assertThat(tree.getSuccessPercent()).isEqualTo(744 / 840.0);
    }

    @ParameterizedTest
    @ArgumentsSource(InvokerTestCase.class)
    void testSearch11(IntFunction<ConcurrentCheckerInvoker> invokerGenerator) throws Exception {
        PatternGenerator blocksGenerator = new LoadedPatternGenerator("*p5");
        int maxClearLine = 4;
        int maxDepth = 4;

        String marks = "" +
                "XXXX______" +
                "XXXXXXXXXX" +
                "XXXXX_____" +
                "XXXX_____X" +
                "";

        ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
        AnalyzeTree tree = runTestCase(invoker, marks, blocksGenerator, maxClearLine, maxDepth);

        // Source: myself 20170707
        assertThat(tree.getSuccessPercent()).isEqualTo(1758 / 2520.0);
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
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int count = 0; count < 7; count++) {
            int maxClearLine = randoms.nextIntOpen(3, 6);
            int maxDepth = randoms.nextIntClosed(3, 5);
            Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
            Field field = randoms.field(maxClearLine, maxDepth);

            PatternGenerator blocksGenerator = createPiecesGenerator(maxDepth);
            List<Pieces> searchingPieces = blocksGenerator.blocksStream().collect(Collectors.toList());

            ConcurrentCheckerInvoker invoker = invokerGenerator.apply(maxClearLine);
            List<Pair<Pieces, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

            // 結果を集計する
            AnalyzeTree tree = new AnalyzeTree();
            for (Pair<Pieces, Boolean> resultPair : resultPairs) {
                Pieces pieces1 = resultPair.getKey();
                Boolean result = resultPair.getValue();
                tree.set(result, pieces1);
            }

            for (Pieces pieces : searchingPieces) {
                boolean check = checker.check(field, pieces.getPieces(), candidate, maxClearLine, maxDepth);
                assertThat(tree.isSucceed(pieces)).isEqualTo(check);
            }
        }

        executorService.shutdown();
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

    private static class InvokerTestCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            IntFunction<ConcurrentCheckerInvoker> concurrentGenerator = this::createConcurrentCheckerUsingHoldInvoker;
            IntFunction<ConcurrentCheckerInvoker> singleGenerator = this::createSingleCheckerUsingHoldInvoker;
            return Stream.of(Arguments.of(concurrentGenerator), Arguments.of(singleGenerator));
        }

        private ConcurrentCheckerInvoker createConcurrentCheckerUsingHoldInvoker(int maxClearLine) {
            MinoFactory minoFactory = new MinoFactory();
            CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
            Supplier<MinoRotation> minoRotationSupplier = SRSMinoRotationFactory::createDefault;
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(minoRotationSupplier, maxClearLine, false);
            ILockedReachableThreadLocal reachableThreadLocal = new ILockedReachableThreadLocal(minoRotationSupplier, maxClearLine, false);
            CheckerCommonObj commonObj = new CheckerCommonObj(minoFactory, candidateThreadLocal, checkerThreadLocal, reachableThreadLocal);
            return new ConcurrentCheckerUsingHoldInvoker(executorService, commonObj);
        }

        private ConcurrentCheckerInvoker createSingleCheckerUsingHoldInvoker(int maxClearLine) {
            MinoFactory minoFactory = new MinoFactory();
            CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
            Supplier<MinoRotation> minoRotationSupplier = SRSMinoRotationFactory::createDefault;
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(minoRotationSupplier, maxClearLine, false);
            ILockedReachableThreadLocal reachableThreadLocal = new ILockedReachableThreadLocal(minoRotationSupplier, maxClearLine, false);
            CheckerCommonObj commonObj = new CheckerCommonObj(minoFactory, candidateThreadLocal, checkerThreadLocal, reachableThreadLocal);
            return new SingleCheckerUsingHoldInvoker(commonObj);
        }
    }
}
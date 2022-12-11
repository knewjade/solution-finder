package searcher.pack.task;

import common.SyntaxException;
import common.buildup.BuildUpStream;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import concurrent.ILockedReachableThreadLocal;
import core.action.candidate.Candidate;
import core.action.candidate.CandidateFacade;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Randoms;
import module.LongTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import searcher.checker.CheckerNoHold;
import searcher.common.validator.PerfectValidator;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.FilterOnDemandBasicSolutions;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.solutions.OnDemandBasicSolutions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PackSearcherTest {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

    private BasicSolutions createMappedBasicSolutions(SizedBit sizedBit) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        return new MappedBasicSolutions(calculate);
    }

    private BasicSolutions createOnDemandBasicSolutions(SizedBit sizedBit) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        return new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);
    }

    private BasicSolutions createOnDemandBasicSolutions(SizedBit sizedBit, ColumnSmallField field) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        return new OnDemandBasicSolutions(separableMinos, sizedBit, field, memorizedPredicate);
    }

    private SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        ILockedReachableThreadLocal lockedReachableThreadLocal = new ILockedReachableThreadLocal(minoRotation, sizedBit.getHeight(), false);
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }

    private BasicSolutions createFilterOnDemandBasicSolutions(SizedBit sizedBit, SolutionFilter solutionFilter) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        return new FilterOnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate, solutionFilter);
    }

    private BasicSolutions createFilterOnDemandBasicSolutions(SizedBit sizedBit, ColumnSmallField field, SolutionFilter solutionFilter) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        return new FilterOnDemandBasicSolutions(separableMinos, sizedBit, field, memorizedPredicate, solutionFilter);
    }

    @Nested
    class Square {
        // width = expected_count
        // SizedBit=3x4用の最後まで探索した解の個数
        // デグレ確認用
        private final HashSet<Pair<Integer, Long>> ALL3x4_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
            {
                add(new Pair<>(4, 840L));
                add(new Pair<>(5, 6953L));
                add(new Pair<>(6, 53418L));
                add(new Pair<>(7, 388293L));
//                add(new Pair<>(8, 3195227L));
            }
        };

        // SizedBit=2x4用の最後まで探索した解の個数
        // デグレ確認用
        private final HashSet<Pair<Integer, Long>> ALL2x4_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
            {
                add(new Pair<>(4, 956L));
                add(new Pair<>(5, 7043L));
                add(new Pair<>(6, 60416L));
                add(new Pair<>(7, 437597L));
//                add(new Pair<>(8, 3615403L));
            }
        };

        private final HashSet<Pair<Integer, Long>> SRS_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
            {
                add(new Pair<>(4, 424L));
                add(new Pair<>(5, 2602L));
                add(new Pair<>(6, 16944L));
                add(new Pair<>(7, 103465L));
//                add(new Pair<>(8, 634634L));
            }
        };

        @Test
        void packAllCandidate1() throws ExecutionException, InterruptedException {
            // ALL: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=Mapped
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);

            // Create
            BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
            TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
            SolutionFilter solutionFilter = new AllPassedSolutionFilter();

            // Assert
            assertAllCandidates(sizedBit, basicSolutions, taskResultHelper, solutionFilter, ALL3x4_WIDTH_EXPECT);
        }

        @Test
        void packAllCandidate2() throws ExecutionException, InterruptedException {
            // ALL: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=OnDemand
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);

            // Create
            BasicSolutions basicSolutions = createOnDemandBasicSolutions(sizedBit);
            TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
            SolutionFilter solutionFilter = new AllPassedSolutionFilter();

            // Assert
            assertAllCandidates(sizedBit, basicSolutions, taskResultHelper, solutionFilter, ALL3x4_WIDTH_EXPECT);
        }

        @Test
        void packAllCandidate3() throws ExecutionException, InterruptedException {
            // ALL: SizedBit=2x4, TaskResultHelper=Basic, BasicSolutions=Mapped
            int width = 2;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);

            // Create
            BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
            TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();
            SolutionFilter solutionFilter = new AllPassedSolutionFilter();

            // Assert
            assertAllCandidates(sizedBit, basicSolutions, taskResultHelper, solutionFilter, ALL2x4_WIDTH_EXPECT);
        }

        private void assertAllCandidates(SizedBit sizedBit, BasicSolutions basicSolutions, TaskResultHelper taskResultHelper, SolutionFilter solutionFilter, HashSet<Pair<Integer, Long>> exceptPairs) throws InterruptedException, ExecutionException {
            for (Pair<Integer, Long> pair : exceptPairs) {
                Integer emptyWidth = pair.getKey();
                Long expectedCount = pair.getValue();
                Field initField = createSquareEmptyField(emptyWidth, sizedBit.getHeight());
                List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

                PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
                assertThat(searcher.count()).isEqualTo(expectedCount);
            }
        }

        private Field createSquareEmptyField(int emptyWidth, int emptyHeight) {
            Field field = FieldFactory.createField(emptyHeight);
            for (int x = emptyWidth; x < FIELD_WIDTH; x++)
                for (int y = 0; y < emptyHeight; y++)
                    field.setBlock(x, y);
            return field;
        }

        @Test
        @LongTest
        void packSRSCandidate1() throws ExecutionException, InterruptedException {
            // SRS: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=Mapped
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);

            // Create
            BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
            TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

            // Assert
            assertSRSCandidates(sizedBit, basicSolutions, taskResultHelper, SRS_WIDTH_EXPECT);
        }

        @Test
        void packSRSCandidate2() throws ExecutionException, InterruptedException {
            // SRS: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=OnDemand
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);

            // Create
            BasicSolutions basicSolutions = createOnDemandBasicSolutions(sizedBit);
            TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

            // Assert
            assertSRSCandidates(sizedBit, basicSolutions, taskResultHelper, SRS_WIDTH_EXPECT);
        }

        @Test
        @LongTest
        void packSRSCandidate3() throws ExecutionException, InterruptedException {
            // SRS: SizedBit=2x4, TaskResultHelper=Basic, BasicSolutions=Mapped
            int width = 2;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);

            // Create
            BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
            TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();

            // Assert
            assertSRSCandidates(sizedBit, basicSolutions, taskResultHelper, SRS_WIDTH_EXPECT);
        }

        private void assertSRSCandidates(SizedBit sizedBit, BasicSolutions basicSolutions, TaskResultHelper taskResultHelper, HashSet<Pair<Integer, Long>> exceptPairs) throws InterruptedException, ExecutionException {
            for (Pair<Integer, Long> pair : exceptPairs) {
                Integer emptyWidth = pair.getKey();
                Field initField = createSquareEmptyField(emptyWidth, sizedBit.getHeight());
                List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

                SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);
                PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
                Long expectedCount = pair.getValue();
                assertThat(searcher.count()).isEqualTo(expectedCount);
            }
        }
    }

    private PatternGenerator createPiecesGenerator(int maxDepth) throws SyntaxException {
        switch (maxDepth) {
            case 3:
                return new LoadedPatternGenerator("*, *p2");
            case 4:
                return new LoadedPatternGenerator("*, *p3");
            case 5:
                return new LoadedPatternGenerator("*, *p4");
            case 6:
                return new LoadedPatternGenerator("*, *p5");
        }
        throw new UnsupportedOperationException();
    }

    private void assertHeight4(SizedBit sizedBit, int maxCount, BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier) throws ExecutionException, InterruptedException, SyntaxException {
        assert sizedBit.getWidth() == 3;
        assert sizedBit.getHeight() == 4;

        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        Randoms randoms = new Randoms();

        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        for (int count = 0; count < maxCount; count++) {
            // Field
            int maxDepth = randoms.nextIntClosed(3, 6);
            Field initField = randoms.field(height, maxDepth);
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);
            SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);

            // Pack
            BasicSolutions basicSolutions = basicSolutionSupplier.apply(initField, solutionFilter);
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            List<Result> results = searcher.toList();

            // Possible
            HashSet<Pieces> possiblePieces = new HashSet<>();
            for (Result result : results) {
                // result to possible pieces
                List<MinoOperationWithKey> operationWithKeys = result.getMemento()
                        .getSeparableMinoStream(width)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toList());

                Set<LongPieces> sets = new BuildUpStream(reachable, height).existsValidBuildPattern(initField, operationWithKeys)
                        .map(keys -> keys.stream().map(OperationWithKey::getPiece))
                        .map(LongPieces::new)
                        .collect(Collectors.toSet());
                possiblePieces.addAll(sets);
            }

            // Checker
            PerfectValidator validator = new PerfectValidator();
            CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

            // Assert generator
            PatternGenerator generator = createPiecesGenerator(maxDepth);
            generator.blocksStream()
                    .forEach(blocks -> {
                        List<Piece> pieceList = blocks.getPieces();
                        boolean check = checker.check(initField, pieceList, candidate, height, maxDepth);
                        assertThat(possiblePieces.contains(blocks))
                                .as(pieceList.toString())
                                .isEqualTo(check);
                    });
        }
    }

    void assertHeight5(SizedBit sizedBit, int maxCount, BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier) throws ExecutionException, InterruptedException, SyntaxException {
        assert sizedBit.getWidth() == 2;
        assert sizedBit.getHeight() == 5;

        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        Randoms randoms = new Randoms();

        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();

        for (int count = 0; count < maxCount; count++) {
            // Field
            int maxDepth = randoms.nextIntClosed(3, 6);
            Field initField = randoms.field(height, maxDepth);
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);
            SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);

            // Pack
            BasicSolutions basicSolutions = basicSolutionSupplier.apply(initField, solutionFilter);
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            List<Result> results = searcher.toList();

            // Possible
            HashSet<Pieces> possiblePieces = new HashSet<>();
            for (Result result : results) {
                // result to possible pieces
                List<MinoOperationWithKey> operationWithKeys = result.getMemento()
                        .getSeparableMinoStream(width)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toList());

                Set<LongPieces> sets = new BuildUpStream(reachable, height).existsValidBuildPattern(initField, operationWithKeys)
                        .map(keys -> keys.stream().map(OperationWithKey::getPiece))
                        .map(LongPieces::new)
                        .collect(Collectors.toSet());
                possiblePieces.addAll(sets);
            }

            // Checker
            PerfectValidator validator = new PerfectValidator();
            CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

            // Assert generator
            PatternGenerator generator = createPiecesGenerator(maxDepth);
            generator.blocksStream()
                    .forEach(blocks -> {
                        List<Piece> pieceList = blocks.getPieces();
                        boolean check = checker.check(initField, pieceList, candidate, height, maxDepth);
                        assertThat(possiblePieces.contains(blocks))
                                .as(pieceList.toString())
                                .isEqualTo(check);
                    });
        }
    }

    @Nested
    class MappedRandomTestCase {
        @Test
        void height4() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> createMappedBasicSolutions(sizedBit);
            assertHeight4(sizedBit, 5, basicSolutionSupplier);
        }

        @Test
        void height5() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 2;
            int height = 5;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> createMappedBasicSolutions(sizedBit);
            assertHeight5(sizedBit, 5, basicSolutionSupplier);
        }
    }

    @Nested
    class OnDemandRandomTestCase {
        @Test
        void height4() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> createOnDemandBasicSolutions(sizedBit);
            assertHeight4(sizedBit, 5, basicSolutionSupplier);
        }

        @Test
        void height5() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 2;
            int height = 5;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> createOnDemandBasicSolutions(sizedBit);
            assertHeight5(sizedBit, 5, basicSolutionSupplier);
        }
    }

    @Nested
    class LimitOnDemandRandomTestCase {
        @Test
        void height4() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> {
                ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
                return createOnDemandBasicSolutions(sizedBit, maxOuterBoard);
            };
            assertHeight4(sizedBit, 5, basicSolutionSupplier);
        }

        @Test
        void height5() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 2;
            int height = 5;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> {
                ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
                return createOnDemandBasicSolutions(sizedBit, maxOuterBoard);
            };
            assertHeight5(sizedBit, 5, basicSolutionSupplier);
        }
    }

    @Nested
    class FilterOnDemandRandomTestCase {
        @Test
        void height4() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> createFilterOnDemandBasicSolutions(sizedBit, solutionFilter);
            assertHeight4(sizedBit, 5, basicSolutionSupplier);
        }

        @Test
        void height5() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 2;
            int height = 5;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> createFilterOnDemandBasicSolutions(sizedBit, solutionFilter);
            assertHeight5(sizedBit, 5, basicSolutionSupplier);
        }
    }

    @Nested
    class LimitFilterOnDemandRandomTestCase {
        @Test
        void height4() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> {
                ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
                return createFilterOnDemandBasicSolutions(sizedBit, maxOuterBoard, solutionFilter);
            };
            assertHeight4(sizedBit, 5, basicSolutionSupplier);
        }

        @Test
        void height5() throws ExecutionException, InterruptedException, SyntaxException {
            int width = 2;
            int height = 5;
            SizedBit sizedBit = new SizedBit(width, height);
            BiFunction<Field, SolutionFilter, BasicSolutions> basicSolutionSupplier = (field, solutionFilter) -> {
                ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
                return createFilterOnDemandBasicSolutions(sizedBit, maxOuterBoard, solutionFilter);
            };
            assertHeight5(sizedBit, 5, basicSolutionSupplier);
        }
    }
}

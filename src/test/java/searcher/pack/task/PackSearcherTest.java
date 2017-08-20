package searcher.pack.task;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.pieces.LongBlocks;
import common.datastore.pieces.Blocks;
import common.pattern.BlocksGenerator;
import concurrent.LockedReachableThreadLocal;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Randoms;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
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
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.solutions.OnDemandBasicSolutions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PackSearcherTest {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = new MinoRotation();

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

    private SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
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
                add(new Pair<>(8, 3195227L));
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
                add(new Pair<>(8, 3615403L));
            }
        };

        private final HashSet<Pair<Integer, Long>> SRS_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
            {
                add(new Pair<>(4, 424L));
                add(new Pair<>(5, 2602L));
                add(new Pair<>(6, 16944L));
                add(new Pair<>(7, 103465L));
                add(new Pair<>(8, 634634L));
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

                PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
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
        @Tag("long")
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
        @Tag("long")
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
                PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
                Long expectedCount = pair.getValue();
                assertThat(searcher.count()).isEqualTo(expectedCount);
            }
        }
    }

    @Nested
    class RandomChecker {
        @Test
        void height4() throws ExecutionException, InterruptedException {
            int width = 3;
            int height = 4;
            SizedBit sizedBit = new SizedBit(width, height);
            Randoms randoms = new Randoms();

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);
            LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

            TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

            for (int count = 0; count < 20; count++) {
                BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);

                // Field
                int maxDepth = randoms.nextIntClosed(3, 6);
                Field initField = randoms.field(height, maxDepth);
                List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);
                SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);

                // Pack
                PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
                List<Result> results = searcher.toList();

                // Possible
                HashSet<Blocks> possiblePieces = new HashSet<>();
                for (Result result : results) {
                    // result to possible pieces
                    List<OperationWithKey> operationWithKeys = result.getMemento().getOperationsStream(width).collect(Collectors.toList());
                    Set<LongBlocks> sets = new BuildUpStream(reachable, height).existsValidBuildPattern(initField, operationWithKeys)
                            .map(keys -> keys.stream().map(OperationWithKey::getMino).map(Mino::getBlock))
                            .map(LongBlocks::new)
                            .collect(Collectors.toSet());
                    possiblePieces.addAll(sets);
                }

                // Checker
                PerfectValidator validator = new PerfectValidator();
                CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

                // Assert generator
                BlocksGenerator generator = createPiecesGenerator(maxDepth);
                for (Blocks pieces : generator) {
                    List<Block> blocks = pieces.getBlockList();
                    boolean check = checker.check(initField, blocks, candidate, height, maxDepth);
                    assertThat(possiblePieces.contains(pieces))
                            .as(blocks.toString())
                            .isEqualTo(check);
                }
            }
        }

        @Test
        void height5() throws ExecutionException, InterruptedException {
            int width = 2;
            int height = 5;
            SizedBit sizedBit = new SizedBit(width, height);
            Randoms randoms = new Randoms();

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);
            LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

            TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();

            for (int count = 0; count < 20; count++) {
                BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);

                // Field
                int maxDepth = randoms.nextIntClosed(3, 6);
                Field initField = randoms.field(height, maxDepth);
                List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);
                SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);

                // Pack
                PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
                List<Result> results = searcher.toList();

                // Possible
                HashSet<Blocks> possiblePieces = new HashSet<>();
                for (Result result : results) {
                    // result to possible pieces
                    List<OperationWithKey> operationWithKeys = result.getMemento().getOperationsStream(width).collect(Collectors.toList());
                    Set<LongBlocks> sets = new BuildUpStream(reachable, height).existsValidBuildPattern(initField, operationWithKeys)
                            .map(keys -> keys.stream().map(OperationWithKey::getMino).map(Mino::getBlock))
                            .map(LongBlocks::new)
                            .collect(Collectors.toSet());
                    possiblePieces.addAll(sets);
                }

                // Checker
                PerfectValidator validator = new PerfectValidator();
                CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

                // Assert generator
                BlocksGenerator generator = createPiecesGenerator(maxDepth);
                for (Blocks pieces : generator) {
                    List<Block> blocks = pieces.getBlockList();
                    boolean check = checker.check(initField, blocks, candidate, height, maxDepth);
                    assertThat(possiblePieces.contains(pieces))
                            .as(blocks.toString())
                            .isEqualTo(check);
                }
            }
        }

        private BlocksGenerator createPiecesGenerator(int maxDepth) {
            switch (maxDepth) {
                case 3:
                    return new BlocksGenerator("*, *p2");
                case 4:
                    return new BlocksGenerator("*, *p3");
                case 5:
                    return new BlocksGenerator("*, *p4");
                case 6:
                    return new BlocksGenerator("*, *p5");
            }
            throw new UnsupportedOperationException();
        }
    }
}

package common.buildup;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.iterable.PermutationIterable;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import concurrent.ILockedReachableThreadLocal;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Randoms;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PerfectPackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class BuildUpStreamTest {
    @Test
    void buildUp1() {
        // Create LockedReachable
        int height = 4;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        // Create OperationWithKey List
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX"
        );
        Operations operations = OperationInterpreter.parseToOperations("J,0,1,0;S,L,1,2;O,0,2,1;J,2,2,1");
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // Create Pieces
        Set<String> valid = new BuildUpStream(reachable, height)
                .existsValidBuildPattern(field, operationWithKeys)
                .map(op -> op.stream().map(OperationWithKey::getPiece).map(Piece::name).collect(Collectors.joining()))
                .collect(Collectors.toSet());

        // Assertion
        assertThat(valid)
                .hasSize(2)
                .contains("JSOJ", "JOSJ");
    }

    @Test
    void buildUp2() {
        // Create LockedReachable
        int height = 4;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        // Create OperationWithKey List
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX"
        );
        Operations operations = OperationInterpreter.parseToOperations("J,R,0,1;O,0,1,0;L,L,3,1;I,0,1,0");
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // Create Pieces
        Set<String> valid = new BuildUpStream(reachable, height)
                .existsValidBuildPattern(field, operationWithKeys)
                .map(op -> op.stream().map(OperationWithKey::getPiece).map(Piece::name).collect(Collectors.joining()))
                .collect(Collectors.toSet());

        // Assertion
        assertThat(valid)
                .hasSize(16)
                .doesNotContain("IJOL", "IJLO", "IOJL", "IOLJ", "ILOJ", "ILJO")  // starts I
                .doesNotContain("OIJL", "OILJ")  // starts OI
                .contains("OJLI", "OJIL", "JOIL", "JLIO", "LOJI", "LIOJ");
    }

    @Test
    void randomShort() throws ExecutionException, InterruptedException {
        // Initialize
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        // Define size
        int height = 4;
        int basicWidth = 3;
        SizedBit sizedBit = new SizedBit(basicWidth, height);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        // Create basic solutions
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        ILockedReachableThreadLocal lockedReachableThreadLocal = new ILockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, height, false);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 5000; count++) {
            // Create field
            int numOfMinos = randoms.nextIntOpen(1, 7);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = createRandomSolutionFilter(randoms, sizedBit, lockedReachableThreadLocal, field);
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            // If found solution
            resultOptional.ifPresent(result -> {
                counter.incrementAndGet();

                LinkedList<MinoOperationWithKey> operationWithKeys = result.getMemento().getSeparableMinoStream(basicWidth)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toCollection(LinkedList::new));

                // Create Pieces
                ILockedReachable reachable = lockedReachableThreadLocal.get();
                Set<List<MinoOperationWithKey>> valid = new BuildUpStream(reachable, height)
                        .existsValidBuildPattern(field, operationWithKeys)
                        .collect(Collectors.toSet());

                PermutationIterable<MinoOperationWithKey> permutations = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
                for (List<MinoOperationWithKey> permutation : permutations) {
                    boolean canBuild = BuildUp.cansBuild(field, permutation, height, reachable);

                    if (canBuild) {
                        assertThat(valid)
                                .as(FieldView.toString(field))
                                .contains(permutation);
                    } else {
                        assertThat(valid)
                                .as(FieldView.toString(field))
                                .doesNotContain(permutation);
                    }
                }
            });
        }
    }

    @Test
    @LongTest
    void randomLong() throws ExecutionException, InterruptedException {
        // Initialize
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        // Define size
        int height = 4;
        int basicWidth = 3;
        SizedBit sizedBit = new SizedBit(basicWidth, height);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        // Create basic solutions
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        ILockedReachableThreadLocal lockedReachableThreadLocal = new ILockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, height, false);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 10; count++) {
            // Create field
            int numOfMinos = randoms.nextIntClosed(7, 9);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = createRandomSolutionFilter(randoms, sizedBit, lockedReachableThreadLocal, field);
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            // If found solution
            resultOptional.ifPresent(result -> {
                counter.incrementAndGet();

                LinkedList<MinoOperationWithKey> operationWithKeys = result.getMemento().getSeparableMinoStream(basicWidth)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toCollection(LinkedList::new));

                // Create Pieces
                ILockedReachable reachable = lockedReachableThreadLocal.get();
                Set<List<MinoOperationWithKey>> valid = new BuildUpStream(reachable, height)
                        .existsValidBuildPattern(field, operationWithKeys)
                        .collect(Collectors.toSet());

                PermutationIterable<MinoOperationWithKey> permutations = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
                for (List<MinoOperationWithKey> permutation : permutations) {
                    boolean canBuild = BuildUp.cansBuild(field, permutation, height, reachable);

                    if (canBuild) {
                        assertThat(valid)
                                .as(FieldView.toString(field))
                                .contains(permutation);
                    } else {
                        assertThat(valid)
                                .as(FieldView.toString(field))
                                .doesNotContain(permutation);
                    }
                }
            });
        }
    }

    private SolutionFilter createRandomSolutionFilter(Randoms randoms, SizedBit sizedBit, ILockedReachableThreadLocal lockedReachableThreadLocal, Field field) {
        if (randoms.nextBoolean(0.3))
            return new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
        else
            return new AllPassedSolutionFilter();
    }
}
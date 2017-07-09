package common.buildup;

import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.iterable.CombinationIterable;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import concurrent.LockedReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Randoms;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
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
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // Create OperationWithKey List
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX"
        );
        Operations operations = OperationInterpreter.parseToOperations("J,0,1,0;S,L,1,2;O,0,2,1;J,2,2,1");
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // Create Blocks
        Set<String> valid = new BuildUpStream(reachable, height)
                .existsValidBuildPattern(field, operationWithKeys)
                .map(op -> op.stream().map(OperationWithKey::getMino).map(Mino::getBlock).map(Block::name).collect(Collectors.joining()))
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
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // Create OperationWithKey List
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX"
        );
        Operations operations = OperationInterpreter.parseToOperations("J,R,0,1;O,0,1,0;L,L,3,1;I,0,1,0");
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // Create Blocks
        Set<String> valid = new BuildUpStream(reachable, height)
                .existsValidBuildPattern(field, operationWithKeys)
                .map(op -> op.stream().map(OperationWithKey::getMino).map(Mino::getBlock).map(Block::name).collect(Collectors.joining()))
                .collect(Collectors.toSet());

        // Assertion
        assertThat(valid)
                .hasSize(16)
                .doesNotContain("IJOL", "IJLO", "IOJL", "IOLJ", "ILOJ", "ILJO")  // starts I
                .doesNotContain("OIJL", "OILJ")  // starts OI
                .contains("OJLI", "OJIL", "JOIL", "JLIO", "LOJI", "LIOJ");
    }

    @Test
    void random() throws ExecutionException, InterruptedException {
        // Initialize
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();

        // Define size
        int height = 4;
        int basicWidth = 3;
        SizedBit sizedBit = new SizedBit(basicWidth, height);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        // Create basic solutions
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, height);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        for (int count = 0; count < 1000; count++) {
            // Create field
            Field field = randoms.field(height, 16);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
            PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            // If found solution
            resultOptional.ifPresent(result -> {
                System.out.println("found");
                LinkedList<OperationWithKey> operationWithKeys = result.getMemento().getOperationsStream(basicWidth)
                        .collect(Collectors.toCollection(LinkedList::new));

                // Create Blocks
                LockedReachable reachable = lockedReachableThreadLocal.get();
                Set<List<OperationWithKey>> valid = new BuildUpStream(reachable, height)
                        .existsValidBuildPattern(field, operationWithKeys)
                        .collect(Collectors.toSet());

                CombinationIterable<OperationWithKey> combinations = new CombinationIterable<>(operationWithKeys, operationWithKeys.size());
                for (List<OperationWithKey> combination : combinations) {
                    boolean canBuild = BuildUp.cansBuild(field, combination, height, reachable);

                    if (canBuild) {
                        assertThat(valid)
                                .as(FieldView.toString(field))
                                .contains(combination);
                    } else {
                        assertThat(valid)
                                .as(FieldView.toString(field))
                                .doesNotContain(combination);
                    }
                }
            });
        }
    }
}
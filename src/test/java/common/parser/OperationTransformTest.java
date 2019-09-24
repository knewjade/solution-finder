package common.parser;

import common.buildup.BuildUpStream;
import common.comparator.OperationWithKeyComparator;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import lib.ListComparator;
import lib.Randoms;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PerfectPackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTransformTest {
    @Test
    void parseToOperationWithKeys() throws Exception {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );

        String base = "L,0,2,0;Z,R,2,2;O,0,0,1;L,2,1,1";
        Operations operations = OperationInterpreter.parseToOperations(base);
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, 4);

        String line = OperationWithKeyInterpreter.parseToString(operationWithKeys);
        assertThat(line).isEqualTo("L,0,2,0,0,1025;Z,R,2,2,0,1074791424;O,0,0,1,0,1049600;L,2,1,1,1049600,1073741825");
    }

    @Test
    void parseToOperationWithKeys2() throws Exception {
        Field field = FieldFactory.createField(4);

        String base = "T,2,5,1;I,0,2,0;I,0,7,0;I,L,0,1";
        Operations operations = OperationInterpreter.parseToOperations(base);
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, 4);

        String line = OperationWithKeyInterpreter.parseToString(operationWithKeys);
        assertThat(line).isEqualTo("T,2,5,1,0,1025;I,0,2,0,0,1;I,0,7,0,0,1;I,L,0,1,0,1074791425");
    }

    @Test
    void parseToOperations() throws Exception {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );

        String base = "J,2,2,1;I,0,1,2;J,R,0,1;S,0,2,0";
        Operations operations = OperationInterpreter.parseToOperations(base);
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, 4);
        Operations restoreOperations = OperationTransform.parseToOperations(field, operationWithKeys, 4);

        assertThat(restoreOperations).isEqualTo(operations);
    }

    @Test
    @LongTest
    void randomParse() throws Exception {
        // Initialize
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = MinoRotation.create();

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

        for (int count = 0; count < 50; count++) {
            // Create field
            int numOfMinos = randoms.nextIntOpen(6, 10);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            OperationWithKeyComparator<MinoOperationWithKey> operationWithKeyComparator = new OperationWithKeyComparator<>();
            ListComparator<MinoOperationWithKey> comparator = new ListComparator<>(operationWithKeyComparator);
            BuildUpStream buildUpStream = new BuildUpStream(lockedReachableThreadLocal.get(), height);

            // If found solution
            resultOptional.ifPresent(result -> {
                List<MinoOperationWithKey> list = result.getMemento()
                        .getSeparableMinoStream(basicWidth)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toList());
                Optional<List<MinoOperationWithKey>> validOption = buildUpStream.existsValidBuildPattern(field, list).findAny();
                validOption.ifPresent(operationWithKeys -> {
                    Operations operations = OperationTransform.parseToOperations(field, operationWithKeys, height);
                    List<MinoOperationWithKey> actual = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
                    assertThat(comparator.compare(operationWithKeys, actual))
                            .as("%s%n%s%n %s", FieldView.toString(field, height), OperationWithKeyInterpreter.parseToString(operationWithKeys), OperationWithKeyInterpreter.parseToString(actual))
                            .isEqualTo(0);
                });
            });
        }
    }

    @Test
    void minMaxY() {
        MinoFactory minoFactory = new MinoFactory();
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.T, Rotate.Right, 0, 2),
                new SimpleOperation(Piece.I, Rotate.Left, 9, 2)
        );

        assertThat(OperationTransform.getMinY(minoFactory, operationList)).isEqualTo(1);
        assertThat(OperationTransform.getMaxY(minoFactory, operationList)).isEqualTo(4);
    }

    @Test
    void minMaxYWithEmpty() {
        MinoFactory minoFactory = new MinoFactory();
        List<Operation> operationList = Collections.emptyList();

        assertThat(OperationTransform.getMinY(minoFactory, operationList)).isEqualTo(-1);
        assertThat(OperationTransform.getMaxY(minoFactory, operationList)).isEqualTo(-1);
    }
}
package common.buildup;

import common.ResultHelper;
import common.datastore.*;
import common.datastore.action.Action;
import common.iterable.PermutationIterable;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import common.parser.OperationWithKeyInterpreter;
import concurrent.LockedReachableThreadLocal;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import searcher.checker.CheckerUsingHold;
import searcher.common.validator.PerfectValidator;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PerfectPackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;

class BuildUpTest {
    @Test
    void cansBuild() {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX"
        );
        int height = 4;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        Operations operations = OperationInterpreter.parseToOperations("J,0,1,0;S,L,1,2;O,0,2,1;J,2,2,1");
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
        assertThat(BuildUp.cansBuild(field, operationWithKeys, height, reachable)).isTrue();

        MinoOperationWithKey remove = operationWithKeys.remove(0);
        operationWithKeys.add(1, remove);
        assertThat(BuildUp.cansBuild(field, operationWithKeys, height, reachable)).isFalse();
    }

    @Test
    void cansBuildRandomShortByCheck() {
        Randoms randoms = new Randoms();

        // Create field
        int height = 4;

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 10000; count++) {
            // Pickup solution from checker
            int numOfMinos = randoms.nextInt(1, 7);
            Field field = randoms.field(height, numOfMinos);
            List<Block> blocks = randoms.blocks(numOfMinos);
            boolean check = checker.check(field, blocks, candidate, height, numOfMinos);

            if (check) {
                counter.incrementAndGet();
                Stream<Operation> operationStream = ResultHelper.createOperationStream(checker.getResult());
                Operations operations = new Operations(operationStream);
                List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
                assertThat(BuildUp.cansBuild(field, operationWithKeys, height, reachable))
                        .as(FieldView.toString(field) + blocks)
                        .isTrue();
            }
        }

        System.out.println(counter);
    }

    @Test
    @Tag("long")
    void cansBuildRandomLongByCheck() {
        Randoms randoms = new Randoms();

        // Create field
        int height = 4;

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 100; count++) {
            // Pickup solution from checker
            int numOfMinos = randoms.nextIntClosed(7, 10);
            Field field = randoms.field(height, numOfMinos);
            List<Block> blocks = randoms.blocks(numOfMinos);
            boolean check = checker.check(field, blocks, candidate, height, numOfMinos);

            if (check) {
                counter.incrementAndGet();
                Stream<Operation> operationStream = ResultHelper.createOperationStream(checker.getResult());
                Operations operations = new Operations(operationStream);
                List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
                assertThat(BuildUp.cansBuild(field, operationWithKeys, height, reachable))
                        .as(FieldView.toString(field) + blocks)
                        .isTrue();
            }
        }

        System.out.println(counter);
    }

    @Test
    void existsValidBuildPattern1() throws Exception {
        Field field = FieldFactory.createField("" +
                "_________X" +
                "_________X"
        );
        int maxY = 4;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);

        List<MinoOperationWithKey> operationWithKeys = Arrays.asList(
                new MinoOperationWithKey(minoFactory.create(Block.J, Rotate.Right), 5, 0L, 0L, 0),
                new MinoOperationWithKey(minoFactory.create(Block.J, Rotate.Reverse), 8, 0L, 0L, 2),
                new MinoOperationWithKey(minoFactory.create(Block.L, Rotate.Spawn), 7, 0L, 0L, 0),
                new MinoOperationWithKey(minoFactory.create(Block.S, Rotate.Spawn), 7, 0L, 0L, 1)
        );

        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, maxY, reachable);
        assertThat(exists).isTrue();
    }

    @Test
    void existsValidBuildPattern2() throws Exception {
        Field field = FieldFactory.createField("" +
                "__XXXXXXXX" +
                "__XXXXXXXX" +
                "__XXXXXXXX" +
                "__XXXXXXXX"
        );
        int maxY = 4;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);

        List<MinoOperationWithKey> operationWithKeys = Arrays.asList(
                new MinoOperationWithKey(minoFactory.create(Block.J, Rotate.Right), 0, 0L, 0L, 0),
                new MinoOperationWithKey(minoFactory.create(Block.L, Rotate.Left), 1, 1048576L, 0L, 0)
        );

        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, maxY, reachable);
        assertThat(exists).isTrue();
    }

    @Test
    void randomShortByPacking() throws ExecutionException, InterruptedException {
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

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 10000; count++) {
            // Create field
            int numOfMinos = randoms.nextInt(1, 7);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = new AllPassedSolutionFilter();
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            // If found solution
            resultOptional.ifPresent(result -> {
                counter.incrementAndGet();

                LinkedList<MinoOperationWithKey> operationWithKeys = result.getMemento().getSeparableMinoStream(basicWidth)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toCollection(LinkedList::new));

                LockedReachable reachable = lockedReachableThreadLocal.get();
                boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, height, reachable);

                if (exists) {
                    // cansBuildでtrueとなるものがあることを確認
                    Optional<List<MinoOperationWithKey>> valid = StreamSupport.stream(new PermutationIterable<>(operationWithKeys, operationWithKeys.size()).spliterator(), false)
                            .filter(combination -> BuildUp.cansBuild(field, combination, height, lockedReachableThreadLocal.get()))
                            .findAny();
                    assertThat(valid.isPresent())
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // checksKeyは必ずtrueとなる
                    assertThat(BuildUp.checksKey(operationWithKeys, 0L, height))
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // existsValidByOrderは必ずtrueになる
                    assert valid.isPresent();
                    List<MinoOperationWithKey> keys = valid.get();
                    List<Block> blocks = keys.stream().map(OperationWithKey::getBlock).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, keys.stream(), blocks, height, reachable))
                            .isTrue();
                } else {
                    // cansBuildですべてがfalseとなることを確認
                    boolean noneMatch = StreamSupport.stream(new PermutationIterable<>(operationWithKeys, operationWithKeys.size()).spliterator(), false)
                            .noneMatch(combination -> BuildUp.cansBuild(field, combination, height, lockedReachableThreadLocal.get()));
                    assertThat(noneMatch)
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // existsValidByOrderは必ずfalseになる
                    List<Block> blocks = operationWithKeys.stream().map(OperationWithKey::getBlock).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, operationWithKeys.stream(), blocks, height, reachable))
                            .isFalse();
                }
            });
        }

        System.out.println(counter);
    }

    @Test
    @Tag("long")
    void randomLongByPacking() throws ExecutionException, InterruptedException {
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

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 100; count++) {
            // Create field
            int numOfMinos = randoms.nextIntClosed(7, 10);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = new AllPassedSolutionFilter();
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            // If found solution
            resultOptional.ifPresent(result -> {
                counter.incrementAndGet();

                LinkedList<MinoOperationWithKey> operationWithKeys = result.getMemento().getSeparableMinoStream(basicWidth)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toCollection(LinkedList::new));

                LockedReachable reachable = lockedReachableThreadLocal.get();
                boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, height, reachable);

                if (exists) {
                    // cansBuildでtrueとなるものがあることを確認
                    Optional<List<MinoOperationWithKey>> valid = StreamSupport.stream(new PermutationIterable<>(operationWithKeys, operationWithKeys.size()).spliterator(), false)
                            .filter(combination -> BuildUp.cansBuild(field, combination, height, lockedReachableThreadLocal.get()))
                            .findAny();
                    assertThat(valid.isPresent())
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // checksKeyは必ずtrueとなる
                    assertThat(BuildUp.checksKey(operationWithKeys, 0L, height))
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // existsValidByOrderは必ずtrueになる
                    assert valid.isPresent();
                    List<MinoOperationWithKey> keys = valid.get();
                    List<Block> blocks = keys.stream().map(OperationWithKey::getBlock).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, keys.stream(), blocks, height, reachable))
                            .isTrue();
                } else {
                    // cansBuildですべてがfalseとなることを確認
                    boolean noneMatch = StreamSupport.stream(new PermutationIterable<>(operationWithKeys, operationWithKeys.size()).spliterator(), false)
                            .noneMatch(combination -> BuildUp.cansBuild(field, combination, height, lockedReachableThreadLocal.get()));
                    assertThat(noneMatch)
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // existsValidByOrderは必ずfalseになる
                    List<Block> blocks = operationWithKeys.stream().map(OperationWithKey::getBlock).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, operationWithKeys.stream(), blocks, height, reachable))
                            .isFalse();
                }
            });
        }

        System.out.println(counter);
    }

    @Test
    void checksAllPatterns1() throws Exception {
        int height = 4;
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "___XXXXXXX"
        );
        Operations operations = new Operations(Arrays.asList(
                new SimpleOperation(Block.T, Rotate.Right, 0, 1),
                new SimpleOperation(Block.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Block.Z, Rotate.Spawn, 1, 0)
        ));

        // OperationWithKeyに変換
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // reachableの準備
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // existsValidBuildPatternのチェック
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, height, reachable);
        assertThat(exists).isTrue();

        // 有効な手順を列挙する
        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
        Set<List<MinoOperationWithKey>> validPatterns = buildUpStream.existsValidBuildPattern(field, operationWithKeys)
                .collect(Collectors.toSet());

        // すべての組み合わせでチェック
        Iterable<List<MinoOperationWithKey>> iterable = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
        for (List<MinoOperationWithKey> withKeys : iterable) {
            boolean canBuild = BuildUp.cansBuild(field, withKeys, height, reachable);
            assertThat(canBuild).isEqualTo(validPatterns.contains(withKeys));

            boolean checksKey = BuildUp.checksKey(withKeys, 0L, height);
            assertThat(checksKey).isTrue();
        }
    }

    @Test
    void checksAllPatterns2() throws Exception {
        int height = 4;
        Field field = FieldFactory.createField("" +
                "_____XXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "____XXXXXX"
        );
        Operations operations = new Operations(Arrays.asList(
                new SimpleOperation(Block.I, Rotate.Left, 0, 1),
                new SimpleOperation(Block.J, Rotate.Spawn, 2, 0),
                new SimpleOperation(Block.S, Rotate.Right, 1, 1),
                new SimpleOperation(Block.T, Rotate.Reverse, 3, 1)
        ));

        // OperationWithKeyに変換
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // reachableの準備
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // existsValidBuildPatternのチェック
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, height, reachable);
        assertThat(exists).isTrue();

        // 有効な手順を列挙する
        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
        Set<List<MinoOperationWithKey>> validPatterns = buildUpStream.existsValidBuildPattern(field, operationWithKeys)
                .collect(Collectors.toSet());

        // すべての組み合わせでチェック
        Iterable<List<MinoOperationWithKey>> iterable = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
        for (List<MinoOperationWithKey> withKeys : iterable) {
            boolean canBuild = BuildUp.cansBuild(field, withKeys, height, reachable);
            assertThat(canBuild).isEqualTo(validPatterns.contains(withKeys));

            boolean checksKey = BuildUp.checksKey(withKeys, 0L, height);
            assertThat(checksKey).isTrue();
        }
    }

    @Test
    void existsValidByOrder() throws Exception {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );
        String line = "L,L,3,1,0,1049601;L,R,1,1,0,1049601;I,L,0,1,0,1074791425;T,2,2,2,1048576,1073742848";
        MinoFactory minoFactory = new MinoFactory();
        Stream<MinoOperationWithKey> stream = OperationWithKeyInterpreter.parseToStream(line, minoFactory);
        List<MinoOperationWithKey> operations = stream.collect(Collectors.toList());
        System.out.println(operations);

        int height = 4;
        MinoRotation minoRotation = new MinoRotation();
        MinoShifter minoShifter = new MinoShifter();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // true
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, L, I, T), height, reachable)).isTrue();
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, I, L, T), height, reachable)).isTrue();
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(I, L, L, T), height, reachable)).isTrue();

        // false
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, L, T, I), height, reachable)).isFalse();
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, T, L, I), height, reachable)).isFalse();
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, T, I, L), height, reachable)).isFalse();
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(T, L, I, L), height, reachable)).isFalse();
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(T, I, L, L), height, reachable)).isFalse();
    }
}
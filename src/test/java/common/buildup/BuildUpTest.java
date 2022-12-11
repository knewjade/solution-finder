package common.buildup;

import common.ResultHelper;
import common.datastore.*;
import common.datastore.action.Action;
import common.iterable.PermutationIterable;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import common.parser.OperationWithKeyInterpreter;
import concurrent.ILockedReachableThreadLocal;
import core.action.candidate.Candidate;
import core.action.candidate.CandidateFacade;
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
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Randoms;
import module.LongTest;
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

import static core.mino.Piece.*;
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
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

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
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 10000; count++) {
            // Pickup solution from checker
            int numOfMinos = randoms.nextIntOpen(1, 7);
            Field field = randoms.field(height, numOfMinos);
            List<Piece> pieces = randoms.blocks(numOfMinos);
            boolean check = checker.check(field, pieces, candidate, height, numOfMinos);

            if (check) {
                counter.incrementAndGet();
                Stream<Operation> operationStream = ResultHelper.createOperationStream(checker.getResult());
                Operations operations = new Operations(operationStream);
                List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
                assertThat(BuildUp.cansBuild(field, operationWithKeys, height, reachable))
                        .as(FieldView.toString(field) + pieces)
                        .isTrue();
            }
        }
    }

    @Test
    @LongTest
    void cansBuildRandomLongByCheck() {
        Randoms randoms = new Randoms();

        // Create field
        int height = 4;

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);

        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        AtomicInteger counter = new AtomicInteger();

        for (int count = 0; count < 50; count++) {
            // Pickup solution from checker
            int numOfMinos = randoms.nextIntClosed(7, 10);
            Field field = randoms.field(height, numOfMinos);
            List<Piece> pieces = randoms.blocks(numOfMinos);
            boolean check = checker.check(field, pieces, candidate, height, numOfMinos);

            if (check) {
                counter.incrementAndGet();
                Stream<Operation> operationStream = ResultHelper.createOperationStream(checker.getResult());
                Operations operations = new Operations(operationStream);
                List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
                assertThat(BuildUp.cansBuild(field, operationWithKeys, height, reachable))
                        .as(FieldView.toString(field) + pieces)
                        .isTrue();
            }
        }
    }

    @Test
    void existsValidBuildPattern1() {
        Field field = FieldFactory.createField("" +
                "_________X" +
                "_________X"
        );
        int maxY = 4;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxY);

        List<MinoOperationWithKey> operationWithKeys = Arrays.asList(
                new FullOperationWithKey(minoFactory.create(Piece.J, Rotate.Right), 5, 0L, 0L, 0),
                new FullOperationWithKey(minoFactory.create(Piece.J, Rotate.Reverse), 8, 0L, 0L, 2),
                new FullOperationWithKey(minoFactory.create(Piece.L, Rotate.Spawn), 7, 0L, 0L, 0),
                new FullOperationWithKey(minoFactory.create(Piece.S, Rotate.Spawn), 7, 0L, 0L, 1)
        );

        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, maxY, reachable);
        assertThat(exists).isTrue();
    }

    @Test
    void existsValidBuildPattern2() {
        Field field = FieldFactory.createField("" +
                "__XXXXXXXX" +
                "__XXXXXXXX" +
                "__XXXXXXXX" +
                "__XXXXXXXX"
        );
        int maxY = 4;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxY);

        List<MinoOperationWithKey> operationWithKeys = Arrays.asList(
                new FullOperationWithKey(minoFactory.create(Piece.J, Rotate.Right), 0, 0L, 0L, 0),
                new FullOperationWithKey(minoFactory.create(Piece.L, Rotate.Left), 1, 1048576L, 0L, 0)
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

        for (int count = 0; count < 10000; count++) {
            // Create field
            int numOfMinos = randoms.nextIntOpen(1, 7);
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

                ILockedReachable reachable = lockedReachableThreadLocal.get();
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
                    List<Piece> pieces = keys.stream().map(OperationWithKey::getPiece).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, keys.stream(), pieces, height, reachable))
                            .isTrue();
                } else {
                    // cansBuildですべてがfalseとなることを確認
                    boolean noneMatch = StreamSupport.stream(new PermutationIterable<>(operationWithKeys, operationWithKeys.size()).spliterator(), false)
                            .noneMatch(combination -> BuildUp.cansBuild(field, combination, height, lockedReachableThreadLocal.get()));
                    assertThat(noneMatch)
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // existsValidByOrderは必ずfalseになる
                    List<Piece> pieces = operationWithKeys.stream().map(OperationWithKey::getPiece).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, operationWithKeys.stream(), pieces, height, reachable))
                            .isFalse();
                }
            });
        }
    }

    @Test
    @LongTest
    void randomLongByPacking() throws ExecutionException, InterruptedException {
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

        for (int count = 0; count < 50; count++) {
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

                ILockedReachable reachable = lockedReachableThreadLocal.get();
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
                    List<Piece> pieces = keys.stream().map(OperationWithKey::getPiece).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, keys.stream(), pieces, height, reachable))
                            .isTrue();
                } else {
                    // cansBuildですべてがfalseとなることを確認
                    boolean noneMatch = StreamSupport.stream(new PermutationIterable<>(operationWithKeys, operationWithKeys.size()).spliterator(), false)
                            .noneMatch(combination -> BuildUp.cansBuild(field, combination, height, lockedReachableThreadLocal.get()));
                    assertThat(noneMatch)
                            .as(FieldView.toString(field) + OperationWithKeyInterpreter.parseToString(operationWithKeys))
                            .isTrue();

                    // existsValidByOrderは必ずfalseになる
                    List<Piece> pieces = operationWithKeys.stream().map(OperationWithKey::getPiece).collect(Collectors.toList());
                    assertThat(BuildUp.existsValidByOrder(field, operationWithKeys.stream(), pieces, height, reachable))
                            .isFalse();
                }
            });
        }
    }

    @Test
    void checksAllPatterns1() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "___XXXXXXX"
        );
        Operations operations = new Operations(Arrays.asList(
                new SimpleOperation(Piece.T, Rotate.Right, 0, 1),
                new SimpleOperation(Piece.S, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.Z, Rotate.Spawn, 1, 0)
        ));

        // OperationWithKeyに変換
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // reachableの準備
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

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
    void checksAllPatterns2() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                "_____XXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "____XXXXXX"
        );
        Operations operations = new Operations(Arrays.asList(
                new SimpleOperation(Piece.I, Rotate.Left, 0, 1),
                new SimpleOperation(Piece.J, Rotate.Spawn, 2, 0),
                new SimpleOperation(Piece.S, Rotate.Right, 1, 1),
                new SimpleOperation(Piece.T, Rotate.Reverse, 3, 1)
        ));

        // OperationWithKeyに変換
        MinoFactory minoFactory = new MinoFactory();
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // reachableの準備
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

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
    void existsValidByOrder() {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );
        String line = "L,L,3,1,0,7;L,R,1,1,0,7;I,L,0,1,0,15;T,2,2,2,4,15";
        MinoFactory minoFactory = new MinoFactory();
        Stream<MinoOperationWithKey> stream = OperationWithKeyInterpreter.parseToStream(line, minoFactory);
        List<MinoOperationWithKey> operations = stream.collect(Collectors.toList());

        int height = 4;
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoShifter minoShifter = new MinoShifter();
        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

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

    @Test
    void existsValidBuildPatternWithoutKey() {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );
        int height = 4;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

        {
            String line = "O,0,0,0;O,0,2,0;O,0,2,0;O,0,2,0";
            Operations operations = OperationInterpreter.parseToOperations(line);
            List<MinoOperationWithKey> keys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

            assertThat(BuildUp.existsValidBuildPatternWithoutKey(field, keys, height, reachable)).isTrue();
        }

        {
            String line = "L,2,1,1;I,L,3,1;Z,0,1,0;J,2,1,1";
            Operations operations = OperationInterpreter.parseToOperations(line);
            List<MinoOperationWithKey> keys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

            assertThat(BuildUp.existsValidBuildPatternWithoutKey(field, keys, height, reachable)).isTrue();
        }

        {
            String line = "I,0,1,1;O,0,0,0;O,0,2,0;I,0,1,0";
            Operations operations = OperationInterpreter.parseToOperations(line);
            List<MinoOperationWithKey> keys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

            assertThat(BuildUp.existsValidBuildPatternWithoutKey(field, keys, height, reachable)).isFalse();
        }
    }
}
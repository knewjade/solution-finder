package common.buildup;

import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import common.datastore.SimpleOperationWithKey;
import common.iterable.PermutationIterable;
import common.parser.OperationTransform;
import common.parser.OperationWithKeyInterpreter;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BuildUpTest {
    @Test
    public void existsValidBuildPattern1() throws Exception {
        Field field = FieldFactory.createField("" +
                "_________X" +
                "_________X"
        );
        MinoFactory minoFactory = new MinoFactory();
        List<OperationWithKey> operationWithKeys = Arrays.asList(
                new SimpleOperationWithKey(minoFactory.create(Block.J, Rotate.Right), 5, 0L, 0L, 0),
                new SimpleOperationWithKey(minoFactory.create(Block.J, Rotate.Reverse), 8, 0L, 0L, 2),
                new SimpleOperationWithKey(minoFactory.create(Block.L, Rotate.Spawn), 7, 0L, 0L, 0),
                new SimpleOperationWithKey(minoFactory.create(Block.S, Rotate.Spawn), 7, 0L, 0L, 1)
        );
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int maxY = 4;
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, maxY, reachable);
        assertThat(exists, is(true));
    }

    @Test
    public void existsValidBuildPattern2() throws Exception {
        Field field = FieldFactory.createField("" +
                "__XXXXXXXX" +
                "__XXXXXXXX" +
                "__XXXXXXXX" +
                "__XXXXXXXX"
        );
        MinoFactory minoFactory = new MinoFactory();
        List<OperationWithKey> operationWithKeys = Arrays.asList(
                new SimpleOperationWithKey(minoFactory.create(Block.J, Rotate.Right), 0, 0L, 0L, 0),
                new SimpleOperationWithKey(minoFactory.create(Block.L, Rotate.Left), 1, 1048576L, 0L, 0)
        );
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int maxY = 4;
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, maxY, reachable);
        assertThat(exists, is(true));
    }

    @Test
    public void checksAllPatterns1() throws Exception {
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
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // reachableの準備
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // existsValidBuildPatternのチェック
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, height, reachable);
        assertThat(exists, is(true));

        // 有効な手順を列挙する
        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
        Set<List<OperationWithKey>> validPatterns = buildUpStream.existsValidBuildPattern(field, operationWithKeys)
                .collect(Collectors.toSet());

        // すべての組み合わせでチェック
        Iterable<List<OperationWithKey>> iterable = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
        for (List<OperationWithKey> withKeys : iterable) {
            boolean canBuild = BuildUp.cansBuild(field, withKeys, height, reachable);
            assertThat(canBuild, is(validPatterns.contains(withKeys)));

            boolean checksKey = BuildUp.checksKey(withKeys, 0L, height);
            assertThat(checksKey, is(true));
        }
    }

    @Test
    public void checksAllPatterns2() throws Exception {
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
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);

        // reachableの準備
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // existsValidBuildPatternのチェック
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, height, reachable);
        assertThat(exists, is(true));

        // 有効な手順を列挙する
        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
        Set<List<OperationWithKey>> validPatterns = buildUpStream.existsValidBuildPattern(field, operationWithKeys)
                .collect(Collectors.toSet());

        // すべての組み合わせでチェック
        Iterable<List<OperationWithKey>> iterable = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
        for (List<OperationWithKey> withKeys : iterable) {
            boolean canBuild = BuildUp.cansBuild(field, withKeys, height, reachable);
            assertThat(canBuild, is(validPatterns.contains(withKeys)));

            boolean checksKey = BuildUp.checksKey(withKeys, 0L, height);
            assertThat(checksKey, is(true));
        }
    }

    @Test
    public void existsValidByOrder() throws Exception {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                ""
        );
        String line = "L,L,3,1,0,1049601;L,R,1,1,0,1049601;I,L,0,1,0,1074791425;T,2,2,2,1048576,1073742848";
        MinoFactory minoFactory = new MinoFactory();
        Stream<OperationWithKey> stream = OperationWithKeyInterpreter.parseToStream(line, minoFactory);
        List<OperationWithKey> operations = stream.collect(Collectors.toList());
        System.out.println(operations);

        int height = 4;
        MinoRotation minoRotation = new MinoRotation();
        MinoShifter minoShifter = new MinoShifter();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

        // true
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, L, I, T), height, reachable), is(true));
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, I, L, T), height, reachable), is(true));
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(I, L, L, T), height, reachable), is(true));

        // false
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, L, T, I), height, reachable), is(false));
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, T, L, I), height, reachable), is(false));
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(L, T, I, L), height, reachable), is(false));
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(T, L, I, L), height, reachable), is(false));
        assertThat(BuildUp.existsValidByOrder(field, operations.stream(), Arrays.asList(T, I, L, L), height, reachable), is(false));
    }
}
package common.buildup;

import common.datastore.IOperationWithKey;
import common.datastore.OperationWithKey;
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
        List<IOperationWithKey> operationWithKeys = Arrays.asList(
                new OperationWithKey(minoFactory.create(Block.J, Rotate.Right), 5, 0L, 0),
                new OperationWithKey(minoFactory.create(Block.J, Rotate.Reverse), 8, 0L, 2),
                new OperationWithKey(minoFactory.create(Block.L, Rotate.Spawn), 7, 0L, 0),
                new OperationWithKey(minoFactory.create(Block.S, Rotate.Spawn), 7, 0L, 1)
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
        List<IOperationWithKey> operationWithKeys = Arrays.asList(
                new OperationWithKey(minoFactory.create(Block.J, Rotate.Right), 0, 0L, 0),
                new OperationWithKey(minoFactory.create(Block.L, Rotate.Left), 1, 1048576L, 0)
        );
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int maxY = 4;
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        boolean exists = BuildUp.existsValidBuildPattern(field, operationWithKeys, maxY, reachable);
        assertThat(exists, is(true));
    }
}
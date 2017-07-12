package common.comparator;

import common.datastore.OperationWithKey;
import common.datastore.SimpleOperationWithKey;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class OperationWithKeyComparatorTest {
    @Test
    public void compare() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new SimpleOperationWithKey(minoFactory.create(Block.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        Mino newMino = new MinoFactory().create(Block.I, Rotate.Spawn);
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, y, deleteKey, usingKey);

        OperationWithKeyComparator comparator = new OperationWithKeyComparator();
        assertThat(operationWithKey1.toString(), comparator.compare(operationWithKey1, operationWithKey2), is(0));
        assertThat(operationWithKey2.toString(), comparator.compare(operationWithKey2, operationWithKey1), is(0));
    }

    @Test
    public void compareDiffX() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new SimpleOperationWithKey(minoFactory.create(Block.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        int newX = randoms.nextInt(10);
        if (newX == x)
            newX += 1;
        Mino newMino = new MinoFactory().create(Block.I, Rotate.Spawn);
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, newX, y, deleteKey, usingKey);

        // assert is not 0 & sign reversed
        OperationWithKeyComparator comparator = new OperationWithKeyComparator();
        assertThat(operationWithKey2.toString(), comparator.compare(operationWithKey1, operationWithKey2) * comparator.compare(operationWithKey2, operationWithKey1), is(lessThan(0)));
    }

    @Test
    public void compareDiffY() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new SimpleOperationWithKey(minoFactory.create(Block.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        int newY = randoms.nextInt(20);
        if (newY == y)
            newY += 1;
        Mino newMino = new MinoFactory().create(Block.I, Rotate.Spawn);
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, newY, deleteKey, usingKey);

        // assert is not 0 & sign reversed
        OperationWithKeyComparator comparator = new OperationWithKeyComparator();
        assertThat(operationWithKey2.toString(), comparator.compare(operationWithKey1, operationWithKey2) * comparator.compare(operationWithKey2, operationWithKey1), is(lessThan(0)));
    }

    @Test
    public void compareDiffDeleteKey() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new SimpleOperationWithKey(minoFactory.create(Block.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        Mino newMino = new MinoFactory().create(Block.I, Rotate.Spawn);
        Long choose = randoms.keys();
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, y, choose, usingKey);

        // assert is not 0 & sign reversed
        OperationWithKeyComparator comparator = new OperationWithKeyComparator();
        assertThat(operationWithKey2.toString(), comparator.compare(operationWithKey1, operationWithKey2) * comparator.compare(operationWithKey2, operationWithKey1), is(lessThan(0)));
    }

    @Test
    public void compareDiffUsingKey() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new SimpleOperationWithKey(minoFactory.create(Block.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        Mino newMino = new MinoFactory().create(Block.I, Rotate.Spawn);
        Long choose = randoms.keys();
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, y, deleteKey, choose);

        // assert is 0
        OperationWithKeyComparator comparator = new OperationWithKeyComparator();
        assertThat(operationWithKey1.toString(), comparator.compare(operationWithKey1, operationWithKey2), is(0));
        assertThat(operationWithKey2.toString(), comparator.compare(operationWithKey2, operationWithKey1), is(0));
    }

    private OperationWithKey createNewOperationWithKey(Mino mino, int x, int y, long deleteKey, long usingKey) {
        return new OperationWithKey() {
            @Override
            public Mino getMino() {
                return mino;
            }

            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }

            @Override
            public long getNeedDeletedKey() {
                return deleteKey;
            }

            @Override
            public long getUsingKey() {
                return usingKey;
            }
        };
    }
}
package common.comparator;

import common.datastore.OperationWithKey;
import common.datastore.FullOperationWithKey;
import core.mino.Piece;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationWithKeyComparatorTest {
    @Test
    void compare() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new FullOperationWithKey(minoFactory.create(Piece.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        Mino newMino = new MinoFactory().create(Piece.I, Rotate.Spawn);
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, y, deleteKey, usingKey);

        OperationWithKeyComparator<OperationWithKey> comparator = new OperationWithKeyComparator<>();
        assertThat(comparator.compare(operationWithKey1, operationWithKey2))
                .as(operationWithKey1.toString())
                .isEqualTo(0);
        assertThat(comparator.compare(operationWithKey2, operationWithKey1))
                .as(operationWithKey2.toString())
                .isEqualTo(0);
    }

    @Test
    void compareDiffX() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new FullOperationWithKey(minoFactory.create(Piece.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        int newX = randoms.nextInt(10);
        if (newX == x)
            newX += 1;
        Mino newMino = new MinoFactory().create(Piece.I, Rotate.Spawn);
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, newX, y, deleteKey, usingKey);

        // assert is not 0 & sign reversed
        OperationWithKeyComparator<OperationWithKey> comparator = new OperationWithKeyComparator<>();
        assertThat(comparator.compare(operationWithKey1, operationWithKey2) * comparator.compare(operationWithKey2, operationWithKey1))
                .as(operationWithKey2.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffY() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new FullOperationWithKey(minoFactory.create(Piece.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        int newY = randoms.nextInt(20);
        if (newY == y)
            newY += 1;
        Mino newMino = new MinoFactory().create(Piece.I, Rotate.Spawn);
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, newY, deleteKey, usingKey);

        // assert is not 0 & sign reversed
        OperationWithKeyComparator<OperationWithKey> comparator = new OperationWithKeyComparator<>();
        assertThat(comparator.compare(operationWithKey1, operationWithKey2) * comparator.compare(operationWithKey2, operationWithKey1))
                .as(operationWithKey2.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffDeleteKey() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new FullOperationWithKey(minoFactory.create(Piece.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        Mino newMino = new MinoFactory().create(Piece.I, Rotate.Spawn);
        Long choose = randoms.key();
        while (choose == deleteKey)
            choose = randoms.key();

        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, y, choose, usingKey);

        // assert is not 0 & sign reversed
        OperationWithKeyComparator<OperationWithKey> comparator = new OperationWithKeyComparator<>();
        assertThat(comparator.compare(operationWithKey1, operationWithKey2) * comparator.compare(operationWithKey2, operationWithKey1))
                .as(operationWithKey2.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffUsingKey() throws Exception {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        int x = randoms.nextInt(10);
        int y = randoms.nextInt(20);
        long deleteKey = 0L;
        long usingKey = 1049600L;
        OperationWithKey operationWithKey1 = new FullOperationWithKey(minoFactory.create(Piece.I, Rotate.Spawn), x, y, deleteKey, usingKey);

        Mino newMino = new MinoFactory().create(Piece.I, Rotate.Spawn);
        Long choose = randoms.key();
        OperationWithKey operationWithKey2 = createNewOperationWithKey(newMino, x, y, deleteKey, choose);

        // assert is 0
        OperationWithKeyComparator<OperationWithKey> comparator = new OperationWithKeyComparator<>();

        int compare1 = comparator.compare(operationWithKey1, operationWithKey2);
        assertThat(compare1)
                .as(operationWithKey1.toString())
                .isEqualTo(0);

        int compare2 = comparator.compare(operationWithKey2, operationWithKey1);
        assertThat(compare2)
                .as(operationWithKey2.toString())
                .isEqualTo(0);
    }

    private OperationWithKey createNewOperationWithKey(Mino mino, int x, int y, long deleteKey, long usingKey) {
        // TODO: Use mock
        return new OperationWithKey() {
            @Override
            public Piece getPiece() {
                return mino.getPiece();
            }

            @Override
            public Rotate getRotate() {
                return mino.getRotate();
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
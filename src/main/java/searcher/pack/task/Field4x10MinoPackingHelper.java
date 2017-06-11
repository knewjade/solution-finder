package searcher.pack.task;

import common.datastore.BlockCounter;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import common.datastore.SimpleOperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import searcher.pack.IMinoField;
import searcher.pack.MinoFieldComparator;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

// フィールドの探索範囲が4x10のとき限定のTask。最後のパターンが決まっているため少し高速に動作
public class Field4x10MinoPackingHelper implements TaskResultHelper {
    private static class IOnlyMinoField implements IMinoField {
        private static final List<OperationWithKey> OPERATION_WITH_KEYS = Collections.singletonList(
                new SimpleOperationWithKey(new Mino(Block.I, Rotate.Left), 0, 0L, 1074791425L, 0)
        );

        private static BlockField parseToBlockField(List<OperationWithKey> operations, int height) {
            BlockField blockField = new BlockField(height);
            for (OperationWithKey operation : operations) {
                SmallField smallField = new SmallField();
                Mino mino = operation.getMino();
                smallField.putMino(mino, operation.getX(), operation.getY());
                smallField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                blockField.merge(smallField, mino.getBlock());
            }
            return blockField;
        }

        private final List<OperationWithKey> operationWithKeys = OPERATION_WITH_KEYS;
        private final ColumnSmallField columnSmallField = new ColumnSmallField();
        private final BlockCounter blockCounter = new BlockCounter(Collections.singletonList(Block.I));
        private final BlockField blockField = parseToBlockField(OPERATION_WITH_KEYS, 4);

        @Override
        public ColumnField getOuterField() {
            return columnSmallField;
        }

        @Override
        public List<OperationWithKey> getOperations() {
            return operationWithKeys;
        }

        @Override
        public BlockField getBlockField() {
            return blockField;
        }

        @Override
        public BlockCounter getBlockCounter() {
            return blockCounter;
        }

        @Override
        public int getMaxIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(IMinoField o) {
            return MinoFieldComparator.compareMinoField(this, o);
        }
    }

    private static final IMinoField LEFT_I_ONLY = new IOnlyMinoField();

    // 高さが4・最後の1列がのこる場合で、パフェできるパターンは2つしか存在しない
    @Override
    public Stream<Result> fixResult(PackSearcher searcher, ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        SizedBit sizedBit = searcher.getSizedBit();
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        long board = lastOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
        if (board == sizedBit.getFillBoard()) {
            if (solutionFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
        } else if (board == 0b111111110000L) {
            MinoFieldMemento concatILeft = nextMemento.concat(LEFT_I_ONLY);
            if (solutionFilter.testLast(concatILeft))
                return Stream.of(createResult(concatILeft));
        }
        return Stream.empty();
    }

    private Result createResult(MinoFieldMemento memento) {
        return new Result(memento);
    }
}

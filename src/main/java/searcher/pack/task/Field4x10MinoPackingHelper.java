package searcher.pack.task;

import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.MinoOperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_field.MinoField;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

// フィールドの探索範囲が4x10のとき限定のTask。最後のパターンが決まっているため少し高速に動作
// パフェ用
public class Field4x10MinoPackingHelper implements TaskResultHelper {
    private static class IOnlyMinoField implements MinoField {
        private static final List<OperationWithKey> OPERATION_WITH_KEYS = Collections.singletonList(
                new MinoOperationWithKey(new Mino(Block.I, Rotate.Left), 0, 0L, 1074791425L, 0)
        );

        private final List<OperationWithKey> operationWithKeys = OPERATION_WITH_KEYS;
        private final ColumnSmallField columnSmallField = ColumnFieldFactory.createField();
        private final BlockCounter blockCounter = new BlockCounter(Collections.singletonList(Block.I));

        @Override
        public ColumnField getOuterField() {
            return columnSmallField;
        }

        @Override
        public Stream<OperationWithKey> getOperationsStream() {
            return operationWithKeys.stream();
        }

        @Override
        public BlockCounter getBlockCounter() {
            return blockCounter;
        }

        @Override
        public int getMaxIndex() {
            throw new UnsupportedOperationException();
        }
    }

    private static final MinoField LEFT_I_ONLY = new IOnlyMinoField();

    // 高さが4・最後の1列がのこる場合で、パフェできるパターンは2つしか存在しない
    @Override
    public Stream<Result> fixResult(PackSearcher searcher, long innerFieldBoard, MinoFieldMemento nextMemento) {
        SizedBit sizedBit = searcher.getSizedBit();
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        if (innerFieldBoard == sizedBit.getFillBoard()) {
            if (solutionFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
        } else {
            assert innerFieldBoard == 0b111111110000L;
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

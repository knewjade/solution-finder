package _experimental.allcomb.task;

import _experimental.allcomb.MinoField;
import _experimental.allcomb.PackSearcher;
import _experimental.allcomb.SizedBit;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;
import common.datastore.SimpleOperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

import java.util.Collections;
import java.util.stream.Stream;

// フィールドの探索範囲が4x10のとき限定のTask。最後のパターンが決まっているため少し高速に動作
public class Field4x10MinoPackingHelper implements TaskResultHelper {
    private static final MinoField LEFT_I_ONLY = new MinoField(
            Collections.singletonList(
                    new SimpleOperationWithKey(new Mino(Block.I, Rotate.Left), 0, 0L, 1074791425L, 0)
            ),
            new ColumnSmallField(),
            4
    );

    // 高さが4・最後の1列がのこる場合で、パフェできるパターンは2つしか存在しない
    @Override
    public Stream<Result> fixResult(PackSearcher searcher, ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        SizedBit sizedBit = searcher.getSizedBit();
        MementoFilter mementoFilter = searcher.getMementoFilter();
        long board = lastOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
        if (board == sizedBit.getFillBoard()) {
            if (mementoFilter.testLast(nextMemento))
                return Stream.of(createResult(nextMemento));
        } else if (board == 0b111111110000L) {
            MinoFieldMemento concatILeft = nextMemento.concat(LEFT_I_ONLY);
            if (mementoFilter.testLast(concatILeft))
                return Stream.of(createResult(concatILeft));
        }
        return Stream.empty();
    }

    private Result createResult(MinoFieldMemento memento) {
        return new Result(memento);
    }
}

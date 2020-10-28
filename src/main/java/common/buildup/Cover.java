package common.buildup;

import common.datastore.MinoOperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Piece;

import java.util.List;
import java.util.stream.Stream;

public interface Cover {
    // block順番で組み立てられる手順が存在するかチェックする
    // operationsで使用するミノとblocksが一致していること
    boolean existsValidByOrder(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable, int maxDepth);

    boolean existsValidByOrderWithHold(Field field, Stream<MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable, int maxDepth);
}

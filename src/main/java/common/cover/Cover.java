package common.cover;

import common.datastore.MinoOperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Piece;

import java.util.List;
import java.util.stream.Stream;

public interface Cover {
    // block順番で組み立てられる手順が存在するかチェックする
    // operationsで使用するミノとblocksが一致していること
    boolean canBuild(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable, int maxDepth);

    boolean canBuildWithHold(Field field, Stream<MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable, int maxDepth);
}

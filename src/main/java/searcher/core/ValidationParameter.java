package searcher.core;

import common.datastore.order.Order;
import core.field.Field;
import core.mino.Mino;

public class ValidationParameter {
    private final Order order;  // ミノ接着前の探索状態
    private final Field field;  // ミノ接着後のフィールド (ライン消去後の地形)
    private final long deletedKey;  // ミノ接着後に消去されたライン
    private final int maxClearLine;  // ミノ接着後の残りのライン消去数
    private final boolean isLast;  // 利用できる最後のミノであるか
    private final Mino mino;  // 接着したミノ
    private final int x;  // 接着したミノのx座標
    private final int y;  // 接着したミノのy座標

    public ValidationParameter(Order order, Field field, Mino mino, int x, int y, long deletedKey, int maxClearLine, boolean isLast) {
        this.field = field;
        this.deletedKey = deletedKey;
        this.maxClearLine = maxClearLine;
        this.isLast = isLast;
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.order = order;
    }

    public Field freezeField() {
        return field.freeze();
    }

    public long getDeletedKey() {
        return deletedKey;
    }

    public int getMaxClearLine() {
        return maxClearLine;
    }

    public boolean isLast() {
        return isLast;
    }

    public Mino getMino() {
        return mino;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Order getOrder() {
        return order;
    }
}

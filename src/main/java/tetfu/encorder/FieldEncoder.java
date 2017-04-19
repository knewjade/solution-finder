package tetfu.encorder;

import tetfu.ColoredField;

import static tetfu.Tetfu.*;

public class FieldEncoder extends Encoder {
    private final ColoredField prev;
    private final ColoredField current;

    public FieldEncoder(ColoredField prev, ColoredField current) {
        this.prev = prev;
        this.current = current;
    }

    // フィールドをエンコードする
    // 前のフィールドがないときは空のフィールドを指定する
    // 入力フィールドの高さは23, 幅は10
    public boolean encode() {
        // フィールド値から連続したブロック数に変換
        boolean isChanged = false;
        int prev_diff = getDiff(prev, current, 0, 0);
        int counter = -1;
        for (int yIndex = 0; yIndex < TETFU_MAX_HEIGHT; yIndex++) {
            for (int xIndex = 0; xIndex < TETFU_FIELD_WIDTH; xIndex++) {
                int diff = getDiff(prev, current, xIndex, yIndex);
                if (diff != prev_diff) {
                    recordBlockCounts(prev_diff, counter);
                    counter = 0;
                    prev_diff = diff;
                    isChanged = true;
                } else {
                    counter += 1;
                }
            }
        }

        // 最後の連続ブロックを処理
        recordBlockCounts(prev_diff, counter);

        return isChanged;
    }

    // 前のフィールドとの差を計算: 0〜16
    private int getDiff(ColoredField prev, ColoredField current, int xIndex, int yIndex) {
        int y = TETFU_FIELD_TOP - yIndex - 1;

        if (y < 0)
            return 8;

        return current.getBlockNumber(xIndex, y) - prev.getBlockNumber(xIndex, y) + 8;
    }

    private void recordBlockCounts(int diff, int counter) {
        int value = diff * TETFU_FIELD_BLOCKS + counter;
        pushValues(value, 2);
    }
}

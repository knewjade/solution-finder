package searcher.spins.fill.line;

import core.field.KeyOperators;
import core.mino.Mino;

import java.util.ArrayList;
import java.util.List;

/**
class EmptyLines {
    private final long filledLine;
    private final List<Integer> emptyLines;
    private final int targetLineIndex;
    private final int maxIndex;

    EmptyLines(long filledLine, int targetY, int maxHeight) {
        ArrayList<Integer> emptyLines = new ArrayList<>();

        // 揃っていないラインを抽出
        int targetLineIndex = -1;
        for (int y = 0; y < maxHeight; y++) {
            if ((filledLine & KeyOperators.getBitKey(y)) == 0L) {
                emptyLines.add(y);
                if (y == targetY) {
                    targetLineIndex = emptyLines.size() - 1;
                }
            }
        }

        assert 0 <= targetLineIndex : targetLineIndex;
        assert emptyLines.get(targetLineIndex) == targetY;

        this.filledLine = filledLine;
        this.emptyLines = emptyLines;
        this.maxIndex = emptyLines.size();
        this.targetLineIndex = targetLineIndex;
    }

    public MinoDiffWithKey parse(MinoDiff minoDiff) {
        Mino mino = minoDiff.getMino();
        int dy = minoDiff.getDy();
        int minY = mino.getMinY();

        // 空白ラインを dy だけ移動する
        int emptyMinIndex = targetLineIndex - dy + minY;
        if (emptyMinIndex < 0) {
            return null;
        }

        int emptyMinY = emptyLines.get(emptyMinIndex);

        int emptyMaxIndex = targetLineIndex - dy + mino.getMaxY();
        if (maxIndex <= emptyMaxIndex) {
            return null;
        }

        int emptyMaxY = emptyLines.get(emptyMaxIndex);

        assert 0 <= emptyMinY : emptyMinY;

        // 消去ラインで決まった範囲だけ取り出す
        long aboveY = KeyOperators.getMaskForKeyAboveY(emptyMinY);
        long belowY = KeyOperators.getMaskForKeyBelowY(emptyMaxY + 1);

        // 一番下のラインから回転軸へ移動する
        int y = emptyMinY - minY;

        int minX = minoDiff.getMinX();
        int blockCount = minoDiff.getBlockCount();
        return new MinoDiffWithKey(mino, minX, y, filledLine & aboveY & belowY, blockCount, dy);
    }
}

 */
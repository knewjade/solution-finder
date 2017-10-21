package searcher.pack.separable_mino;

import common.iterable.CombinationIterable;
import core.field.KeyOperators;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class SeparableMinoFactory {
    private final EnumMap<Block, EnumMap<Rotate, List<SeparableMino>>> maps;

    public SeparableMinoFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight, long deleteKeyMask) {
        this.maps = initializeMaps(minoFactory, minoShifter, fieldWidth, fieldHeight, deleteKeyMask);
    }

    private EnumMap<Block, EnumMap<Rotate, List<SeparableMino>>> initializeMaps(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight, long deleteKeyMask) {
        EnumMap<Block, EnumMap<Rotate, List<SeparableMino>>> maps = new EnumMap<>(Block.class);
        for (Block block : Block.values()) {
            EnumMap<Rotate, List<SeparableMino>> rotateMaps = maps.computeIfAbsent(block, blk -> new EnumMap<>(Rotate.class));
            for (Rotate originRotate : Rotate.values()) {
                Rotate rotate = minoShifter.createTransformedRotate(block, originRotate);
                if (rotateMaps.containsKey(rotate))
                    continue;

                Mino mino = minoFactory.create(block, rotate);

                // ミノの高さを計算
                int minoHeight = mino.getMaxY() - mino.getMinY() + 1;

                // フィールドの高さ以上にミノを使う場合はおけない
                if (fieldHeight < minoHeight)
                    continue;

                // 行候補をリストにする
                ArrayList<Integer> lineIndexes = new ArrayList<>();
                for (int index = 0; index < fieldHeight; index++)
                    lineIndexes.add(index);

                // ブロックが置かれる行を選択する
                CombinationIterable<Integer> combinationIterable = new CombinationIterable<>(lineIndexes, minoHeight);

                // リストアップ
                ArrayList<SeparableMino> deleteLimitedMinos = new ArrayList<>();
                for (List<Integer> indexes : combinationIterable) {
                    // ソートする
                    indexes.sort(Integer::compare);

                    // 一番下の行と一番上の行を取得
                    int lowerY = indexes.get(0);
                    int upperY = indexes.get(indexes.size() - 1);

                    // ミノに挟まれる全ての行を含むdeleteKey
                    long deleteKey = KeyOperators.getMaskForKeyAboveY(lowerY) & KeyOperators.getMaskForKeyBelowY(upperY + 1);
                    long usingKey = 0L;

                    assert Long.bitCount(deleteKey) == upperY - lowerY + 1;

                    for (Integer index : indexes) {
                        long bitKey = KeyOperators.getDeleteBitKey(index);

                        // ブロックのある行のフラグを取り消す
                        deleteKey &= ~bitKey;

                        // ブロックのある行にフラグをたてる
                        usingKey |= bitKey;
                    }

                    assert Long.bitCount(deleteKey) + indexes.size() == upperY - lowerY + 1;

                    if ((deleteKeyMask & deleteKey) == deleteKey)
                        for (int x = -mino.getMinX(); x < fieldWidth - mino.getMinX(); x++)
                            deleteLimitedMinos.add(SeparableMino.create(mino, deleteKey, usingKey, x, lowerY, upperY, fieldHeight));
                }

                rotateMaps.put(rotate, deleteLimitedMinos);
            }
        }

        return maps;
    }

    public List<SeparableMino> create() {
        List<SeparableMino> all = new ArrayList<>();
        for (Block block : Block.values())
            for (List<SeparableMino> minos : maps.get(block).values())
                all.addAll(minos);
        return all;
    }
}

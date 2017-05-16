package _experimental.allcomb;

import _experimental.newfield.step2.DeleteKey;
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

class DeleteKeyFactory {
    private final EnumMap<Block, EnumMap<Rotate, List<DeleteKey>>> maps;
    private final MinoShifter minoShifter;

    DeleteKeyFactory(MinoFactory minoFactory, MinoShifter minoShifter, int height) {
        this.minoShifter = minoShifter;
        this.maps = initializeMaps(minoFactory, minoShifter, height);
    }

    private EnumMap<Block, EnumMap<Rotate, List<DeleteKey>>> initializeMaps(MinoFactory minoFactory, MinoShifter minoShifter, int maxClearLine) {
        EnumMap<Block, EnumMap<Rotate, List<DeleteKey>>> maps = new EnumMap<>(Block.class);
        for (Block block : Block.values()) {
            EnumMap<Rotate, List<DeleteKey>> rotateMaps = maps.computeIfAbsent(block, blk -> new EnumMap<>(Rotate.class));
            for (Rotate originRotate : Rotate.values()) {
                Rotate rotate = minoShifter.createTransformedRotate(block, originRotate);
                if (rotateMaps.containsKey(rotate))
                    continue;

                Mino mino = minoFactory.create(block, rotate);

                // ミノの高さを計算
                int minoHeight = mino.getMaxY() - mino.getMinY() + 1;

                // 行候補をリストにする
                ArrayList<Integer> lineIndexes = new ArrayList<>();
                for (int index = 0; index < maxClearLine; index++)
                    lineIndexes.add(index);

                // ブロックが置かれる行を選択する
                CombinationIterable<Integer> combinationIterable = new CombinationIterable<>(lineIndexes, minoHeight);

                // リストアップ
                ArrayList<DeleteKey> deleteLimitedMinos = new ArrayList<>();
                for (List<Integer> indexes : combinationIterable) {
                    // ソートする
                    indexes.sort(Integer::compare);

                    // 一番下の行と一番上の行を取得
                    int lowerY = indexes.get(0);
                    int upperY = indexes.get(indexes.size() - 1);

                    // ミノに挟まれる全ての行を含むdeleteKey
                    long deleteKey = KeyOperators.getMaskForKeyAboveY(lowerY) & KeyOperators.getMaskForKeyBelowY(upperY + 1);

                    assert Long.bitCount(deleteKey) == upperY - lowerY + 1;

                    // ブロックのある行のフラグを取り消す
                    for (Integer index : indexes)
                        deleteKey &= ~KeyOperators.getDeleteBitKey(index);

                    assert Long.bitCount(deleteKey) + indexes.size() == upperY - lowerY + 1;

                    deleteLimitedMinos.add(DeleteKey.create(mino, deleteKey, lowerY, upperY));
                }

                rotateMaps.put(rotate, deleteLimitedMinos);
            }
        }

        return maps;
    }

    List<DeleteKey> parse(Block block, Rotate rotate) {
        Rotate transformedRotate = minoShifter.createTransformedRotate(block, rotate);
        return maps.get(block).get(transformedRotate);
    }

    List<DeleteKey> parse(Mino mino) {
        return parse(mino.getBlock(), mino.getRotate());
    }
}

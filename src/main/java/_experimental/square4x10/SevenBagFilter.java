package _experimental.square4x10;

import common.datastore.BlockCounter;
import core.mino.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

class SevenBagFilter {
    static boolean isIn7Bag(BlockCounter blockCounter) {
        assert blockCounter.getBlockStream().count() <= 10;

        // ミノの個数（最も多い・2番めに多い）を取得
        EnumMap<Block, Integer> map = blockCounter.getEnumMap();
        List<Integer> values = new ArrayList<>(map.values());
        values.add(0);  // ミノが2種類以下の場合はこの0を取得する
        values.add(0);
        values.sort(Comparator.reverseOrder());

        int first = values.get(0);
        int second = values.get(1);
        int third = values.get(2);

        return isIn7Bag(first, second, third);
    }

    /*
    判定の条件は以下の通り (A,B,C は使用されているのが多い順のミノの個数)
        AAAA BB C ****
        AAA BBB C ****
        AAA BB ******
        AA *********
         */
    private static boolean isIn7Bag(int first, int second, int third) {
        return (first == 4 && second <= 2 && third <= 1) ||
                (first == 3 && second == 3 && third <= 1) ||
                (first == 3 && second <= 2) ||
                (first <= 2);
    }
}

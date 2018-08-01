package common.iterable;

import com.google.common.collect.Iterables;
import common.datastore.PieceCounter;
import core.mino.Piece;
import lib.MyIterables;
import lib.Randoms;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class CombinationIterableTest {
    @Test
    void iterator10C5() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 5);
        List<List<Integer>> lists = MyIterables.toList(iterable);
        assertThat(lists).hasSize(252);  // 10C5
    }

    @Test
    void iterator10C3() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 3);
        List<List<Integer>> lists = MyIterables.toList(iterable);
        assertThat(lists).hasSize(120);  // 10C3
    }

    @Test
    void iterator15C6() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 6);
        List<List<Integer>> lists = MyIterables.toList(iterable);
        assertThat(lists).hasSize(5005);  // 15C6
    }

    @Test
    void iterator5000C9999() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        for (int count = 0; count < 5000; count++)
            list.add(count);

        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 5000 - 1);
        List<List<Integer>> lists = MyIterables.toList(iterable);
        assertThat(lists).hasSize(5000);  // 5000C4999
    }

    @Test
    void iteratorRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 35; size++) {
            int pop = randoms.nextIntOpen(1, size <= 10 ? size : 10);

            List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());
            CombinationIterable<Integer> iterable = new CombinationIterable<>(list, pop);
            long actual = StreamSupport.stream(iterable.spliterator(), false).count();

            long upper = 1L;
            long bottom = 1L;
            for (int count = 0; count < (pop < (size - pop) ? pop : size - pop); count++) {
                upper *= size - count;
                bottom *= count + 1;
            }
            assertThat(actual)
                    .as("size=%d, pop=%d", size, pop)
                    .isEqualTo(upper / bottom);
        }
    }

    @Test
    void iteratorRandomBlockCount() throws Exception {
        Randoms randoms = new Randoms();
        ArrayList<Piece> allPieces = Lists.newArrayList(Iterables.concat(Piece.valueList(), Piece.valueList()));
        for (int pop = 1; pop <= 14; pop++) {
            CombinationIterable<Piece> iterable = new CombinationIterable<>(allPieces, pop);
            Set<PieceCounter> sets = StreamSupport.stream(iterable.spliterator(), false)
                    .map(PieceCounter::new)
                    .collect(Collectors.toSet());

            // ランダムに組み合わせを選択し、必ず列挙したセットの中にあることを確認
            for (int count = 0; count < 10000; count++) {
                List<Piece> combinations = randoms.sample(allPieces, pop);
                PieceCounter pieceCounter = new PieceCounter(combinations);
                assertThat(pieceCounter).isIn(sets);
            }
        }
    }
}
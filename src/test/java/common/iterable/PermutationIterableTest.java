package common.iterable;

import com.google.common.collect.Iterables;
import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import lib.Randoms;
import module.LongTest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class PermutationIterableTest {
    @Test
    void iterator10P3() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        PermutationIterable<Integer> iterable = new PermutationIterable<>(list, 3);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists).hasSize(720);  // 10P3
    }

    private <T> List<T> parseToList(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        for (T element : iterable)
            list.add(element);
        return list;
    }

    @Test
    void iterator10P5() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        PermutationIterable<Integer> iterable = new PermutationIterable<>(list, 5);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists).hasSize(30240);  // 10P5
    }

    @Test
    void iterator15P4() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        PermutationIterable<Integer> iterable = new PermutationIterable<>(list, 4);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists).hasSize(32760);  // 10P5
    }

    @Test
    void iteratorRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 15; size++) {
            int pop = randoms.nextInt(1, size <= 8 ? size : 8);

            List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());
            PermutationIterable<Integer> iterable = new PermutationIterable<>(list, pop);
            long actual = StreamSupport.stream(iterable.spliterator(), false).count();

            long upper = 1L;
            for (int count = 0; count < pop; count++)
                upper *= size - count;
            assertThat(actual)
                    .as("size=%d, pop=%d", size, pop)
                    .isEqualTo(upper);
        }
    }

    @Test
    @LongTest
    void iteratorRandomBlock() throws Exception {
        Randoms randoms = new Randoms();
        ArrayList<Piece> allPieces = Lists.newArrayList(Iterables.concat(Piece.valueList(), Piece.valueList()));
        for (int pop = 1; pop <= 7; pop++) {
            PermutationIterable<Piece> iterable = new PermutationIterable<>(allPieces, pop);
            HashSet<LongPieces> sets = StreamSupport.stream(iterable.spliterator(), false)
                    .map(Collection::stream)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            // ランダムにサンプルを選択し、必ず列挙したセットの中にあることを確認
            for (int count = 0; count < 1000; count++) {
                List<Piece> sample = randoms.sample(allPieces, pop);
                LongPieces pieces = new LongPieces(sample);
                assertThat(pieces).isIn(sets);
            }
        }
    }
}
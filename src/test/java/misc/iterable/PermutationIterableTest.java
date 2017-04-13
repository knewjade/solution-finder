package misc.iterable;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PermutationIterableTest {
    @Test
    public void iterator10P3() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        PermutationIterable<Integer> iterable = new PermutationIterable<>(list, 3);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(720));  // 10P3
    }

    private <T> List<T> parseToList(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        for (T element : iterable)
            list.add(element);
        return list;
    }

    @Test
    public void iterator10P5() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        PermutationIterable<Integer> iterable = new PermutationIterable<>(list, 5);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(30240));  // 10P5
    }

    @Test
    public void iterator15P4() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        PermutationIterable<Integer> iterable = new PermutationIterable<>(list, 4);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(32760));  // 10P5
    }
}
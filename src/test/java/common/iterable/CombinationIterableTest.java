package common.iterable;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CombinationIterableTest {
    @Test
    public void iterator10C5() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 5);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(252));  // 10C5
    }

    private <T> List<T> parseToList(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        for (T element : iterable)
            list.add(element);
        return list;
    }

    @Test
    public void iterator10C3() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 3);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(120));  // 10C3
    }

    @Test
    public void iterator15C6() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 6);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(5005));  // 15C6
    }

    @Test
    public void iterator5000C9999() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        for (int count = 0; count < 5000; count++)
            list.add(count);

        CombinationIterable<Integer> iterable = new CombinationIterable<>(list, 5000 - 1);
        List<List<Integer>> lists = parseToList(iterable);
        assertThat(lists.size(), is(5000));  // 5000C9999
    }
}
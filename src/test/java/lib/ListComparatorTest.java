package lib;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ListComparatorTest {
    @Test
    void compare() {
        ListComparator<Integer> comparator = new ListComparator<>(Integer::compare);

        // assert first element
        assertThat(comparator.compare(Collections.singletonList(1), Collections.singletonList(2))).isLessThan(0);
        assertThat(comparator.compare(Collections.singletonList(2), Collections.singletonList(1))).isGreaterThan(0);

        // assert second element
        assertThat(comparator.compare(Arrays.asList(1, 1), Arrays.asList(1, 2))).isLessThan(0);
        assertThat(comparator.compare(Arrays.asList(1, 2), Arrays.asList(1, 1))).isGreaterThan(0);

        // assert size of list
        assertThat(comparator.compare(Collections.singletonList(1), Arrays.asList(1, 2))).isLessThan(0);
        assertThat(comparator.compare(Arrays.asList(1, 2), Collections.singletonList(1))).isGreaterThan(0);

        // assert equal
        assertThat(comparator.compare(Collections.singletonList(1), Collections.singletonList(1))).isEqualTo(0);
        assertThat(comparator.compare(Arrays.asList(1, 2), Arrays.asList(1, 2))).isEqualTo(0);
    }
}
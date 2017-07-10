package lib;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

class CombineComparatorTest {
    @Test
    void compare() throws Exception {
        Comparator<String> first = Comparator.comparingInt(String::length);
        Comparator<String> second = Comparator.comparingInt(o -> o.charAt(0));
        CombineComparator<String> combineComparator = CombineComparator.first(first).andThen(second);

        // assert first key
        assertThat(combineComparator.compare("1", "10")).isLessThan(0);
        assertThat(combineComparator.compare("10", "1")).isGreaterThan(0);

        // assert second key
        assertThat(combineComparator.compare("01", "10")).isLessThan(0);
        assertThat(combineComparator.compare("10", "01")).isGreaterThan(0);

        // assert equal
        assertThat(combineComparator.compare("100", "100")).isEqualTo(0);
    }
}
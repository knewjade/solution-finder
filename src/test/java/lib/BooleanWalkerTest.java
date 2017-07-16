package lib;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

class BooleanWalkerTest {
    @Test
    void walkHasSize() {
        for (int size = 1; size <= 12; size++)
            assertThat(BooleanWalker.walk(size)).hasSize((int) Math.pow(2.0, size));
    }

    @Test
    void walk4() {
        assertThat(BooleanWalker.walk(4).collect(Collectors.toList()))
                .hasSize(16)
                .contains(Arrays.asList(true, true, true, true))
                .contains(Arrays.asList(true, true, true, false))
                .contains(Arrays.asList(true, true, false, true))
                .contains(Arrays.asList(true, true, false, false))
                .contains(Arrays.asList(true, false, true, true))
                .contains(Arrays.asList(true, false, true, false))
                .contains(Arrays.asList(true, false, false, true))
                .contains(Arrays.asList(true, false, false, false))
                .contains(Arrays.asList(false, true, true, true))
                .contains(Arrays.asList(false, true, true, false))
                .contains(Arrays.asList(false, true, false, true))
                .contains(Arrays.asList(false, true, false, false))
                .contains(Arrays.asList(false, false, true, true))
                .contains(Arrays.asList(false, false, true, false))
                .contains(Arrays.asList(false, false, false, true))
                .contains(Arrays.asList(false, false, false, false));
    }
}
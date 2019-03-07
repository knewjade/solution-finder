package searcher.spins;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SolutionsTest {
    @SafeVarargs
    private final <T> Set<T> toSet(T... integers) {
        return new HashSet<>(Arrays.asList(integers));
    }

    @Test
    void contains() {
        Solutions<Integer> solutions = new Solutions<>();
        solutions.add(toSet(1, 2, 3));

        assertThat(solutions)
                .returns(true, it -> it.contains(toSet(1, 2, 3)))
                .returns(false, it -> it.contains(toSet(1)))
                .returns(false, it -> it.contains(toSet(1, 2)))
                .returns(false, it -> it.contains(toSet(1, 2, 4)))
        ;
    }

    @Test
    void partialContains() {
        Solutions<Integer> solutions = new Solutions<>();
        solutions.add(toSet(1, 2, 3));
        solutions.add(toSet(1, 2, 4));

        assertThat(solutions)
                .returns(false, it -> it.partialContains(toSet(), 1))
                .returns(false, it -> it.partialContains(toSet(1), 2))
                .returns(true, it -> it.partialContains(toSet(1, 2), 3))
                .returns(true, it -> it.partialContains(toSet(1, 2, 3), 4))
                .returns(true, it -> it.partialContains(toSet(2, 4, 5), 1))
                .returns(false, it -> it.partialContains(toSet(1, 2, 3), 5))
        ;
    }
}
package common.order;

import core.mino.Piece;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReverseOrderExcludingNoHoldLookUpTest {
    @Test
    void parse() {
        ReverseOrderExcludingNoHoldLookUp lookUp = new ReverseOrderExcludingNoHoldLookUp(3, 4);
        List<List<Piece>> collect = lookUp.parse(Arrays.asList(Piece.T, Piece.J, Piece.S))
                .map(it -> it.collect(Collectors.toList()))
                .collect(Collectors.toList());

        assertThat(collect).hasSize(7);  // 2^3 - 1

        assertThat(collect).contains(Arrays.asList(null, Piece.T, Piece.J, Piece.S));
        assertThat(collect).contains(Arrays.asList(Piece.T, null, Piece.J, Piece.S));
        assertThat(collect).contains(Arrays.asList(Piece.J, Piece.T, null, Piece.S));
        assertThat(collect).contains(Arrays.asList(Piece.T, Piece.J, null, Piece.S));
        assertThat(collect).contains(Arrays.asList(Piece.S, Piece.T, Piece.J, null));
        assertThat(collect).contains(Arrays.asList(Piece.T, Piece.S, Piece.J, null));
        assertThat(collect).contains(Arrays.asList(Piece.J, Piece.T, Piece.S, null));

        assertThat(collect).doesNotContain(Arrays.asList(Piece.T, Piece.J, Piece.S, null));
    }
}
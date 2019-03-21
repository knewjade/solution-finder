package common.order;

import core.mino.Piece;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CountReverseOrderLookUpStartsWithAnyTest {
    @Test
    void parse() {
        CountReverseOrderLookUpStartsWithAny lookUp = new CountReverseOrderLookUpStartsWithAny(3, 4);
        List<WithHoldCount<Piece>> collect = lookUp.parse(Arrays.asList(Piece.T, Piece.J, Piece.S))
                .collect(Collectors.toList());

        assertThat(collect).hasSize(8);  // 2^3

        // 最初のピースをホールドからスタート
        contains(collect, toPair(Arrays.asList(null, Piece.T, Piece.J, Piece.S), 0));
        contains(collect, toPair(Arrays.asList(Piece.T, null, Piece.J, Piece.S), 1));
        contains(collect, toPair(Arrays.asList(Piece.J, Piece.T, null, Piece.S), 1));
        contains(collect, toPair(Arrays.asList(Piece.T, Piece.J, null, Piece.S), 2));
        contains(collect, toPair(Arrays.asList(Piece.S, Piece.T, Piece.J, null), 1));
        contains(collect, toPair(Arrays.asList(Piece.T, Piece.S, Piece.J, null), 2));
        contains(collect, toPair(Arrays.asList(Piece.J, Piece.T, Piece.S, null), 2));
        contains(collect, toPair(Arrays.asList(Piece.T, Piece.J, Piece.S, null), 3));
    }

    private void contains(List<WithHoldCount<Piece>> collect, WithHoldCount<Piece> expected) {
        List<Piece> expectedPieces = expected.getList();
        for (WithHoldCount<Piece> pair : collect) {
            if (expectedPieces.equals(pair.getList())) {
                assertThat(pair.getHoldCount())
                        .as(expected + " : " + collect)
                        .isEqualTo(expected.getHoldCount());

            }
        }
    }

    private WithHoldCount<Piece> toPair(List<Piece> pieces, int holdCount) {
        return new WithHoldCount<>(pieces, holdCount);
    }
}
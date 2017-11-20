package common.order;

import core.mino.Piece;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LongStackOrderTest {
    @Test
    void add() throws Exception {
        LongStackOrder stackOrder = new LongStackOrder();
        stackOrder.addLast(Piece.S);
        stackOrder.addLast(Piece.Z);
        stackOrder.addLast(Piece.O);
        stackOrder.addLastTwo(Piece.I);

        List<Piece> pieces = stackOrder.toList();
        assertThat(pieces).isEqualTo(Arrays.asList(Piece.S, Piece.Z, Piece.I, Piece.O));
    }

    @Test
    void stock() throws Exception {
        LongStackOrder stackOrder = new LongStackOrder();
        stackOrder.addLast(Piece.S);
        stackOrder.stock(Piece.T);  // to head and memory TS*
        stackOrder.addLast(Piece.Z);
        stackOrder.addLastTwo(Piece.O);
        stackOrder.stock(Piece.I);
        stackOrder.stock(null);

        List<Piece> pieces = stackOrder.toList();
        assertThat(pieces).isEqualTo(Arrays.asList(Piece.T, Piece.S, Piece.I, Piece.O, Piece.Z, null));
    }
}
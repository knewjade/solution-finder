package common.parser;

import core.mino.Piece;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;

class PieceInterpreterTest {
    @Test
    void parse10WithJust() throws Exception {
        Stream<Piece> stream = BlockInterpreter.parse10("TIJLOSZTIJ");
        assertThat(stream).containsExactly(T, I, J, L, O, S, Z, T, I, J);
    }

    @Test
    void parse10Over() throws Exception {
        Stream<Piece> stream = BlockInterpreter.parse10("SJOTLZOJSZIJLTIO");
        assertThat(stream).containsExactly(S, J, O, T, L, Z, O, J, S, Z);
    }

    @Test
    void parse11Just() throws Exception {
        Stream<Piece> stream = BlockInterpreter.parse11("ZLSJZLSJTIO");
        assertThat(stream).containsExactly(Z, L, S, J, Z, L, S, J, T, I, O);
    }

    @Test
    void parse11Over() throws Exception {
        Stream<Piece> stream = BlockInterpreter.parse11("LZISTOJLZISTOJ");
        assertThat(stream).containsExactly(L, Z, I, S, T, O, J, L, Z, I, S);
    }

    @Test
    void parseWhenSizeIs10() throws Exception {
        Stream<Piece> stream = BlockInterpreter.parse("SZSZSIOIOI");
        assertThat(stream).containsExactly(S, Z, S, Z, S, I, O, I, O, I);
    }

    @Test
    void parseWhenSizeIs11() throws Exception {
        Stream<Piece> stream = BlockInterpreter.parse("LLLJJTTTTTO");
        assertThat(stream).containsExactly(L, L, L, J, J, T, T, T, T, T, O);
    }

    @Test
    void parseRandom() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 100);
            List<Piece> pieces = randoms.blocks(size);
            String name = pieces.stream()
                    .map(Piece::getName)
                    .collect(Collectors.joining());
            Stream<Piece> stream = BlockInterpreter.parse(name);
            assertThat(stream).containsExactlyElementsOf(pieces);
        }
    }
}
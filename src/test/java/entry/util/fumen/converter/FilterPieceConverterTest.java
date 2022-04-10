package entry.util.fumen.converter;

import common.tetfu.common.ColorConverter;
import core.mino.MinoFactory;
import core.mino.Piece;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilterPieceConverterTest {
    @Test
    void case1() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.T);
        assertThat(converter.parse("vhGKJJRQJUGJvMJTNJFKJ+NJ")).isEqualTo("v115@vhFKJJRwBUmBvsBTtB+jB");
    }

    @Test
    void case2() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.O);
        assertThat(converter.parse("vhGKJJRQJUGJvMJTNJFKJ+NJ")).isEqualTo("v115@vhFKJJRwBUmBvsBFqB+jB");
    }

    @Test
    void case3() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.J);
        assertThat(converter.parse("vhGKJJRwBUmBvsBTtB+jBFqB")).isEqualTo("v115@vhFKJJRwBUmBvsBTtBFqB");
    }

    @Test
    void case4() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.L);
        assertThat(converter.parse("9gAtE8DeQpQ4D8Deglg0D8DewhwwD8NeKMJvhCUNJG?NJxRJ"))
                .isEqualTo("v115@9gAtE8DeQpQ4D8Deglg0D8DewhwwD8NeUNJvhBGoBx?iB");
    }

    @Test
    void noLock() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.T);
        assertThat(converter.parse("vhLRQJKJJPGnvMnv/ITNJGMJNnmNAnNKnFKJUHJ"))
                .isEqualTo("v115@vhFRQJKpBvfBTtBGsBUiB");
    }

    @Test
    void withComment() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.T);
        assertThat(converter.parse("vhGKJYBARAAAARwQBASAAAAUmQBATAAAAvsQBAUAAA?ATtQBAVAAAAFqQBAWAAAA+tQBAXAAAA")).isEqualTo("v115@vhFKJYBARAAAARwQBASAAAAUmQBATAAAAvsQBAUAAA?ATtQBAVAAAA+jQBAXAAAA");
    }

    @Test
    void empty() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.T);
        assertThat(converter.parse("vhAAgH")).isEqualTo("v115@vhAAgH");
    }

    @Test
    void tOnly() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.T);
        assertThat(converter.parse("vhA1QJ")).isEqualTo("v115@vhAAgH");
    }

    @Test
    void noT() throws Exception {
        FilterPieceConverter converter = new FilterPieceConverter(new MinoFactory(), new ColorConverter(), Piece.T);
        assertThat(converter.parse("vhFKJJRwBUmBvsBTtB+jB")).isEqualTo("v115@vhFKJJRwBUmBvsBTtB+jB");
    }
}
package entry.searching_pieces;

import common.datastore.pieces.LongPieces;
import common.pattern.PiecesGenerator;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class HoldBreakEnumeratePiecesTest {
    @Test
    void enumerate1() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerate2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 4);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateOverAny() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("T, J, O, Z");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(8);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z",
                "T, O, J, T",
                "T, J, O, Z"
        ));
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(13);
        assertThat(core.getCounter()).isEqualTo(3);
    }

    @Test
    void enumerateJust() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p3");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(210);
    }

    @Test
    void enumerateJustAny() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("T, O, S");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(4);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateJustRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            String pattern = blocks.stream()
                    .map(Block::getName)
                    .collect(Collectors.joining(","));
            PiecesGenerator piecesGenerator = new PiecesGenerator(pattern);
            HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(piecesGenerator, size);
            Set<LongPieces> pieces = core.enumerate();

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean(0.3)) {
                        // そのまま追加
                        sample.add(blocks.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(blocks.get(holdIndex));
                        holdIndex = index;
                    }
                }

                // ホールドを追加
                sample.add(blocks.get(holdIndex));

                assertThat(new LongPieces(sample)).isIn(pieces);
            }
        }
    }

    @Test
    void enumerateOverRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 15; size++) {
            List<Block> blocks = randoms.blocks(size);
            String pattern = blocks.stream()
                    .map(Block::getName)
                    .collect(Collectors.joining(","));
            PiecesGenerator piecesGenerator = new PiecesGenerator(pattern);
            HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(piecesGenerator, size - 1);
            Set<LongPieces> pieces = core.enumerate();

            for (int count = 0; count < 10000; count++) {
                ArrayList<Block> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean(0.3)) {
                        // そのまま追加
                        sample.add(blocks.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(blocks.get(holdIndex));
                        holdIndex = index;
                    }
                }

                assertThat(new LongPieces(sample)).isIn(pieces);
            }
        }
    }
}
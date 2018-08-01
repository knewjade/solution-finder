package common.datastore;

import core.mino.Piece;
import lib.Randoms;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PieceCounterTest {
    @Test
    void testEmpty() throws Exception {
        List<Piece> emptyList = Collections.emptyList();
        PieceCounter counter = new PieceCounter(emptyList);
        assertThat(counter.getCounter()).isEqualTo(0L);
    }

    @Test
    void testAdd() throws Exception {
        PieceCounter counter = new PieceCounter(Arrays.asList(Piece.I, Piece.J));
        PieceCounter actual = counter.addAndReturnNew(Collections.singletonList(Piece.T));

        assertThat(counter.getCounter()).isEqualTo(new PieceCounter(Arrays.asList(Piece.I, Piece.J)).getCounter());
        assertThat(actual.getCounter()).isEqualTo(new PieceCounter(Arrays.asList(Piece.I, Piece.T, Piece.J)).getCounter());
    }

    @Test
    void testAdd2() throws Exception {
        PieceCounter counter1 = new PieceCounter(Arrays.asList(Piece.I, Piece.J, Piece.T));
        PieceCounter counter2 = new PieceCounter(Arrays.asList(Piece.I, Piece.J, Piece.O));
        PieceCounter actual = counter1.addAndReturnNew(counter2);
        assertThat(actual.getCounter()).isEqualTo(new PieceCounter(Arrays.asList(Piece.I, Piece.I, Piece.J, Piece.J, Piece.T, Piece.O)).getCounter());
    }


    @Test
    void testRemove() throws Exception {
        PieceCounter counter = new PieceCounter(Arrays.asList(Piece.I, Piece.J, Piece.I, Piece.L));
        PieceCounter actual = counter.removeAndReturnNew(new PieceCounter(Arrays.asList(Piece.I, Piece.J)));

        assertThat(counter.getCounter()).isEqualTo(new PieceCounter(Arrays.asList(Piece.I, Piece.J, Piece.I, Piece.L)).getCounter());
        assertThat(actual.getCounter()).isEqualTo(new PieceCounter(Arrays.asList(Piece.I, Piece.L)).getCounter());
    }

    @Test
    void testGet() throws Exception {
        List<Piece> pieces = Arrays.asList(Piece.I, Piece.J, Piece.T, Piece.S);
        PieceCounter counter = new PieceCounter(pieces);

        assertThat(counter.getBlockStream()).containsExactlyElementsOf(counter.getBlocks());
    }

    @Test
    void testGetMap() throws Exception {
        List<Piece> pieces = Arrays.asList(Piece.I, Piece.J, Piece.T, Piece.I, Piece.I, Piece.T, Piece.S);
        PieceCounter counter = new PieceCounter(pieces);
        EnumMap<Piece, Integer> map = counter.getEnumMap();
        assertThat(map.get(Piece.I)).isEqualTo(3);
        assertThat(map.get(Piece.T)).isEqualTo(2);
        assertThat(map.get(Piece.S)).isEqualTo(1);
        assertThat(map.get(Piece.J)).isEqualTo(1);
        assertThat(map.get(Piece.L)).isNull();
        assertThat(map.get(Piece.Z)).isNull();
        assertThat(map.get(Piece.O)).isNull();
    }

    @Test
    void testRandom() throws Exception {
        Randoms randoms = new Randoms();

        LOOP:
        for (int count = 0; count < 10000; count++) {
            List<Piece> pieces = randoms.blocks(randoms.nextIntOpen(0, 500));
            Map<Piece, List<Piece>> group = pieces.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Piece> eachPiece : group.values())
                if (128 <= eachPiece.size())
                    continue LOOP;

            PieceCounter pieceCounter = new PieceCounter(pieces);
            EnumMap<Piece, Integer> counterMap = pieceCounter.getEnumMap();
            for (Piece piece : Piece.values()) {
                int expected = group.getOrDefault(piece, Collections.emptyList()).size();
                assertThat(counterMap.getOrDefault(piece, 0)).isEqualTo(expected);
            }
        }
    }

    @Test
    void testContainsAll1() {
        PieceCounter counter1 = new PieceCounter(Collections.singletonList(Piece.T));
        PieceCounter counter2 = new PieceCounter(Arrays.asList(Piece.T, Piece.I));
        assertThat(counter1.containsAll(counter2)).isFalse();
        assertThat(counter2.containsAll(counter1)).isTrue();
    }

    @Test
    void testContainsAll2() {
        PieceCounter counter1 = new PieceCounter(Arrays.asList(Piece.S, Piece.Z, Piece.T));
        PieceCounter counter2 = new PieceCounter(Arrays.asList(Piece.Z, Piece.T, Piece.S));
        assertThat(counter1.containsAll(counter2)).isTrue();
        assertThat(counter2.containsAll(counter1)).isTrue();
    }

    @Test
    void testContainsAllRandom() throws Exception {
        Randoms randoms = new Randoms();

        LOOP:
        for (int count = 0; count < 10000; count++) {
            List<Piece> blocks1 = randoms.blocks(randoms.nextIntOpen(0, 500));
            Map<Piece, List<Piece>> group1 = blocks1.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Piece> eachPiece : group1.values())
                if (128 <= eachPiece.size())
                    continue LOOP;

            List<Piece> blocks2 = randoms.blocks(randoms.nextIntOpen(0, 500));
            Map<Piece, List<Piece>> group2 = blocks2.stream().collect(Collectors.groupingBy(Function.identity()));

            for (List<Piece> eachPiece : group2.values())
                if (128 <= eachPiece.size())
                    continue LOOP;

            boolean isChild1 = true;
            boolean isChild2 = true;
            List<Piece> empty = Lists.emptyList();
            for (Piece piece : Piece.values()) {
                isChild1 &= group1.getOrDefault(piece, empty).size() <= group2.getOrDefault(piece, empty).size();
                isChild2 &= group2.getOrDefault(piece, empty).size() <= group1.getOrDefault(piece, empty).size();
            }

            PieceCounter counter1 = new PieceCounter(blocks1);
            PieceCounter counter2 = new PieceCounter(blocks2);
            assertThat(counter1.containsAll(counter2)).isEqualTo(isChild2);
            assertThat(counter2.containsAll(counter1)).isEqualTo(isChild1);
        }
    }
}
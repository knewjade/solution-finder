package common.order;

import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReverseOrderLookUpTest {
    @Test
    void parseJustBlocksCount() throws Exception {
        List<Piece> pieceList = Piece.valueList();
        int toDepth = pieceList.size();

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(pieceList.size(), toDepth);
        long count = lookUp.parse(pieceList).count();

        assertThat(count).isEqualTo(64);
    }

    @Test
    void parseOverBlocksCount() throws Exception {
        List<Piece> pieceList = Piece.valueList();
        int toDepth = pieceList.size() + 1;

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(pieceList.size(), toDepth);
        long count = lookUp.parse(pieceList).count();

        assertThat(count).isEqualTo(128);
    }

    @Test
    void parseOver() throws Exception {
        List<Piece> pieceList = Arrays.asList(Piece.I, Piece.T, Piece.Z, Piece.O, Piece.I, Piece.L);
        int fromDepth = pieceList.size() + 1;

        Comparator<List<Piece>> comparator = (o1, o2) -> {
            int size1 = o1.size();
            int size2 = o2.size();
            int compareSize = Integer.compare(size1, size2);
            if (compareSize != 0)
                return compareSize;

            Comparator<Piece> blockComparator = Comparator.nullsLast(Enum::compareTo);
            for (int index = 0; index < size1; index++) {
                int compare = blockComparator.compare(o1.get(index), o2.get(index));
                if (compare != 0)
                    return compare;
            }

            return 0;
        };

        List<List<Piece>> reverse1 = OrderLookup.reverseBlocks(pieceList, fromDepth).stream()
                .map(StackOrder::toList)
                .sorted(comparator)
                .collect(Collectors.toList());

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(pieceList.size(), fromDepth);
        List<List<Piece>> reverse2 = lookUp.parse(pieceList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(reverse2).isEqualTo(reverse1);
    }

    @Test
    void parseJustRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 13; size++) {
            List<Piece> blocks = randoms.blocks(size);
            int fromDepth = blocks.size();

            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(blocks.size(), fromDepth);
            List<LongPieces> reverse = reverseOrderLookUp.parse(blocks)
                    .map(LongPieces::new)
                    .collect(Collectors.toList());

            LongPieces target = new LongPieces(blocks);
            ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(blocks.size(), fromDepth);
            for (LongPieces pieces : reverse) {
                boolean isFound = forwardOrderLookUp.parse(pieces.getPieces())
                        .map(LongPieces::new)
                        .anyMatch(target::equals);
                assertThat(isFound).isTrue();
            }
        }
    }

    @Test
    @Tag("long")
    void parseOverRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 13; size++) {
            List<Piece> pieces = randoms.blocks(size);
            int fromDepth = pieces.size() + 1;

            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(pieces.size(), fromDepth);
            List<Stream<Piece>> reverse = reverseOrderLookUp.parse(pieces)
                    .collect(Collectors.toList());

            LongPieces target = new LongPieces(pieces);
            ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(pieces.size(), fromDepth);
            for (Stream<Piece> stream : reverse) {
                List<Piece> sample = stream
                        .map(block -> block != null ? block : randoms.block())
                        .collect(Collectors.toList());
                boolean isFound = forwardOrderLookUp.parse(sample)
                        .map(LongPieces::new)
                        .anyMatch(target::equals);
                assertThat(isFound).isTrue();
            }
        }
    }

    @Test
    void parseOver2Random() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 12; size++) {
            List<Piece> pieces = randoms.blocks(size);
            int fromDepth = pieces.size() + 2;

            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(pieces.size(), fromDepth);
            List<Stream<Piece>> reverse = reverseOrderLookUp.parse(pieces)
                    .collect(Collectors.toList());

            LongPieces target = new LongPieces(pieces);
            ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(pieces.size(), fromDepth);
            for (Stream<Piece> stream : reverse) {
                List<Piece> sample = stream
                        .map(block -> block != null ? block : randoms.block())
                        .collect(Collectors.toList());
                boolean isFound = forwardOrderLookUp.parse(sample)
                        .map(LongPieces::new)
                        .anyMatch(target::equals);
                assertThat(isFound).isTrue();
            }
        }
    }
}
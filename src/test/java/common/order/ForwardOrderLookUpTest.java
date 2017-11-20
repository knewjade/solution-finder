package common.order;

import common.comparator.PiecesNumberComparator;
import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ForwardOrderLookUpTest {
    @Test
    void parseJustBlocksCount() throws Exception {
        List<Piece> pieceList = Piece.valueList();
        int toDepth = pieceList.size();

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, pieceList.size());
        HashSet<LongPieces> forward = lookUp.parse(pieceList)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(forward).hasSize(64);
    }

    @Test
    void parseOverBlocksCount() throws Exception {
        List<Piece> pieceList = Piece.valueList();
        int toDepth = pieceList.size() - 1;

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, pieceList.size());
        HashSet<LongPieces> forward = lookUp.parse(pieceList)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(forward).hasSize(64);
    }

    @Test
    void parseJustBlocks() throws Exception {
        List<Piece> pieceList = Arrays.asList(Piece.I, Piece.T, Piece.Z, Piece.O, Piece.I, Piece.L);
        int toDepth = pieceList.size();

        PiecesNumberComparator comparator = new PiecesNumberComparator();
        List<LongPieces> forward1 = OrderLookup.forwardBlocks(pieceList, toDepth).stream()
                .map(StackOrder::toList)
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, pieceList.size());
        List<LongPieces> forward2 = lookUp.parse(pieceList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(forward2).isEqualTo(forward1);
    }

    @Test
    void parseOverBlocks() throws Exception {
        List<Piece> pieceList = Arrays.asList(Piece.I, Piece.T, Piece.Z, Piece.O, Piece.I, Piece.L);
        int toDepth = pieceList.size() - 1;

        PiecesNumberComparator comparator = new PiecesNumberComparator();
        List<LongPieces> forward1 = OrderLookup.forwardBlocks(pieceList, toDepth).stream()
                .map(StackOrder::toList)
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, pieceList.size());
        List<LongPieces> forward2 = lookUp.parse(pieceList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(forward2).isEqualTo(forward1);
    }

    @Test
    void parseJustBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 15; size++) {
            List<Piece> pieces = randoms.blocks(size);
            int toDepth = pieces.size();

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth, pieces.size());
            HashSet<LongPieces> forward = lookUp.parse(pieces)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            for (int count = 0; count < 10000; count++) {
                ArrayList<Piece> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean(0.3)) {
                        // そのまま追加
                        sample.add(pieces.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(pieces.get(holdIndex));
                        holdIndex = index;
                    }
                }

                // ホールドを追加
                sample.add(pieces.get(holdIndex));

                assertThat(new LongPieces(sample)).isIn(forward);
            }
        }
    }

    @Test
    void parseOverBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 15; size++) {
            List<Piece> pieces = randoms.blocks(size);
            int toDepth = pieces.size();

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth - 1, pieces.size());
            HashSet<LongPieces> forward = lookUp.parse(pieces)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            for (int count = 0; count < 10000; count++) {
                ArrayList<Piece> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size; index++) {
                    if (randoms.nextBoolean(0.3)) {
                        // そのまま追加
                        sample.add(pieces.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(pieces.get(holdIndex));
                        holdIndex = index;
                    }
                }

                assertThat(new LongPieces(sample)).isIn(forward);
            }
        }
    }

    @Test
    void parseOver2BlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 4; size <= 15; size++) {
            List<Piece> pieces = randoms.blocks(size);
            int toDepth = pieces.size();

            ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth - 2, pieces.size());
            HashSet<LongPieces> forward = lookUp.parse(pieces)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            for (int count = 0; count < 10000; count++) {
                ArrayList<Piece> sample = new ArrayList<>();
                int holdIndex = 0;
                for (int index = 1; index < size - 1; index++) {
                    if (randoms.nextBoolean(0.3)) {
                        // そのまま追加
                        sample.add(pieces.get(index));
                    } else {
                        // ホールドを追加
                        sample.add(pieces.get(holdIndex));
                        holdIndex = index;
                    }
                }

                assertThat(new LongPieces(sample)).isIn(forward);
            }
        }
    }
}
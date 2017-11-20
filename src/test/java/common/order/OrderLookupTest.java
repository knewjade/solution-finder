package common.order;

import common.comparator.PiecesNumberComparator;
import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import lib.ListComparator;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderLookupTest {
    @Test
    void reverseWithJustBlocks() throws Exception {
        List<Piece> pieces = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 1; depth < pieces.size(); depth++) {
            ArrayList<StackOrder<Piece>> reverse = OrderLookup.reverseBlocks(pieces.subList(0, depth), depth);
            assertThat(reverse).hasSize((int) Math.pow(2, depth - 1));
        }
    }

    @Test
    void reverseWithOverBlocks() throws Exception {
        List<Piece> pieces = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 1; depth < pieces.size(); depth++) {
            ArrayList<StackOrder<Piece>> reverse = OrderLookup.reverseBlocks(pieces.subList(0, depth), depth + 1);
            assertThat(reverse).hasSize((int) Math.pow(2, depth));
        }
    }

    @Test
    void reverseWithJustBlocks2() throws Exception {
        List<Piece> pieces = Arrays.asList(O, J, L, T, I, S, Z);
        for (int depth = 1; depth < pieces.size(); depth++) {
            ArrayList<StackOrder<Piece>> reverse = OrderLookup.reverseBlocks(pieces.subList(0, depth), depth + 1);
            assertThat(reverse).hasSize((int) Math.pow(2, depth));
        }
    }

    @Test
    void forwardWithJustBlocks() throws Exception {
        List<Piece> pieces = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 2; depth < pieces.size(); depth++) {
            ArrayList<StackOrder<Piece>> forward = OrderLookup.forwardBlocks(pieces.subList(0, depth), depth);
            assertThat(forward).hasSize((int) Math.pow(2, depth - 1));
        }
    }

    @Test
    void forwardWithLessBlocks() throws Exception {
        List<Piece> pieces = Arrays.asList(T, I, O, S, Z, J, L, T, I, O, S, Z, J, L);
        for (int depth = 3; depth < pieces.size(); depth++) {
            ArrayList<StackOrder<Piece>> forward = OrderLookup.forwardBlocks(pieces.subList(0, depth), depth - 1);
            assertThat(forward).hasSize((int) Math.pow(2, depth - 1));
        }
    }

    @Test
    void forwardJustBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 2; size <= 13; size++) {
            List<Piece> pieceList = randoms.blocks(size);
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
    }

    @Test
    void forwardOverBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 3; size <= 13; size++) {
            List<Piece> pieceList = randoms.blocks(size);
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
    }

    @Test
    void forwardOver2BlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 4; size <= 13; size++) {
            List<Piece> pieceList = randoms.blocks(size);
            int toDepth = pieceList.size() - 2;

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
    }

    @Test
    void reverseJustBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 13; size++) {
            List<Piece> pieceList = randoms.blocks(size);
            int fromDepth = pieceList.size();

            PiecesNumberComparator comparator = new PiecesNumberComparator();
            List<LongPieces> forward1 = OrderLookup.reverseBlocks(pieceList, fromDepth).stream()
                    .map(StackOrder::toList)
                    .map(LongPieces::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ReverseOrderLookUp lookUp = new ReverseOrderLookUp(pieceList.size(), fromDepth);
            List<LongPieces> forward2 = lookUp.parse(pieceList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .map(LongPieces::new)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void reverseOverBlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 13; size++) {
            List<Piece> pieceList = randoms.blocks(size);
            int fromDepth = pieceList.size() + 1;

            Comparator<List<Piece>> comparator = new ListComparator<>(Comparator.nullsFirst(Comparator.comparingInt(Piece::getNumber)));
            List<List<Piece>> forward1 = OrderLookup.reverseBlocks(pieceList, fromDepth).stream()
                    .map(StackOrder::toList)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ReverseOrderLookUp lookUp = new ReverseOrderLookUp(pieceList.size(), fromDepth);
            List<List<Piece>> forward2 = lookUp.parse(pieceList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }

    @Test
    void reverseOver2BlocksRandom() throws Exception {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 13; size++) {
            List<Piece> pieceList = randoms.blocks(size);
            int fromDepth = pieceList.size() + 2;

            Comparator<List<Piece>> comparator = new ListComparator<>(Comparator.nullsFirst(Comparator.comparingInt(Piece::getNumber)));
            List<List<Piece>> forward1 = OrderLookup.reverseBlocks(pieceList, fromDepth).stream()
                    .map(StackOrder::toList)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            ReverseOrderLookUp lookUp = new ReverseOrderLookUp(pieceList.size(), fromDepth);
            List<List<Piece>> forward2 = lookUp.parse(pieceList)
                    .map(blockStream -> blockStream.collect(Collectors.toList()))
                    .sorted(comparator)
                    .collect(Collectors.toList());

            assertThat(forward2).isEqualTo(forward1);
        }
    }
}
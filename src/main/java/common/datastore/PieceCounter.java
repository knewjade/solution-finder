package common.datastore;

import core.mino.Piece;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

// 1blockにつき127コまで
// 8bit中7bitをカウンターとして使わない（最上位bitは判定に使うため、個数の保持には使わない）
public class PieceCounter {
    public static final PieceCounter EMPTY = new PieceCounter(0L);

    private static final long[] SLIDE_MASK = new long[]{1L, 1L << 8, 1L << 16, 1L << 24, 1L << 32, 1L << 40, 1L << 48};

    private final long counter;

    public PieceCounter() {
        this(0L);
    }

    public PieceCounter(long counter) {
        this.counter = counter;
    }

    public PieceCounter(List<Piece> pieces) {
        this(0L, pieces);
    }

    private PieceCounter(long counter, List<Piece> pieces) {
        for (Piece piece : pieces) {
            long mask = SLIDE_MASK[piece.getNumber()];
            counter += mask;
        }
        this.counter = counter;
    }

    public PieceCounter(Stream<Piece> blocks) {
        this(0L, blocks);
    }

    private PieceCounter(long counter, Stream<Piece> blocks) {
        long sum = blocks.mapToLong(block -> SLIDE_MASK[block.getNumber()]).sum();
        this.counter = counter + sum;
    }

    public PieceCounter addAndReturnNew(List<Piece> pieces) {
        return new PieceCounter(counter, pieces);
    }

    public PieceCounter addAndReturnNew(PieceCounter pieceCounter) {
        return new PieceCounter(counter + pieceCounter.counter);
    }

    // 引く側のブロックをすべて惹かれる側に含まれていること
    // この関数を呼ぶ前にそのことを確認して置くこと
    public PieceCounter removeAndReturnNew(PieceCounter pieceCounter) {
        assert this.containsAll(pieceCounter);
        return new PieceCounter(counter - pieceCounter.counter);
    }

    public long getCounter() {
        return counter;
    }

    public List<Piece> getBlocks() {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int index = 0, max = Piece.getSize(); index < max; index++) {
            Piece piece = Piece.getBlock(index);
            long size = (counter >>> 8 * index) & 0xff;
            for (int counter = 0; counter < size; counter++)
                pieces.add(piece);
        }
        return pieces;
    }

    public Stream<Piece> getBlockStream() {
        Stream.Builder<Piece> builder = Stream.builder();
        for (int index = 0, max = Piece.getSize(); index < max; index++) {
            Piece piece = Piece.getBlock(index);
            long size = (counter >>> 8 * index) & 0xff;
            for (int counter = 0; counter < size; counter++)
                builder.accept(piece);
        }
        return builder.build();
    }

    public EnumMap<Piece, Integer> getEnumMap() {
        EnumMap<Piece, Integer> map = new EnumMap<>(Piece.class);
        for (int index = 0, max = Piece.getSize(); index < max; index++) {
            Piece piece = Piece.getBlock(index);
            long size = (counter >>> 8 * index) & 0xff;
            if (size != 0)
                map.put(piece, (int) size);
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceCounter that = (PieceCounter) o;
        return counter == that.counter;
    }

    @Override
    public int hashCode() {
        return (int) (counter ^ (counter >>> 32));
    }

    @Override
    public String toString() {
        return "PieceCounter" + getEnumMap();
    }

    public boolean containsAll(PieceCounter child) {
        long difference = this.counter - child.counter;
        // 各ブロックの最上位ビットが1のとき（繰り下がり）が発生していない時true
        return (difference & 0x80808080808080L) == 0L;
    }
}

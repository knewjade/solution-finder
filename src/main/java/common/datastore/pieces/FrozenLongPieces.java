package common.datastore.pieces;

import common.comparator.PiecesNumberComparator;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// max <= 22であること
public class FrozenLongPieces implements Pieces {
    private static final long[] SCALE = new long[22];

    static {
        for (int index = 0; index < SCALE.length; index++)
            SCALE[index] = pow(index);
    }

    private static long pow(int number) {
        return (long) Math.pow(7, number);
    }

    private final long pieces;

    FrozenLongPieces(long pieces) {
        this.pieces = pieces;
    }

    public long getPieces() {
        return pieces;
    }

    @Override
    public List<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();
        long value = pieces;
        while (value != 0L) {
            Block block = Block.getBlock((int) (value % 7));
            blocks.add(block);
            value = value / 7;
        }
        return blocks;
    }

    @Override
    public Stream<Block> getBlockStream() {
        Stream.Builder<Block> builder = Stream.builder();
        long value = pieces;
        while (value != 0L) {
            Block block = Block.getBlock((int) (value % 7));
            builder.accept(block);
            value = value / 7;
        }
        return builder.build();
    }

    @Override
    public Pieces addAndReturnNew(List<Block> blocks) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public Pieces addAndReturnNew(Block block) {
        throw new UnsupportedOperationException("this is frozen");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() != o.getClass()) {
            FrozenLongPieces that = (FrozenLongPieces) o;
            return pieces == that.pieces;
        } else if (o instanceof LongPieces) {
            LongPieces that = (LongPieces) o;
            return pieces == that.getPieces();
        } else if (o instanceof Pieces) {
            Pieces that = (Pieces) o;
            return PiecesNumberComparator.comparePieces(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (int) (pieces ^ (pieces >>> 32));
    }
}

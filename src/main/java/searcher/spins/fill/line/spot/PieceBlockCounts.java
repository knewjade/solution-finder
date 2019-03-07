package searcher.spins.fill.line.spot;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PieceBlockCounts implements Comparable<PieceBlockCounts> {
    private static final int MAX_SIZE = 4;

    private final List<PieceBlockCount> pieceBlockCountList;
    private final long key;
    private final int usingBlockCount;

    public PieceBlockCounts(List<PieceBlockCount> pieceBlockCountList) {
        assert pieceBlockCountList.size() <= MAX_SIZE : pieceBlockCountList.size();

        Collections.sort(pieceBlockCountList);
        this.pieceBlockCountList = pieceBlockCountList;
        this.key = calcKey(pieceBlockCountList);

        int usingBlockCount = 0;
        for (PieceBlockCount pieceBlockCount : pieceBlockCountList) {
            usingBlockCount += pieceBlockCount.getBlockCount();
        }
        this.usingBlockCount = usingBlockCount;
    }

    private long calcKey(List<PieceBlockCount> pieceBlockCountList) {
        long key = 0L;
        for (PieceBlockCount pieceBlockCount : pieceBlockCountList) {
            key *= 11 * 7L;
            key += pieceBlockCount.getPiece().getNumber() * 11 + pieceBlockCount.getBlockCount();
        }
        return key;
    }

    public long getKey() {
        return key;
    }

    public List<PieceBlockCount> getPieceBlockCountList() {
        return pieceBlockCountList;
    }

    public int getUsingBlockCount() {
        return usingBlockCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceBlockCounts that = (PieceBlockCounts) o;
        return key == that.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public int compareTo(PieceBlockCounts o) {
        return Long.compare(this.key, o.key);
    }
}

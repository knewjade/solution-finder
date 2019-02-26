package searcher.spins.fill.line;

import java.util.Collections;
import java.util.List;

public class PieceBlockCounts {
    private final List<PieceBlockCount> pieceBlockCountList;
    private final long key;

    PieceBlockCounts(List<PieceBlockCount> pieceBlockCountList) {
        assert pieceBlockCountList.size() <= 3 : pieceBlockCountList.size();
        Collections.sort(pieceBlockCountList);
        this.pieceBlockCountList = pieceBlockCountList;
        this.key = calcKey(pieceBlockCountList);
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
}

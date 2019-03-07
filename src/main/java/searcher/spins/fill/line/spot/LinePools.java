package searcher.spins.fill.line.spot;

import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.*;

public class LinePools {
    public static LinePools create(MinoFactory minoFactory, MinoShifter minoShifter) {
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = new EnumMap<>(Piece.class);
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = new HashMap<>();

        for (Piece piece : Piece.values()) {
            Set<PieceBlockCount> currentPieceToPieceBlockCounts = new HashSet<>();

            Set<Rotate> rotates = minoShifter.getUniqueRotates(piece);
            for (Rotate rotate : rotates) {
                Mino mino = minoFactory.create(piece, rotate);

                // 計算
                HashMap<Integer, MinXBlockCount> dyToMinXLineCount = new HashMap<>();
                int[][] positions = mino.getPositions();
                for (int[] position : positions) {
                    int dx = position[0];
                    int dy = position[1];

                    MinXBlockCount minXBlockCount = dyToMinXLineCount.computeIfAbsent(dy, (key) -> new MinXBlockCount());
                    minXBlockCount.incrementBlockCount();
                    minXBlockCount.updateMinX(dx);
                }

                // 更新: dy
                for (Map.Entry<Integer, MinXBlockCount> entry : dyToMinXLineCount.entrySet()) {
                    int dy = entry.getKey();
                    MinXBlockCount minXBlockCount = entry.getValue();
                    int blockCount = minXBlockCount.getBlockCount();
                    int minX = minXBlockCount.getMinX();

                    PieceBlockCount pieceBlockCount = new PieceBlockCount(piece, blockCount);
                    MinoDiff minoDiff = new MinoDiff(mino, minX, dy, blockCount);

                    // currentPieceToPieceBlockCounts
                    currentPieceToPieceBlockCounts.add(pieceBlockCount);

                    // currentPieceLineCountToMinos
                    List<MinoDiff> minoDiffs = pieceBlockCountToMinoDiffs.computeIfAbsent(pieceBlockCount, (key) -> new ArrayList<>());
                    minoDiffs.add(minoDiff);
                }
            }

            pieceToPieceBlockCounts.put(piece, currentPieceToPieceBlockCounts);
        }

        return new LinePools(pieceToPieceBlockCounts, pieceBlockCountToMinoDiffs);
    }

    private final Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts;
    private final Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs;

    private LinePools(
            Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts,
            Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs
    ) {
        this.pieceToPieceBlockCounts = pieceToPieceBlockCounts;
        this.pieceBlockCountToMinoDiffs = pieceBlockCountToMinoDiffs;
    }

    public Map<PieceBlockCount, List<MinoDiff>> getPieceBlockCountToMinoDiffs() {
        return pieceBlockCountToMinoDiffs;
    }

    public Map<Piece, Set<PieceBlockCount>> getPieceToPieceBlockCounts() {
        return pieceToPieceBlockCounts;
    }
}

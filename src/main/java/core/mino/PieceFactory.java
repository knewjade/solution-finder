package core.mino;

import common.ActionParser;
import common.datastore.action.Action;
import core.srs.Rotate;

import java.util.*;

public class PieceFactory {
    private static final int FIELD_WIDTH = 10;

    private final HashMap<Integer, Piece> pieces;
    private final EnumMap<Block, HashMap<Integer, List<Piece>>> underLine;

    public PieceFactory(MinoShifter minoShifter, int fieldHeight) {
        this.pieces = new HashMap<>();
        this.underLine = new EnumMap<>(Block.class);

        for (Block block : Block.values()) {
            HashMap<Integer, List<Piece>> map = new HashMap<>();
            underLine.put(block, map);

            Set<Rotate> uniqueRotates = minoShifter.getUniqueRotates(block);
            for (Rotate rotate : uniqueRotates) {
                Mino mino = new Mino(block, rotate);
                for (int y = -mino.getMinY(); y < fieldHeight - mino.getMaxY(); y++) {
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        addUniquePiece(mino, x, y, map, fieldHeight);
                    }
                }
            }
        }

        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                Rotate newRotate = minoShifter.createTransformedRotate(block, rotate);
                if (rotate != newRotate) {
                    // 既存のPieceにマッピングする
                    Mino mino = new Mino(block, rotate);
                    for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                        for (int y = fieldHeight - mino.getMaxY() - 1; -mino.getMinY() <= y; y--) {
                            addReferencePiece(minoShifter, mino, x, y);
                        }
                    }
                }
            }
        }
    }

    private void addUniquePiece(Mino mino, int x, int y, HashMap<Integer, List<Piece>> map, int fieldHeight) {
        Piece piece = new Piece(mino, x, y, fieldHeight);

        int indexKey = ActionParser.parseToInt(mino.getBlock(), mino.getRotate(), x, y);
        pieces.put(indexKey, piece);

        int maxY = y + mino.getMaxY();
        for (int yIndex = maxY + 1; yIndex <= fieldHeight; yIndex++) {
            List<Piece> underLineList = map.computeIfAbsent(yIndex, key -> new ArrayList<>());
            underLineList.add(piece);
        }
    }

    private void addReferencePiece(MinoShifter minoShifter, Mino mino, int x, int y) {
        Block block = mino.getBlock();
        Rotate rotate = mino.getRotate();
        Action action = minoShifter.createTransformedAction(block, x, y, rotate);

        int indexKey = ActionParser.parseToInt(block, action.getRotate(), action.getX(), action.getY());
        assert pieces.containsKey(indexKey);
        Piece piece = pieces.get(indexKey);

        int newIndexKey = ActionParser.parseToInt(block, rotate, x, y);
        pieces.put(newIndexKey, piece);
    }

    public Piece create(Block block, Rotate rotate, int x, int y) {
        int indexKey = ActionParser.parseToInt(block, rotate, x, y);
        assert pieces.containsKey(indexKey);
        return pieces.get(indexKey);
    }

    public List<Piece> getUnderLine(int y, Block block) {
        assert underLine.containsKey(block);
        assert underLine.get(block).containsKey(y);
        return underLine.get(block).get(y);
    }
}

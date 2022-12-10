package entry.common.kicks.factory;

import core.mino.Piece;
import core.srs.*;

import java.util.EnumMap;

public class SRSMinoRotationFactory {
    private static final SRSMinoRotationFactory DEFAULT_FACTORY = new SRSMinoRotationFactory();

    public static MinoRotation createDefault() {
        return DEFAULT_FACTORY.create();
    }

    private static EnumMap<Piece, EnumMap<Rotate, Pattern>> createRightMap() {
        EnumMap<Piece, EnumMap<Rotate, Pattern>> blockMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values()) {
                Pattern pattern = getPattern(piece, rotate, rotate.getRightRotate());
                rotateMap.put(rotate, pattern);
            }
            blockMap.put(piece, rotateMap);
        }
        return blockMap;
    }

    private static Pattern getPattern(Piece piece, Rotate current, Rotate next) {
        switch (piece) {
            case I:
                return OffsetDefine.I.getPattern(current, next);
            case O:
                return OffsetDefine.O.getPattern(current, next);
            case T:
                return OffsetDefine.T.getPattern(current, next);
            default:
                return OffsetDefine.Other.getPattern(current, next);
        }
    }

    private static EnumMap<Piece, EnumMap<Rotate, Pattern>> createLeftMap() {
        EnumMap<Piece, EnumMap<Rotate, Pattern>> blockMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values()) {
                Pattern pattern = getPattern(piece, rotate, rotate.getLeftRotate());
                rotateMap.put(rotate, pattern);
            }
            blockMap.put(piece, rotateMap);
        }
        return blockMap;
    }

    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> rightMap = createRightMap();
    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> leftMap = createLeftMap();

    public MinoRotation create() {
        return new MinoRotationNo180Impl(rightMap, leftMap);
    }
}

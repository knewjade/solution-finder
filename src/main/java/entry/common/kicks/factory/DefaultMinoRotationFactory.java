package entry.common.kicks.factory;

import core.mino.Piece;
import core.srs.*;

import java.util.EnumMap;

public class DefaultMinoRotationFactory {
    public static MinoRotation createDefault() {
        return new DefaultMinoRotationFactory().create();
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

    private static EnumMap<Piece, EnumMap<Rotate, Pattern>> createRotate180Map() {
        EnumMap<Piece, EnumMap<Rotate, Pattern>> blockMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values()) {
                Pattern pattern = getPatternRotate180(piece, rotate);
                rotateMap.put(rotate, pattern);
            }
            blockMap.put(piece, rotateMap);
        }
        return blockMap;
    }

    private static Pattern getPatternRotate180(Piece piece, Rotate current) {
        switch (piece) {
            case I: {
                switch (current) {
                    case Spawn:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {1, -1}, {0, -1}, {-1, -1}, {2, -1}, {3, -1}, {1, -2},
                        });
                    case Right:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {-1, -1}, {-1, -2}, {-1, -3}, {-1, 0}, {-1, 1}, {-2, -1},
                        });
                    case Reverse:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {-1, 1}, {0, 1}, {1, 1}, {-2, 1}, {-3, 1}, {-1, 2},
                        });
                    case Left:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {1, 1}, {1, 0}, {1, -1}, {1, 2}, {1, 3}, {2, 1}
                        });
                }
            }
            case O: {
                switch (current) {
                    case Spawn:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {1, 1},
                        });
                    case Right:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {1, -1},
                        });
                    case Reverse:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {-1, -1},
                        });
                    case Left:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {-1, 1},
                        });
                }
            }
            default: {
                switch (current) {
                    case Spawn:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {0, 0}, {1, 0}, {2, 0}, {1, -1}, {2, -1}, {-1, 0}, {-2, 0}, {-1, -1}, {-2, -1}, {0, 1}, {3, 0}, {-3, 0},
                        });
                    case Right:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {0, 0}, {0, -1}, {0, -2}, {-1, -1}, {-1, -2}, {0, 1}, {0, 2}, {-1, 1}, {-1, 2}, {1, 0}, {0, -3}, {0, 3},
                        });
                    case Reverse:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {0, 0}, {-1, 0}, {-2, 0}, {-1, 1}, {-2, 1}, {1, 0}, {2, 0}, {1, 1}, {2, 1}, {0, -1}, {-3, 0}, {3, 0},
                        });
                    case Left:
                        return Pattern.noPrivilegeSpins(new int[][]{
                                {0, 0}, {0, -1}, {0, -2}, {1, -1}, {1, -2}, {0, 1}, {0, 2}, {1, 1}, {1, 2}, {-1, 0}, {0, -3}, {0, 3},
                        });
                }
            }
        }
        throw new IllegalStateException();
    }

    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> rightMap = createRightMap();
    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> leftMap = createLeftMap();
    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> rotate180Map = createRotate180Map();

    public MinoRotation create() {
        return new MinoRotationImpl(rightMap, leftMap, rotate180Map);
    }
}

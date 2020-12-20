package core.srs;

import core.field.Field;
import core.mino.Mino;
import core.mino.Piece;

import java.util.EnumMap;

public class MinoRotationImpl implements MinoRotation {
    private static final int FIELD_WIDTH = 10;

    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> rightMap;
    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> leftMap;
    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> rotate180Map;

    public MinoRotationImpl() {
        this.rightMap = createRightMap();
        this.leftMap = createLeftMap();
        this.rotate180Map = createRotate180Map();
    }

    private EnumMap<Piece, EnumMap<Rotate, Pattern>> createRightMap() {
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

    private Pattern getPattern(Piece piece, Rotate current, Rotate next) {
        switch (piece) {
            case I:
                return OffsetDefine.I.getPattern(current, next);
            case O:
                return OffsetDefine.O.getPattern(current, next);
            default:
                return OffsetDefine.Other.getPattern(current, next);
        }
    }

    private EnumMap<Piece, EnumMap<Rotate, Pattern>> createLeftMap() {
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

    private EnumMap<Piece, EnumMap<Rotate, Pattern>> createRotate180Map() {
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

    private Pattern getPatternRotate180(Piece piece, Rotate current) {
        switch (piece) {
            case I: {
                switch (current) {
                    case Spawn:
                        return new Pattern(new int[][]{
                                {1, -1}, {0, -1}, {-1, -1}, {2, -1}, {3, -1}, {1, -2},
                        });
                    case Right:
                        return new Pattern(new int[][]{
                                {-1, -1}, {-1, -2}, {-1, -3}, {-1, 0}, {-1, 1}, {-2, -1},
                        });
                    case Reverse:
                        return new Pattern(new int[][]{
                                {-1, 1}, {0, 1}, {1, 1}, {-2, 1}, {-3, 1}, {-1, 2},
                        });
                    case Left:
                        return new Pattern(new int[][]{
                                {1, 1}, {1, 0}, {1, -1}, {1, 2}, {1, 3}, {2, 1}
                        });
                }
            }
            case O: {
                switch (current) {
                    case Spawn:
                        return new Pattern(new int[][]{
                                {1, 1},
                        });
                    case Right:
                        return new Pattern(new int[][]{
                                {1, -1},
                        });
                    case Reverse:
                        return new Pattern(new int[][]{
                                {-1, -1},
                        });
                    case Left:
                        return new Pattern(new int[][]{
                                {-1, 1},
                        });
                }
            }
            default: {
                switch (current) {
                    case Spawn:
                        return new Pattern(new int[][]{
                                {0, 0}, {1, 0}, {2, 0}, {1, -1}, {2, -1}, {-1, 0}, {-2, 0}, {-1, -1}, {-2, -1}, {0, 1}, {3, 0}, {-3, 0},
                        });
                    case Right:
                        return new Pattern(new int[][]{
                                {0, 0}, {0, -1}, {0, -2}, {-1, -1}, {-1, -2}, {0, 1}, {0, 2}, {-1, 1}, {-1, 2}, {1, 0}, {0, -3}, {0, 3},
                        });
                    case Reverse:
                        return new Pattern(new int[][]{
                                {0, 0}, {-1, 0}, {-2, 0}, {-1, 1}, {-2, 1}, {1, 0}, {2, 0}, {1, 1}, {2, 1}, {0, -1}, {-3, 0}, {3, 0},
                        });
                    case Left:
                        return new Pattern(new int[][]{
                                {0, 0}, {0, -1}, {0, -2}, {1, -1}, {1, -2}, {0, 1}, {0, 2}, {1, 1}, {1, 2}, {-1, 0}, {0, -3}, {0, 3},
                        });
                }
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public int[] getKicks(Field field, Mino before, Mino after, int x, int y, RotateDirection direction) {
        switch (direction) {
            case Right:
                return getKicksWithRightRotation(field, before, after, x, y);
            case Left:
                return getKicksWithLeftRotation(field, before, after, x, y);
        }
        throw new IllegalStateException();
    }

    @Override
    public int[] getKicksWithRightRotation(Field field, Mino before, Mino after, int x, int y) {
        Pattern pattern = rightMap.get(before.getPiece()).get(before.getRotate());
        return getKicks(field, x, y, after, pattern);
    }

    private int[] getKicks(Field field, int x, int y, Mino after, Pattern pattern) {
        int[][] offsets = pattern.getOffsets();
        int minX = -after.getMinX();
        int maxX = FIELD_WIDTH - after.getMaxX();
        int minY = -after.getMinY();
        for (int[] offset : offsets) {
            int toX = x + offset[0];
            int toY = y + offset[1];
            if (minX <= toX && toX < maxX && minY <= toY && field.canPut(after, toX, toY))
                return offset;
        }
        return null;
    }

    @Override
    public int[] getKicksWithLeftRotation(Field field, Mino before, Mino after, int x, int y) {
        Pattern pattern = leftMap.get(before.getPiece()).get(before.getRotate());
        return getKicks(field, x, y, after, pattern);
    }

    @Override
    public int[] getKicksWith180Rotation(Field field, Mino before, Mino after, int x, int y) {
        Pattern pattern = rotate180Map.get(before.getPiece()).get(before.getRotate());
        return getKicks(field, x, y, after, pattern);
    }

    @Override
    public int[][] getPatternsFrom(Mino current, RotateDirection direction) {
        switch (direction) {
            case Right:
                return getRightPatternsFrom(current);
            case Left:
                return getLeftPatternsFrom(current);
        }
        throw new IllegalStateException();
    }

    @Override
    public int[][] getRightPatternsFrom(Mino current) {
        return rightMap.get(current.getPiece()).get(current.getRotate()).getOffsets();
    }

    @Override
    public int[][] getLeftPatternsFrom(Mino current) {
        return leftMap.get(current.getPiece()).get(current.getRotate()).getOffsets();
    }

    @Override
    public int[][] getRotate180PatternsFrom(Mino current) {
        return leftMap.get(current.getPiece()).get(current.getRotate()).getOffsets();
    }
}

package core.srs;

import core.field.Field;
import core.mino.Mino;
import core.mino.Piece;

import java.util.EnumMap;

public class MinoRotationNo180Impl implements MinoRotation {
    private static final int FIELD_WIDTH = 10;

    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> rightMap;
    private final EnumMap<Piece, EnumMap<Rotate, Pattern>> leftMap;

    public MinoRotationNo180Impl(
            EnumMap<Piece, EnumMap<Rotate, Pattern>> rightMap,
            EnumMap<Piece, EnumMap<Rotate, Pattern>> leftMap
    ) {
        this.rightMap = rightMap;
        this.leftMap = leftMap;
    }

    @Override
    public int[] getKicks(Field field, Mino before, Mino after, int x, int y, RotateDirection direction) {
        switch (direction) {
            case Right:
                return getKicksWithRightRotation(field, before, after, x, y);
            case Left:
                return getKicksWithLeftRotation(field, before, after, x, y);
            case Rotate180:
                return getKicksWith180Rotation(field, before, after, x, y);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int[][] getPatternsFrom(Mino current, RotateDirection direction) {
        switch (direction) {
            case Right:
                return getRightPatternsFrom(current);
            case Left:
                return getLeftPatternsFrom(current);
            case Rotate180:
                return getRotate180PatternsFrom(current);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPrivilegeSpins(Mino before, RotateDirection direction, int testPatternIndex) {
        switch (direction) {
            case Right:
                return rightMap.get(before.getPiece()).get(before.getRotate()).isPrivilegeSpinsAt(testPatternIndex);
            case Left:
                return leftMap.get(before.getPiece()).get(before.getRotate()).isPrivilegeSpinsAt(testPatternIndex);
            case Rotate180:
                throw new UnsupportedOperationException();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean supports180() {
        return false;
    }
}

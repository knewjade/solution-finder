package core.mino;

import java.util.Arrays;
import java.util.List;

public enum Piece {
    T(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {0, 1}}, 0),
    I(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {2, 0}}, 1),
    L(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {1, 1}}, 2),
    J(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {-1, 1}}, 3),
    S(new int[][]{{0, 0}, {-1, 0}, {0, 1}, {1, 1}}, 4),
    Z(new int[][]{{0, 0}, {1, 0}, {0, 1}, {-1, 1}}, 5),
    O(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, 6),;

    private static final Piece[] PIECE_MAP = new Piece[Piece.values().length];

    static {
        for (Piece piece : Piece.values())
            PIECE_MAP[piece.getNumber()] = piece;
    }

    public static Piece getBlock(int number) {
        assert number < PIECE_MAP.length;
        return PIECE_MAP[number];
    }

    public static List<Piece> valueList() {
        return Arrays.asList(values());
    }

    public static int getSize() {
        return PIECE_MAP.length;
    }

    private final int[][] positions;
    private final int number;
    private final String name;

    Piece(int[][] positions, int number) {
        this.positions = positions;
        this.number = number;
        this.name = name();
    }

    public int[][] getPositions() {
        return positions;
    }

    public int minX() {
        int min = Integer.MAX_VALUE;
        for (int[] position : positions) {
            int value = position[0];
            min = value < min ? value : min;
        }
        return min;
    }

    public int maxX() {
        int max = Integer.MIN_VALUE;
        for (int[] position : positions) {
            int value = position[0];
            max = max < value ? value : max;
        }
        return max;
    }

    public int minY() {
        int min = Integer.MAX_VALUE;
        for (int[] position : positions) {
            int value = position[1];
            min = value < min ? value : min;
        }
        return min;
    }

    public int maxY() {
        int max = Integer.MIN_VALUE;
        for (int[] position : positions) {
            int value = position[1];
            max = max < value ? value : max;
        }
        return max;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}

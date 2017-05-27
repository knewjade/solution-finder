package core.mino;

public enum Block {
    T(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {0, 1}}, 0),
    I(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {2, 0}}, 1),
    L(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {1, 1}}, 2),
    J(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {-1, 1}}, 3),
    S(new int[][]{{0, 0}, {-1, 0}, {0, 1}, {1, 1}}, 4),
    Z(new int[][]{{0, 0}, {1, 0}, {0, 1}, {-1, 1}}, 5),
    O(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, 6),;

    private static final Block[] BLOCK_MAP = new Block[Block.values().length];

    static {
        for (Block block : Block.values())
            BLOCK_MAP[block.getNumber()] = block;
    }

    public static Block getBlock(int number) {
        assert number < BLOCK_MAP.length;
        return BLOCK_MAP[number];
    }

    public static int getSize() {
        return BLOCK_MAP.length;
    }

    private final int[][] positions;
    private final int number;
    private final String name;

    Block(int[][] positions, int number) {
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

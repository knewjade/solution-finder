package core.srs;

import java.util.EnumMap;

enum OffsetDefine {
    I(
            new Offset(new int[][]{
                    {0, 0}, {-1, 0}, {2, 0}, {-1, 0}, {2, 0},
            }),
            new Offset(new int[][]{
                    {-1, 0}, {0, 0}, {0, 0}, {0, 1}, {0, -2},
            }),
            new Offset(new int[][]{
                    {-1, 1}, {1, 1}, {-2, 1}, {1, 0}, {-2, 0},
            }),
            new Offset(new int[][]{
                    {0, 1}, {0, 1}, {0, 1}, {0, -1}, {0, 2},
            })
    ),
    O(
            new Offset(new int[][]{
                    {0, 0},
            }),
            new Offset(new int[][]{
                    {0, -1},
            }),
            new Offset(new int[][]{
                    {-1, -1},
            }),
            new Offset(new int[][]{
                    {-1, 0},
            })
    ),
    Other(
            new Offset(new int[][]{
                    {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0},
            }),
            new Offset(new int[][]{
                    {0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2},
            }),
            new Offset(new int[][]{
                    {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0},
            }),
            new Offset(new int[][]{
                    {0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2},
            })
    ),;

    private final EnumMap<Rotate, Offset> map = new EnumMap<>(Rotate.class);

    OffsetDefine(Offset spawn, Offset right, Offset reverse, Offset left) {
        map.put(Rotate.Spawn, spawn);
        map.put(Rotate.Right, right);
        map.put(Rotate.Reverse, reverse);
        map.put(Rotate.Left, left);
    }

    Pattern getPattern(Rotate current, Rotate next) {
        return map.get(current).to(map.get(next));
    }
}


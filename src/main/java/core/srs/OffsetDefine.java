package core.srs;

import java.util.EnumMap;

public enum OffsetDefine {
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
            }),
            false
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
            }),
            false
    ),
    T(
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
            }),
            true
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
            }),
            false
    ),
    ;

    private final boolean isT;
    private final EnumMap<Rotate, Offset> map = new EnumMap<>(Rotate.class);

    OffsetDefine(Offset spawn, Offset right, Offset reverse, Offset left, boolean isT) {
        this.isT = isT;
        map.put(Rotate.Spawn, spawn);
        map.put(Rotate.Right, right);
        map.put(Rotate.Reverse, reverse);
        map.put(Rotate.Left, left);
    }

    public Pattern getPattern(Rotate current, Rotate next) {
        // SRSでは、TSTフォームはRegular判定にする
        if (isT && isVertical(next)) {
            // 「接着時にTが横向き and 回転テストパターンが最後(4)のケース」を格上げする
            return map.get(current).to(map.get(next), 4);
        }
        return map.get(current).to(map.get(next));
    }

    private boolean isVertical(Rotate rotate) {
        return rotate == Rotate.Right || rotate == Rotate.Left;
    }
}


package entry.cover;

public enum CoverModes {
    Normal,
    B2BContinuous,
    TSpinMini,
    TSpinSingle,
    TSpinDouble,
    TSpinTriple,
    Tetris,
    TetrisEnd,
    OneLine,
    OneLineOrPC,
    TwoLines,
    TwoLinesOrPC,
    ThreeLines,
    ThreeLinesOrPC,
    FourLines,
    FourLinesOrPC,
    ;

    public static boolean isNLinesMode(CoverModes modes) {
        switch (modes) {
            case Normal:
            case OneLine:
            case OneLineOrPC:
            case TwoLines:
            case TwoLinesOrPC:
            case ThreeLines:
            case ThreeLinesOrPC:
            case FourLines:
            case FourLinesOrPC:
                return true;
        }
        return false;
    }
}

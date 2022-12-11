package entry;

public enum DropType {
    Harddrop(false),
    Softdrop(false),
    Softdrop180(true),
    SoftdropTOnly(false),
    SoftdropTOnly180(true),
    TSpinZero(false),
    TSpinZero180(true),
    TSpinMini(false),
    TSpinMini180(true),
    TSpinSingle(false),
    TSpinSingle180(true),
    TSpinDouble(false),
    TSpinDouble180(true),
    TSpinTriple(false),
    TSpinTriple180(true),
    ;

    private final boolean use180Rotation;

    DropType(boolean use180Rotation) {
        this.use180Rotation = use180Rotation;
    }

    public boolean uses180Rotation() {
        return use180Rotation;
    }
}

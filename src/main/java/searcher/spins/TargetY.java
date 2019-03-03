package searcher.spins;

import core.field.KeyOperators;

public class TargetY {
    private final int targetY;
    private final long keyBelowY;

    public TargetY(int targetY) {
        this.targetY = targetY;
        this.keyBelowY = KeyOperators.getMaskForKeyBelowY(targetY);
    }

    public int getTargetY() {
        return targetY;
    }

    public long getKeyBelowY() {
        return keyBelowY;
    }
}

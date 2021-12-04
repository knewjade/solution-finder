package common.cover;

public class TSpinGuard {
    public static final TSpinGuard FAILURE = new TSpinGuard(-1, -1, true);

    private final int requiredCounter;
    private final int b2bContinuousAfterStart;  // 開始直後B2Bを継続する必要がある回数
    private final boolean failed;

    public TSpinGuard(int b2bContinuousAfterStart) {
        this(0, b2bContinuousAfterStart, false);
    }

    public TSpinGuard(int requiredCounter, int b2bContinuousAfterStart, boolean failed) {
        this.requiredCounter = requiredCounter;
        this.b2bContinuousAfterStart = b2bContinuousAfterStart;
        this.failed = failed;
    }

    public boolean isSatisfied() {
        return !failed && 0 < requiredCounter && b2bContinuousAfterStart <= 0;
    }

    public TSpinGuard recordRequiredTSpin() {
        if (failed) {
            return this;
        }
        return new TSpinGuard(requiredCounter + 1, b2bContinuousAfterStart - 1, failed);
    }

    public TSpinGuard recordUnrequiredTSpin() {
        if (failed) {
            return this;
        }
        return new TSpinGuard(requiredCounter, b2bContinuousAfterStart - 1, failed);
    }

    public TSpinGuard recordNormalClearedLine(int clearedLine) {
        if (failed) {
            return this;
        }
        if (4 <= clearedLine) {
            return recordUnrequiredTSpin();
        }
        if (0 < b2bContinuousAfterStart) {
            return FAILURE;
        }
        return new TSpinGuard(requiredCounter, b2bContinuousAfterStart, failed);
    }

    public boolean isAmbiguous() {
        return !(isSatisfied() || isFailed());
    }

    public boolean isFailed() {
        return failed;
    }
}
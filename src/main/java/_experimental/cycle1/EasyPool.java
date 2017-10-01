package _experimental.cycle1;

import common.tetfu.Tetfu;
import common.tetfu.common.ColorConverter;
import concurrent.LockedReachableThreadLocal;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;

public class EasyPool {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final ColorConverter colorConverter;

    public EasyPool() {
        this.minoFactory = new MinoFactory();
        this.minoShifter = new MinoShifter();
        this.minoRotation = new MinoRotation();
        this.colorConverter = new ColorConverter();
    }

    public MinoFactory getMinoFactory() {
        return minoFactory;
    }

    public MinoShifter getMinoShifter() {
        return minoShifter;
    }

    public ColorConverter getColorConverter() {
        return colorConverter;
    }

    public LockedReachable getLockedReachable(int maxY) {
        return new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
    }

    public LockedCandidate getLockedCandidate(int maxY) {
        return new LockedCandidate(minoFactory, minoShifter, minoRotation, maxY);
    }

    public LockedReachableThreadLocal getLockedReachableThreadLocal(int maxY) {
        return new LockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, maxY);
    }

    public Tetfu getTetfu() {
        return new Tetfu(minoFactory, colorConverter);
    }

    public MinoRotation getMinoRotation() {
        return new MinoRotation();
    }
}

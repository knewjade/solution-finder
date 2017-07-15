package _experimental.unused;

import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Stopwatch;
import searcher.checker.CheckerUsingHold;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.List;

import static java.util.concurrent.TimeUnit.*;

public class CheckerInvoker {
    public static CheckerInvoker createInstance(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        PerfectValidator validator = new PerfectValidator();
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);
        return new CheckerInvoker(checker, candidate, maxClearLine, Stopwatch.createStoppedStopwatch());
    }

    private final CheckerUsingHold<Action> checker;
    private final Candidate<Action> candidate;
    private final int maxClearLine;
    private final Stopwatch stopwatch;

    private boolean lastResult = false;

    private CheckerInvoker(CheckerUsingHold<Action> checker, Candidate<Action> candidate, int maxClearLine, Stopwatch stopwatch) {
        this.checker = checker;
        this.candidate = candidate;
        this.maxClearLine = maxClearLine;
        this.stopwatch = stopwatch;
    }

    public void measure(Field field, List<Block> blocks, int maxExecuteCount) {
        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
        if (emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        if (blocks.size() < maxDepth)
            throw new IllegalArgumentException("Error: blocks size check short: " + blocks.size() + " < " + maxDepth);

        // 計測
        for (int count = 0; count < maxExecuteCount; count++) {
            stopwatch.start();
            lastResult = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatch.stop();
        }
    }

    public void clearStopwatch() {
        stopwatch.clear();
    }

    public void show() {
        System.out.println(stopwatch.toMessage(MILLISECONDS, MICROSECONDS, NANOSECONDS));
        System.out.println(String.format("Result = %s", lastResult));
    }

    public boolean getLastResult() {
        return lastResult;
    }
}

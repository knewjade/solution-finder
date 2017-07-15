package _experimental.unused;

import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Stopwatch;
import searcher.checkmate.Checkmate;
import searcher.checkmate.CheckmateNoHold;
import searcher.checkmate.CheckmateUsingHold;
import common.datastore.Result;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.*;

public class CheckmateInvoker {
    public static CheckmateInvoker createPerfectCheckmateUsingHold(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        PerfectValidator validator = new PerfectValidator();
        Checkmate<Action> checkmate = new CheckmateUsingHold<>(minoFactory, validator);
        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        return new CheckmateInvoker(checkmate, candidate, maxClearLine, stopwatch);
    }

    public static CheckmateInvoker createPerfectCheckmateNoHold(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        PerfectValidator validator = new PerfectValidator();
        Checkmate<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        return new CheckmateInvoker(checkmate, candidate, maxClearLine, stopwatch);
    }

    private final Checkmate<Action> checkmate;
    private final Candidate<Action> candidate;
    private final int maxClearLine;

    private final Stopwatch stopwatch;

    private List<Result> lastResults = new ArrayList<>();

    private CheckmateInvoker(Checkmate<Action> checkmate, Candidate<Action> candidate, int maxClearLine, Stopwatch stopwatch) {
        this.checkmate = checkmate;
        this.candidate = candidate;
        this.maxClearLine = maxClearLine;
        this.stopwatch = stopwatch;
    }

    public void measure(Field field, List<Block> blocks, int maxCount) {
        measure(field, blocks, maxCount, true);
    }

    public void measure(Field field, List<Block> blocks, int maxCount, boolean parameterCheck) {
        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
        if (parameterCheck && emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        if (parameterCheck && blocks.size() < maxDepth)
            throw new IllegalArgumentException("Error: blocks size check short: " + blocks.size());

        // 計測
        for (int count = 0; count < maxCount; count++) {
            stopwatch.start();
            lastResults = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
            stopwatch.stop();
        }
    }

    public void clearStopwatch() {
        stopwatch.clear();
    }

    public void show(boolean resultVisible) {
        System.out.println(stopwatch.toMessage(MILLISECONDS, MICROSECONDS, NANOSECONDS));
        System.out.println(String.format("Result (no uniquify) = %d", lastResults.size()));

        if (resultVisible) {
            for (Result result : lastResults)
                System.out.println(result);
        }
    }

    public List<Result> getLastResults() {
        return lastResults;
    }
}

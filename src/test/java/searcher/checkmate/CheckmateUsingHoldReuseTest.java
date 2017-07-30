package searcher.checkmate;

import common.datastore.Result;
import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Randoms;
import lib.Stopwatch;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CheckmateUsingHoldReuseTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = new MinoRotation();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckmateUsingHold<Action> checkmate = new CheckmateUsingHold<>(minoFactory, validator);
    private final CheckmateUsingHoldReuse<Action> checkmateReuse = new CheckmateUsingHoldReuse<>(minoFactory, validator);

    @Test
    void randomCheckmateWithJustBlock() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 50; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Block> blocks = randoms.blocks(maxDepth);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, blocks.size());
                Block pop = blocks.remove(index);
                blocks.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse.start();
                List<Result> result2 = checkmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchReuse.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

            assertThat(stopwatchReuse.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
        }
    }

    @Test
    void randomCheckmateWithJustBlockTwice() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 50; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Block> blocks = randoms.blocks(maxDepth);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse1 = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse2 = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, blocks.size());
                Block pop = blocks.remove(index);
                blocks.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse1.start();
                List<Result> result2 = checkmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchReuse1.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);

                stopwatchReuse2.start();
                List<Result> result3 = checkmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchReuse2.stop();

                assertThat(result3)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

            assertThat(stopwatchReuse1.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
            assertThat(stopwatchReuse2.getNanoAverageTime()).isLessThan(stopwatchReuse1.getNanoAverageTime());
        }
    }

    @Test
    void randomCheckmateOverBlock() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 50; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Block> blocks = randoms.blocks(maxDepth + 1);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, blocks.size());
                Block pop = blocks.remove(index);
                blocks.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse.start();
                List<Result> result2 = checkmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchReuse.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

            assertThat(stopwatchReuse.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
        }
    }

    @Test
    void randomCheckmateOverMoreBlock() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 50; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Block> blocks = randoms.blocks(maxDepth + 10);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, blocks.size());
                Block pop = blocks.remove(index);
                blocks.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse.start();
                List<Result> result2 = checkmateReuse.search(field, blocks, candidate, maxClearLine, maxDepth);
                stopwatchReuse.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

            assertThat(stopwatchReuse.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
        }
    }
}

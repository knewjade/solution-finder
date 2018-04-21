package searcher.checkmate;

import common.datastore.Result;
import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import lib.Randoms;
import lib.Stopwatch;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Add tests to check time
class CheckmateNoHoldReuseTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = new MinoRotation();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckmateNoHold<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
    private final CheckmateNoHoldReuse<Action> checkmateReuse = new CheckmateNoHoldReuse<>(minoFactory, validator);

    @Test
    @LongTest
    void randomCheckmateWithJustBlock() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 100; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Piece> pieces = randoms.blocks(maxDepth);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, pieces.size());
                Piece pop = pieces.remove(index);
                pieces.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse.start();
                List<Result> result2 = checkmateReuse.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchReuse.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

            // 実行時間の比較がうまくいかないためskipする（戻り値のチェックのみ）
//            assertThat(stopwatchReuse.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
        }
    }

    @Test
    void randomCheckmateWithJustBlockTwice() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 100; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Piece> pieces = randoms.blocks(maxDepth);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse1 = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse2 = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, pieces.size());
                Piece pop = pieces.remove(index);
                pieces.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse1.start();
                List<Result> result2 = checkmateReuse.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchReuse1.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);

                stopwatchReuse2.start();
                List<Result> result3 = checkmateReuse.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchReuse2.stop();

                assertThat(result3)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

            // 実行時間の比較がうまくいかないためskipする（戻り値のチェックのみ）
//            assertThat(stopwatchReuse1.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
//            assertThat(stopwatchReuse2.getNanoAverageTime()).isLessThan(stopwatchReuse1.getNanoAverageTime());
        }
    }

    @Test
    @LongTest
    void randomCheckmateOverBlock() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 100; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Piece> pieces = randoms.blocks(maxDepth + 1);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, pieces.size());
                Piece pop = pieces.remove(index);
                pieces.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse.start();
                List<Result> result2 = checkmateReuse.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchReuse.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

//            assertThat(stopwatchReuse.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
        }
    }

    @Test
    @LongTest
    void randomCheckmateOverMoreBlock() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 100; count++) {
            int maxClearLine = randoms.nextInt(3, 8);

            int maxDepth = randoms.nextIntClosed(5, 7);
            List<Piece> pieces = randoms.blocks(maxDepth + 10);

            Field field = randoms.field(maxClearLine, maxDepth);

            Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

            Stopwatch stopwatchNoUse = Stopwatch.createStoppedStopwatch();
            Stopwatch stopwatchReuse = Stopwatch.createStoppedStopwatch();

            for (int swap = 0; swap < 250; swap++) {
                int index = randoms.nextInt(3, pieces.size());
                Piece pop = pieces.remove(index);
                pieces.add(pop);

                stopwatchNoUse.start();
                List<Result> result1 = checkmate.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchNoUse.stop();

                stopwatchReuse.start();
                List<Result> result2 = checkmateReuse.search(field, pieces, candidate, maxClearLine, maxDepth);
                stopwatchReuse.stop();

                assertThat(result2)
                        .hasSameSizeAs(result1)
                        .containsAll(result1);
            }

//            assertThat(stopwatchReuse.getNanoAverageTime()).isLessThan(stopwatchNoUse.getNanoAverageTime());
        }
    }
}

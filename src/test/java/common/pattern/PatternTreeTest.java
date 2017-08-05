package common.pattern;

import _experimental.unused.CheckerInvoker;
import common.comparator.PiecesNameComparator;
import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.pieces.Pieces;
import concurrent.LockedCandidateThreadLocal;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Stopwatch;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import searcher.checker.CheckerUsingHold;
import searcher.checkmate.CheckmateUsingHold;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.mino.Block.*;
import static core.mino.Block.T;
import static org.assertj.core.api.Assertions.assertThat;

class PatternTreeTest {
    @Test
    void start() {
        PatternTree tree = new PatternTree();
        PiecesGenerator piecesGenerator = new PiecesGenerator("I,I,J,[LOSZT]p5,*p3");
        ArrayList<Pieces> piecesList = Lists.newArrayList(piecesGenerator);
        piecesList.forEach(pieces -> tree.build(pieces.getBlocks(), blocks -> new PatternTree()));

        System.out.println(piecesList.size());

        Field field = FieldFactory.createField("" +
                "__________"
//                "XXXX______" +
//                "XXXX______" +
//                "XXXX______"
        );

        MinoFactory minoFactory = new MinoFactory();
        int height = 4;
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(height);
        PerfectValidator validator = new PerfectValidator();
        CommonObj commonObj = new CommonObj(candidateThreadLocal, minoFactory, validator);
//        tree.prepare(field, height, commonObj);


        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        boolean run = tree.run(field, height, commonObj);
        stopwatch.stop();

        System.out.println(run);
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

        piecesList.sort(new PiecesNameComparator());
        for (Pieces pieces : piecesList) {
            List<Block> blocks = pieces.getBlocks();
            System.out.println(blocks + " " + tree.get(blocks));
        }
    }

    @Test
    void name() {
        List<Block> blocks = Arrays.asList(I, I, J, L, O, T, Z, S, T, S, Z);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
       PerfectValidator validator = new PerfectValidator();

        // Field
        String marks = "" +
                "__________" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 10;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
            // Execute
        CheckerUsingHold<Action> checker = new CheckerUsingHold<>(minoFactory, validator);
        boolean isSucceed = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
        System.out.println(isSucceed);
    }

    @Test
    void h() {
        System.out.println(Heuristic.c(FieldFactory.createField("" +
                "XXXX______" +
                ""), 4));

        System.out.println(Heuristic.c(FieldFactory.createField("" +
                "_XXXX_____" +
                ""), 4));

        System.out.println(Heuristic.c(FieldFactory.createField("" +
                "X_________" +
                "_XXX______" +
                ""), 4));
        System.out.println(Heuristic.c(FieldFactory.createField("" +
                "X_________" +
                "X_________" +
                "_XX_______" +
                ""), 4));
    }
}
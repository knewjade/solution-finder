package _experimental;

import common.comparator.PiecesNameComparator;
import common.datastore.action.Action;
import common.datastore.pieces.Blocks;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import concurrent.LockedCandidateThreadLocal;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.Stopwatch;
import searcher.checker.CheckerUsingHold;
import searcher.common.validator.PerfectValidator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static core.mino.Block.*;

public class Main {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        PatternTree tree = new PatternTree();
//        PiecesGenerator blocksGenerator = new BlocksGenerator("I,I,J,L,O,[SZT]p3,*p3");
        IBlocksGenerator blocksGenerator = new BlocksGenerator("I,I,J,L,O,S,Z,T,*p3");
        List<Blocks> piecesList = blocksGenerator.blocksStream().collect(Collectors.toList());
        piecesList.forEach(pieces -> tree.build(pieces.getBlocks(), blocks -> new TerminateChecker()));

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


        UsingHoldPerfectTreeVisitor visitor = new UsingHoldPerfectTreeVisitor(field, height, 10);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        boolean run = tree.run(visitor);
        stopwatch.stop();


        piecesList.sort(new PiecesNameComparator());
        for (Blocks pieces : piecesList) {
            List<Block> blocks = pieces.getBlocks();
            System.out.println(blocks + " " + tree.get(blocks));
        }

        System.out.println(run);
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        System.out.println(visitor.stopwatch.toMessage(TimeUnit.MICROSECONDS));
    }

    private static void name() {
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

    private static void h() {
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

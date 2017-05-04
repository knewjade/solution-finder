package _experimental.main;

import common.datastore.Operation;
import core.action.candidate.FixPlaceLockedCandidate;
import common.tree.AnalyzeTree;
import common.tree.VisitedTree;
import core.field.Field;
import core.field.FieldFactory;
import core.field.MiddleField;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import common.order.OrderLookup;
import common.order.Pieces;
import common.Stopwatch;
import common.iterable.AllPermutationIterable;
import common.iterable.CombinationIterable;
import searcher.checker.CheckerUsingHold;
import searcher.common.Result;
import common.datastore.action.Action;
import searcher.common.validator.BuildValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.mino.Block.*;

public class BuilderMain {
    public static void main(String[] args) {
        String marks = "" +
                "____X_____" +
                "___XXXX___" +
                "____XXXXX_" +
                "XX_XXXXXXX" +
                "XX_XXXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);

        List<Block> usingBlocks = Arrays.asList(I, T, S, Z, J, L, O);
        int combinationPopCount = 7;

        ArrayList<HashableBlocks> searchingPieces = new ArrayList<>();
        // 組み合わせの列挙
        Iterable<List<Block>> combinationIterable = new CombinationIterable<>(usingBlocks, combinationPopCount);
        for (List<Block> combination : combinationIterable) {
            // 組み合わせから、順列を列挙
            Iterable<List<Block>> permutationIterable = new AllPermutationIterable<>(combination);
            for (List<Block> permutation : permutationIterable) {
                searchingPieces.add(new HashableBlocks(permutation));
            }
        }

        int maxY = 5;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        FixPlaceLockedCandidate candidate = new FixPlaceLockedCandidate(minoFactory, minoShifter, minoRotation, maxY, field);
        BuildValidator validator = new BuildValidator(field);

        CheckerUsingHold<Action> builder = new CheckerUsingHold<>(minoFactory, validator);

        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        VisitedTree visitedTree = new VisitedTree();

        int maxDepth = usingBlocks.size();
        if (10 < maxDepth)
            throw new IllegalArgumentException("Max Depth should be <= 10");

        stopwatch.start();
        for (HashableBlocks pieces : searchingPieces) {
            List<Block> blocks = pieces.getBlocks();

            if (visitedTree.isVisited(blocks)) {
                continue;
            }

            MiddleField currentField = new MiddleField();
            boolean checkResult = builder.check(currentField, blocks, candidate, maxY, maxDepth);
            visitedTree.set(checkResult, blocks);

            if (checkResult) {
                Result result = builder.getResult();
                List<Operation> operations = result.createOperations();
                ArrayList<Block> operationBlocks = new ArrayList<>();
                for (Operation operation : operations) {
                    operationBlocks.add(operation.getBlock());
                }

                int reverseMaxDepth = result.getLastHold() != null ? operationBlocks.size() + 1 : operationBlocks.size();
                ArrayList<Pieces> reversePieces = OrderLookup.reverse(operationBlocks, reverseMaxDepth);
                for (Pieces piece : reversePieces) {
                    visitedTree.set(true, piece.getBlocks());
                }
            }
        }
        stopwatch.stop();

        AnalyzeTree tree = new AnalyzeTree();
        for (HashableBlocks piece : searchingPieces) {
            List<Block> blocks = piece.getBlocks();
            int result = visitedTree.isSucceed(blocks);
            assert result != VisitedTree.NO_RESULT;
            tree.set(result == VisitedTree.SUCCEED, blocks);
        }

        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        System.out.println(tree.show());
        System.out.println(tree.tree(2));
    }
}

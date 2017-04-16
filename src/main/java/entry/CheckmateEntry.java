package entry;

import action.candidate.LockedCandidate;
import concurrent.checker.invoker.OrderLookup;
import concurrent.checker.invoker.Pieces;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import misc.PiecesGenerator;
import misc.SafePieces;
import misc.Stopwatch;
import searcher.checkmate.CheckmateNoHold;
import searcher.checkmate2.FullCheckmateNoHold;
import searcher.common.Operation;
import searcher.common.Result;
import searcher.common.ResultHelper;
import searcher.common.action.Action;
import searcher.common.action.MinimalAction;
import searcher.common.validator.PathFullValidator;
import searcher.common.validator.PerfectValidator;
import tree.VisitedTree;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CheckmateEntry {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final List<Writer> writers;
    private final boolean isOutputToConsole;

    public CheckmateEntry(Writer writer) {
        this(Collections.singletonList(writer));
    }

    CheckmateEntry(Writer writer, boolean isOutputToConsole) {
        this(Collections.singletonList(writer), isOutputToConsole);
    }

    private CheckmateEntry(List<Writer> writers) {
        this(writers, true);
    }

    private CheckmateEntry(List<Writer> writers, boolean isOutputToConsole) {
        this.writers = writers;
        this.isOutputToConsole = isOutputToConsole;
    }

    public void invoke(Field field, List<String> patterns, int maxClearLine) throws ExecutionException, InterruptedException, IOException {
        output("# Setup Field");
        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        PiecesGenerator generator = new PiecesGenerator(patterns);

        output("Max clear lines: " + maxClearLine);
        output("Searching patterns:");
        for (String pattern : patterns)
            output("  " + pattern);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
//        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
//        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
//        ConcurrentUsingHoldCheckerInvoker invoker = new ConcurrentUsingHoldCheckerInvoker(executorService, candidateThreadLocal, checkerThreadLocal);
//
        output("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getAllBlockCount();
        if (emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        int piecesDepth = generator.getDepth();
        if (piecesDepth < maxDepth)
            throw new IllegalArgumentException("Error: blocks size check short: " + piecesDepth + " < " + maxDepth);

        output("Need Pieces = " + maxDepth);

        output();
        // ========================================
        output("# Enumerate pieces");

        // 必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        int combinationPopCount = maxDepth + 1;
        if (piecesDepth < combinationPopCount)
            combinationPopCount = piecesDepth;

        output("Piece pop count = " + combinationPopCount);

        List<List<Block>> searchingPieces = createSearchingPieces(generator, combinationPopCount, maxDepth);

        output("Searching pattern size (no hold, no dup.) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // パフェできるパターンを列挙
        // 同じ地形は統合される
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckmateNoHold<Action> checkmate = new CheckmateNoHold<>(minoFactory, minoShifter, validator);
        LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        List<Result> allResults = new ArrayList<>();
        for (List<Block> piece : searchingPieces) {
            System.out.print(piece);
            List<Result> results = checkmate.search(field, piece, candidate, maxClearLine, maxDepth);
            allResults.addAll(results);
            System.out.println(results);
        }

        System.out.println(allResults.size());

        // 同じ地形で統合されたパターンを列挙

        FullCheckmateNoHold<Action> fullCheckmate = new FullCheckmateNoHold<>(minoFactory, minoShifter);

//        VisitedTree duplicateCheckTree = new VisitedTree();
        for (Result result : allResults) {
            ArrayList<Field> expectField = new ArrayList<>();
            ArrayList<Block> expectBlock = new ArrayList<>();

            int currentMaxClearLine = maxClearLine;
            Field current = field.freeze(currentMaxClearLine);
            for (Operation operation : result.createOperations()) {
                Block block = operation.getBlock();
                Rotate rotate = operation.getRotate();
                int x = operation.getX();
                int y = operation.getY();

                expectBlock.add(block);

                Mino mino = minoFactory.create(block, rotate);
                current.putMino(mino, x, y);
                currentMaxClearLine -= current.clearLine();
                expectField.add(current);

                current = current.freeze(currentMaxClearLine);
            }
            PathFullValidator pathFullValidator = PathFullValidator.createWithoutHold(expectField, validator);
            List<Result> search = fullCheckmate.search(field, expectBlock, candidate, pathFullValidator, maxClearLine, maxDepth);
            System.out.println(result);
            for (Result search1 : search) {
                System.out.println(" => " + search1);
            }
        }

//
////        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);
////
////        // 結果を集計する
////        AnalyzeTree tree = new AnalyzeTree();
////        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
////            List<Block> pieces = resultPair.getKey();
////            Boolean result = resultPair.getValue();
////            tree.set(result, pieces);
////        }
//
        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));
//
//        output();
//        // ========================================
//        output("# Output");
////        output(tree.show());
//
//        output();
//        output("Success pattern tree [Head 3 pieces]:");
////        output(tree.tree(3));
//
//        output("-------------------");
//        output("Fail pattern (Max. 100)");
////        int counter = 0;
////        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
////            Boolean result = resultPair.getValue();
////            if (!result) {
////                output(resultPair.getKey().toString());
////                counter += 1;
////                if (100 <= counter)
////                    break;
////            }
////        }
////
////        if (counter == 0)
////            output("nothing");
//
//        output();
//        // ========================================
//        output("# Finalize");
        executorService.shutdown();
//        output("done");

        flush();
    }

    private List<List<Block>> createSearchingPieces(PiecesGenerator generator, int combinationPopCount, int maxDepth) throws IOException {
        int counter = 0;
        List<List<Block>> searchingPieces = new ArrayList<>();
        VisitedTree duplicateCheckTree = new VisitedTree();
        boolean isOverPieces = maxDepth < combinationPopCount;

        // 組み合わせの列挙
        for (SafePieces pieces : generator) {
            counter++;
            List<Block> blocks = pieces.getBlocks();

            // ホールドありパターンから複数のホールドなしに分解
            List<Pieces> forward = OrderLookup.forward(blocks, combinationPopCount);

            for (Pieces piecesWithNoHold : forward) {
                List<Block> blocksWithNoHold = piecesWithNoHold.getBlocks();
                if (isOverPieces)
                    blocksWithNoHold = blocksWithNoHold.subList(0, maxDepth);

                // 重複するホールドなしパターンを除去
                if (!duplicateCheckTree.isVisited(blocksWithNoHold)) {
                    searchingPieces.add(blocksWithNoHold);
                    duplicateCheckTree.success(blocksWithNoHold);
                }
            }
        }

        output("Searching pattern size (duplicate) = " + counter);

        return searchingPieces;
    }

    private void output() throws IOException {
        output("");
    }

    private void output(String str) throws IOException {
        for (Writer writer : writers)
            writer.append(str).append(LINE_SEPARATOR);

        if (isOutputToConsole)
            System.out.println(str);
    }

    private void flush() throws IOException {
        for (Writer writer : writers)
            writer.flush();
    }
}

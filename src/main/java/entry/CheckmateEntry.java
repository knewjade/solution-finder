package entry;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.invoker.OrderLookup;
import concurrent.checker.invoker.Pair;
import concurrent.checker.invoker.Pieces;
import concurrent.checkmate.CheckmateNoHoldThreadLocal;
import concurrent.checkmate.invoker.no_hold.ConcurrentCheckmateCommonInvoker;
import concurrent.full_checkmate.FullCheckmateNoHoldThreadLocal;
import concurrent.full_checkmate.invoker.no_hold.ConcurrentFullCheckmateNoHoldInvoker;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import misc.PiecesGenerator;
import misc.SafePieces;
import misc.Stopwatch;
import searcher.common.Operation;
import searcher.common.Operations;
import searcher.common.Result;
import searcher.common.action.Action;
import searcher.common.validator.FullValidator;
import searcher.common.validator.PathFullValidator;
import searcher.common.validator.PerfectValidator;
import tree.VisitedTree;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;

public class CheckmateEntry {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final List<Writer> writers;
    private final File allFile;
    private final File uniqueFile;
    private final Settings settings;
    private final boolean isOutputToConsole;

    public CheckmateEntry(Writer writer, File allFile, File uniqueFile, Settings settings) {
        this(Collections.singletonList(writer), allFile, uniqueFile, settings, true);
    }

    private CheckmateEntry(List<Writer> writers, File allFile, File uniqueFile, Settings settings, boolean isOutputToConsole) {
        this.writers = writers;
        this.allFile = allFile;
        this.uniqueFile = uniqueFile;
        this.settings = settings;
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
        CheckmateNoHoldThreadLocal<Action> checkmateThreadLocal = new CheckmateNoHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckmateCommonInvoker invoker = new ConcurrentCheckmateCommonInvoker(executorService, candidateThreadLocal, checkmateThreadLocal, core * 10);

        FullCheckmateNoHoldThreadLocal<Action> fullCheckmateThreadLocal = new FullCheckmateNoHoldThreadLocal<>();
        ConcurrentFullCheckmateNoHoldInvoker invokerFull = new ConcurrentFullCheckmateNoHoldInvoker(executorService, candidateThreadLocal, fullCheckmateThreadLocal);

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

        // Holdができるときは必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        int combinationPopCount = settings.isUsingHold() ? maxDepth + 1 : maxDepth;
        if (piecesDepth < combinationPopCount)
            combinationPopCount = piecesDepth;

        output("Piece pop count = " + combinationPopCount);

        // ホールドありのパターンから複数のホールドなしパターンに分解する
        List<List<Block>> searchingPieces;
        if (settings.isUsingHold()) {
            searchingPieces = createSearchingPiecesUsingHold(generator, combinationPopCount, maxDepth);
        } else {
            searchingPieces = createSearchingPiecesNoHold(generator, maxDepth);
        }

        output("Searching pattern size (no hold, no dup.) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // パフェできるパターンをホールドなしで列挙
        // 同じ地形は統合される
        List<Pair<List<Block>, List<Result>>> allMergedPatterns = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // パフェできるパターンから派生するパターンをすべて列挙
        // 同じ地形のパターンを探索
        PerfectValidator perfectValidator = new PerfectValidator();
        MinoFactory minoFactory = new MinoFactory();
        List<Pair<List<Operation>, FullValidator>> sear = new ArrayList<>();

        // マージされた探索結果から派生するパスを探索する準備
        for (Pair<List<Block>, List<Result>> mergedPatternPair : allMergedPatterns) {
            List<Result> results = mergedPatternPair.getValue();
            for (Result result : results) {
                ArrayList<Field> expectField = createFieldListFromResult(field, maxClearLine, minoFactory, result);

                // 統合される瞬間までを探索するValidator
                PathFullValidator pathFullValidator = PathFullValidator.createWithoutHold(expectField, perfectValidator);

                sear.add(new Pair<>(result.createOperations(), pathFullValidator));
            }
        }

        // 統合される瞬間までのパスを探索する
        List<Pair<List<Operation>, List<Result>>> allPathPatterns = invokerFull.search(field, sear, maxClearLine, maxDepth);

        // Operationに変換し、重複を取り除く
        TreeSet<Operations> allOperations = new TreeSet<>();
        for (Pair<List<Operation>, List<Result>> allPathPatternPair : allPathPatterns) {
            List<Operation> baseOperations = allPathPatternPair.getKey();
            List<Result> search = allPathPatternPair.getValue();

            // Resultからオペレーションに変換。オペレーションが長い順に並び替える
            List<List<Operation>> sortedOperations = search.stream()
                    .map(Result::createOperations)
                    .sorted(Comparator.comparing(List::size, reverseOrder()))
                    .collect(Collectors.toList());

            ArrayList<Operations> newOperations = new ArrayList<>();
            newOperations.add(new Operations(baseOperations));

            // すでに確定しているオペレーション順をもとに、新たに派生するオペレーションをつなげて追加する
            for (List<Operation> operation : sortedOperations) {
                // すべての確定分をもとに、派生パターンを生成
                for (int index = 0, size = newOperations.size(); index < size; index++) {
                    List<Operation> base = newOperations.get(index).getOperations();
                    ArrayList<Operation> list = new ArrayList<>(operation);
                    list.addAll(base.subList(operation.size(), base.size()));
                    assert list.size() == base.size();
                    newOperations.add(new Operations(list));
                }
            }

            allOperations.addAll(newOperations);
        }

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        TreeSet<BlockField> blockFields = new TreeSet<>();
        for (Operations allOperation : allOperations) {
            List<Operation> operations = allOperation.getOperations();
            BlockField blockField = new BlockField(allOperation, maxClearLine);

            Field freeze = field.freeze(maxClearLine);
            long deleteKey = 0;
            for (Operation operation : operations) {
                Rotate rotate = operation.getRotate();
                Block block = operation.getBlock();
                int x = operation.getX();
                int y = operation.getY();
                Mino mino = minoFactory.create(block, rotate);

                Field vanila = FieldFactory.createField(maxClearLine);
                vanila.putMino(mino, x, y);
                vanila.insertWhiteLineWithKey(deleteKey);
                blockField.add(vanila, block);

                long d1 = freeze.clearLineReturnKey();
                freeze.putMino(mino, x, y);
                freeze.insertBlackLineWithKey(d1);
                deleteKey = freeze.clearLineReturnKey();
                freeze.insertBlackLineWithKey(deleteKey);
            }

            blockFields.add(blockField);
        }

        List<BlockField> uniqueBlockField = blockFields.stream().sorted(Comparator.comparing(o -> o.operations)).collect(Collectors.toList());

        output();
        output("Found path [all] = " + allOperations.size());
        output("Found path [unique] = " + uniqueBlockField.size());

        output();
        // ========================================
        output("# Output file");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(allFile))) {
            for (Operations allOperation : allOperations) {
                String operationLine = allOperation.getOperations().stream()
                        .map(o -> String.format("%s,%s,%d,%d", o.getBlock().getName(), toRotate(o.getRotate()), o.getX(), o.getY()))
                        .collect(Collectors.joining(","));
                writer.write(operationLine);
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(uniqueFile))) {
            for (BlockField blockField : uniqueBlockField) {
                Operations allOperation = blockField.operations;
                String operationLine = allOperation.getOperations().stream()
                        .map(o -> String.format("%s,%s,%d,%d", o.getBlock().getName(), toRotate(o.getRotate()), o.getX(), o.getY()))
                        .collect(Collectors.joining(","));
                writer.write(operationLine);
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
        output("done");

        output();
        // ========================================
        output("# Finalize");
        executorService.shutdown();
        output("done");

        flush();
    }

    private String toRotate(Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return "0";
            case Left:
                return "L";
            case Reverse:
                return "2";
            case Right:
                return "R";
        }
        throw new IllegalStateException("No reachable");
    }

    private ArrayList<Field> createFieldListFromResult(Field field, int maxClearLine, MinoFactory minoFactory, Result result) {
        ArrayList<Field> expectField = new ArrayList<>();

        int currentMaxClearLine = maxClearLine;
        Field current = field.freeze(currentMaxClearLine);
        for (Operation operation : result.createOperations()) {
            Block block = operation.getBlock();
            Rotate rotate = operation.getRotate();
            int x = operation.getX();
            int y = operation.getY();

            Mino mino = minoFactory.create(block, rotate);
            current.putMino(mino, x, y);
            currentMaxClearLine -= current.clearLine();
            expectField.add(current);

            current = current.freeze(currentMaxClearLine);
        }
        return expectField;
    }

    private List<List<Block>> createSearchingPiecesUsingHold(PiecesGenerator generator, int combinationPopCount, int maxDepth) throws IOException {
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

    private List<List<Block>> createSearchingPiecesNoHold(PiecesGenerator generator, int maxDepth) throws IOException {
        int counter = 0;
        List<List<Block>> searchingPieces = new ArrayList<>();
        VisitedTree duplicateCheckTree = new VisitedTree();
        boolean isOverPieces = maxDepth < generator.getDepth();

        // 組み合わせの列挙
        for (SafePieces pieces : generator) {
            counter++;
            List<Block> blocks = pieces.getBlocks();

            if (isOverPieces)
                blocks = blocks.subList(0, maxDepth);

            // 重複するホールドなしパターンを除去
            if (!duplicateCheckTree.isVisited(blocks)) {
                searchingPieces.add(blocks);
                duplicateCheckTree.success(blocks);
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

    private static class BlockField implements Comparable<BlockField> {
        private final Operations operations;
        private final int maxClearLine;
        private final EnumMap<Block, Field> map = new EnumMap<>(Block.class);

        public BlockField(Operations operations, int maxClearLine) {
            this.operations = operations;
            this.maxClearLine = maxClearLine;
        }

        private void add(Field field, Block block) {
            map.computeIfAbsent(block, b -> FieldFactory.createField(maxClearLine)).merge(field);
        }

        @Override
        public int compareTo(BlockField o) {
            for (Block block : Block.values()) {
                Field field = this.map.getOrDefault(block, null);
                Field oField = o.map.getOrDefault(block, null);

                if (field == null && oField == null)
                    continue;

                if (field == null && oField != null)
                    return -1;

                if (field != null && oField == null)
                    return 1;

                int size = field.getBoardCount();
                int oSize = oField.getBoardCount();
                assert size == oSize;

                for (int index = 0; index < size; index++) {
                    int compare = Long.compare(field.getBoard(index), oField.getBoard(index));
                    if (compare != 0)
                        return compare;
                }
            }
            return 0;
        }
    }
}



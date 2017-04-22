package entry.path;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.invoker.Pair;
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
import entry.EntryPoint;
import entry.searching_pieces.EnumeratePiecesCore;
import entry.searching_pieces.HoldBreakEnumeratePieces;
import entry.searching_pieces.NormalEnumeratePieces;
import misc.PiecesGenerator;
import misc.Stopwatch;
import misc.SyntaxException;
import searcher.common.Operation;
import searcher.common.Operations;
import searcher.common.Result;
import searcher.common.action.Action;
import searcher.common.validator.FullValidator;
import searcher.common.validator.PathFullValidator;
import searcher.common.validator.PerfectValidator;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;

public class PathEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CHARSET = "utf-8";

    private final PathSettings settings;
    private final BufferedWriter logWriter;

    public PathEntryPoint(PathSettings settings) throws IOException {
        this.settings = settings;

        String logFilePath = settings.getLogFilePath();
        File logFile = new File(logFilePath);
        if (logFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as log file path");
        if (logFile.exists() && !logFile.canWrite())
            throw new IllegalArgumentException("Cannot write log file");

        this.logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, false), CHARSET));
    }

    @Override
    public void run() throws Exception {
        output("# Setup Field");
        Field field = settings.getField();
        if (field == null)
            throw new IllegalArgumentException("Should specify field");

        int maxClearLine = settings.getMaxClearLine();
        if (maxClearLine < 2 || 12 < maxClearLine)
            throw new IllegalArgumentException("Field Height should be 2 <= height <= 12");

        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Searching patterns:");
        List<String> patterns = settings.getPatterns();
        if (patterns.isEmpty())
            throw new IllegalArgumentException("Should specify patterns, not allow empty");

        try {
            PiecesGenerator.verify(patterns);
        } catch (SyntaxException e) {
            throw new IllegalArgumentException("Invalid patterns", e);
        }

        for (String pattern : patterns)
            output("  " + pattern);

        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = settings.getOutputBaseFilePath();
        File outputBaseFile = new File(outputBaseFilePath);
        if (outputBaseFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output base file path");
        if (outputBaseFile.exists() && !outputBaseFile.canWrite())
            throw new IllegalArgumentException("Cannot write output base file");

        // uniqueファイル
        String canonicalPath = outputBaseFile.getCanonicalPath();
        String uniqueOutputFilePath = String.format("%s_unique%s", getRemoveExtensionFromPath(canonicalPath), getExtensionFromPath(canonicalPath));
        File outputUniqueFile = new File(uniqueOutputFilePath);
        if (outputUniqueFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output unique file path");
        if (outputBaseFile.exists() && !outputUniqueFile.canWrite())
            throw new IllegalArgumentException("Cannot write output unique file");

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

        PiecesGenerator generator = new PiecesGenerator(patterns);

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
        output("Piece pop count = " + (settings.isUsingHold() ? maxDepth + 1 : maxDepth));

        // ホールドありなら、探索パターンから複数のホールドなしパターンに分解する
        // ホールドなしなら、探索パターンをそのままリスト化する
        EnumeratePiecesCore enumeratePiecesCore = createEnumeratePiecesCore(generator, maxDepth);
        List<List<Block>> searchingPieces = enumeratePiecesCore.enumerate();

        output("Searching pattern size (duplicate) = " + enumeratePiecesCore.getCounter());
        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // 探索パターンをホールドなしで列挙
        // 同じ地形は統合される
        List<Pair<List<Block>, List<Result>>> allMergedPatterns = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // パフェできるホールドなしパターンから派生するパターンをすべて列挙
        // 途中で同じ地形になる統合されたパターンを探索
        PerfectValidator perfectValidator = new PerfectValidator();
        MinoFactory minoFactory = new MinoFactory();

        // マージされた探索結果から派生するパスを探索する準備
        List<Pair<List<Operation>, FullValidator>> pathCheckList = createPathCheckList(field, maxClearLine, allMergedPatterns, perfectValidator, minoFactory);

        // 統合される瞬間までのパスを探索する
        List<Pair<List<Operation>, List<Result>>> allDerivationPath = invokerFull.search(field, pathCheckList, maxClearLine, maxDepth);

        // Operationに変換し、重複を取り除く  // 全パス
        TreeSet<Operations> allUniqueOperations = parseToUniqueOperations(allDerivationPath);

        // Blockごとに置き分けたフィールド群に変換する  // 同一ミノ配置を取り除く
        TreeSet<BlockField> blockFields = createBlockFields(field, maxClearLine, minoFactory, allUniqueOperations);

        // 操作順に並び替える
        List<BlockField> uniqueBlockField = blockFields.stream()
                .sorted(Comparator.comparing(BlockField::getOperations))
                .collect(Collectors.toList());

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        output("Found path [all] = " + allUniqueOperations.size());
        output("Found path [unique] = " + uniqueBlockField.size());

        output();
        // ========================================
        output("# Output file");

        // 全パスの出力
        outputOperationsToCSV(outputBaseFile, allUniqueOperations);

        // 同一ミノ配置を取り除いたパスの出力
        List<Operations> uniqueBlockFieldOperations = uniqueBlockField.stream()
                .map(BlockField::getOperations)
                .collect(Collectors.toList());
        outputOperationsToCSV(outputUniqueFile, uniqueBlockFieldOperations);

        output("done");

        output();
        // ========================================
        output("# Finalize");
        executorService.shutdown();
        output("done");

        flush();
    }

    private String getRemoveExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        if (pointIndex != -1)
            return path.substring(0, pointIndex);

        return path;
    }

    private String getExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return "";

        // .があるとき
        if (pointIndex != -1)
            return path.substring(pointIndex);

        return "";
    }

    private TreeSet<BlockField> createBlockFields(Field field, int maxClearLine, MinoFactory minoFactory, TreeSet<Operations> allUniqueOperations) {
        TreeSet<BlockField> blockFields = new TreeSet<>();
        for (Operations allOperation : allUniqueOperations) {
            // 操作を取り出す
            List<Operation> operations = allOperation.getOperations();

            // 初期化
            BlockField blockField = new BlockField(allOperation, maxClearLine);
            Field freeze = field.freeze(maxClearLine);  // このフィールドには行が揃っていてもライン消去されていない状態で記録

            // 操作を再生する
            for (Operation operation : operations) {
                // 接着情報を取り出す
                Rotate rotate = operation.getRotate();
                Block block = operation.getBlock();
                int x = operation.getX();
                int y = operation.getY();
                Mino mino = minoFactory.create(block, rotate);

                // 一度、フィールドをライン消去する
                long newdeletekey = freeze.clearLineReturnKey();

                // 何もないフィールドにミノをおき、これまでに消去されたラインを空白で復元させる
                Field vanilla = FieldFactory.createField(maxClearLine);
                vanilla.putMino(mino, x, y);
                vanilla.insertWhiteLineWithKey(newdeletekey);

                // おいたミノをこれまでの結果に統合する
                blockField.merge(vanilla, block);

                // ライン消去されたフィールドにミノをおく
                freeze.putMino(mino, x, y);

                // ライン消去前の状態に戻す
                freeze.insertBlackLineWithKey(newdeletekey);
            }

            blockFields.add(blockField);
        }
        return blockFields;
    }

    private TreeSet<Operations> parseToUniqueOperations(List<Pair<List<Operation>, List<Result>>> allDerivationPath) {
        TreeSet<Operations> allOperations = new TreeSet<>();
        for (Pair<List<Operation>, List<Result>> allPathPatternPair : allDerivationPath) {
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

        return allOperations;
    }

    private List<Pair<List<Operation>, FullValidator>> createPathCheckList(Field field, int maxClearLine, List<Pair<List<Block>, List<Result>>> allMergedPatterns, PerfectValidator perfectValidator, MinoFactory minoFactory) {
        List<Pair<List<Operation>, FullValidator>> pathCheckList = new ArrayList<>();
        for (Pair<List<Block>, List<Result>> mergedPatternPair : allMergedPatterns) {
            List<Result> results = mergedPatternPair.getValue();
            for (Result result : results) {
                // resultから1手ごとの地形リストを作成
                ArrayList<Field> expectField = createExpectedFieldListFromResult(field, maxClearLine, minoFactory, result);

                // 統合される瞬間までのパスを探索するためのValidator
                PathFullValidator pathFullValidator = PathFullValidator.createWithoutHold(expectField, perfectValidator);

                pathCheckList.add(new Pair<>(result.createOperations(), pathFullValidator));
            }
        }
        return pathCheckList;
    }

    private EnumeratePiecesCore createEnumeratePiecesCore(PiecesGenerator generator, int maxDepth) throws IOException {
        if (settings.isUsingHold()) {
            return new HoldBreakEnumeratePieces(generator, maxDepth);
        } else {
            return new NormalEnumeratePieces(generator, maxDepth, false);
        }
    }

    private ArrayList<Field> createExpectedFieldListFromResult(Field field, int maxClearLine, MinoFactory minoFactory, Result result) {
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

    private void outputOperationsToCSV(File file, Collection<Operations> operations) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
            for (Operations allOperation : operations) {
                String operationLine = allOperation.getOperations().stream()
                        .map(this::parseOperationToString)
                        .collect(Collectors.joining(","));
                writer.write(operationLine);
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
    }

    private String parseOperationToString(Operation operation) {
        return String.format("%s,%s,%d,%d",
                operation.getBlock().getName(),
                parseRotateToString(operation.getRotate()),
                operation.getX(),
                operation.getY()
        );
    }

    private String parseRotateToString(Rotate rotate) {
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

    private void output() throws IOException {
        output("");
    }

    private void output(String str) throws IOException {
        logWriter.append(str).append(LINE_SEPARATOR);

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws IOException {
        logWriter.flush();
    }

    @Override
    public void close() throws Exception {
        logWriter.close();
    }
}

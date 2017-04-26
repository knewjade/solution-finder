package entry.path;

import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import core.srs.Rotate;
import entry.EntryPoint;
import entry.searching_pieces.EnumeratePiecesCore;
import misc.Stopwatch;
import misc.SyntaxException;
import misc.pattern.PiecesGenerator;
import searcher.common.Operation;
import searcher.common.Operations;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PathEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CHARSET = "utf-8";

    private final PathSettings settings;
    private final BufferedWriter logWriter;

    public PathEntryPoint(PathSettings settings) throws IOException {
        this.settings = settings;

        String logFilePath = settings.getLogFilePath();
        File logFile = new File(logFilePath);

        // 親ディレクトリがない場合は作成
        if (!logFile.getParentFile().exists()) {
            boolean mairSuccess = logFile.getParentFile().mkdir();
            if (!mairSuccess) {
                throw new IllegalStateException("Failed to make output directory");
            }
        }

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

        // 親ディレクトリがない場合は作成
        if (!outputBaseFile.getParentFile().exists()) {
            boolean mairSuccess = outputBaseFile.getParentFile().mkdir();
            if (!mairSuccess) {
                throw new IllegalStateException("Failed to make output directory");
            }
        }

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
        output("Piece pop count = " + (settings.isUsingHold() && maxDepth < generator.getDepth() ? maxDepth + 1 : maxDepth));

        // ホールドありなら、探索パターンから複数のホールドなしパターンに分解する
        // ホールドなしなら、探索パターンをそのままリスト化する
        EnumeratePiecesCore enumeratePiecesCore = PathCore.createEnumeratePiecesCore(generator, maxDepth, settings.isUsingHold());
        List<List<Block>> searchingPieces = enumeratePiecesCore.enumerate();

        output("Searching pattern size (duplicate) = " + enumeratePiecesCore.getCounter());
        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // 探索を行う
        PathCore pathCore = new PathCore(maxClearLine, executorService, core * 10);
        pathCore.run(field, searchingPieces, maxClearLine, maxDepth);
        TreeSet<Operations> allOperations = pathCore.getAllOperations();
        List<Operations> uniqueOperations = pathCore.getUniqueOperations();

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        output("Found path [all] = " + allOperations.size());
        output("Found path [unique] = " + uniqueOperations.size());

        output();
        // ========================================
        output("# Output file");

        // 全パスの出力
        outputOperationsToCSV(outputBaseFile, allOperations);

        // 同一ミノ配置を取り除いたパスの出力
        outputOperationsToCSV(outputUniqueFile, uniqueOperations);

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

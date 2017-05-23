package entry.path;

import common.OperationHelper;
import common.Stopwatch;
import common.SyntaxException;
import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import common.datastore.Operation;
import common.datastore.Operations;
import common.iterable.PermutationIterable;
import common.pattern.PiecesGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.EntryPoint;
import entry.searching_pieces.EnumeratePiecesCore;

import java.io.*;
import java.util.*;
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
        String extension = getExtension();
        String outputBaseFilePath = settings.getOutputBaseFilePath();
        String outputBaseCanonicalPath = new File(outputBaseFilePath).getCanonicalPath();
        String outputFilePath = String.format("%s%s", getRemoveExtensionFromPath(outputBaseCanonicalPath), extension);
        File outputFile = new File(outputFilePath);

        // 親ディレクトリがない場合は作成
        if (!outputFile.getParentFile().exists()) {
            boolean mairSuccess = outputFile.getParentFile().mkdir();
            if (!mairSuccess) {
                throw new IllegalStateException("Failed to make output directory");
            }
        }

        if (outputFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output base file path");
        if (outputFile.exists() && !outputFile.canWrite())
            throw new IllegalArgumentException("Cannot write output base file");

        // uniqueファイル
        String uniqueOutputFilePath = String.format("%s_unique%s", getRemoveExtensionFromPath(outputBaseCanonicalPath), extension);
        File outputUniqueFile = new File(uniqueOutputFilePath);
        if (outputUniqueFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output unique file path");
        if (outputUniqueFile.exists() && !outputUniqueFile.canWrite())
            throw new IllegalArgumentException("Cannot write output unique file");

        // minimalファイル
        String minimalOutputFilePath = String.format("%s_minimal%s", getRemoveExtensionFromPath(outputBaseCanonicalPath), extension);
        File outputMinimalFile = new File(minimalOutputFilePath);
        if (outputMinimalFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output minimal file path");
        if (outputMinimalFile.exists() && !outputMinimalFile.canWrite())
            throw new IllegalArgumentException("Cannot write output minimal file");

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
        int maxLayer = settings.getMaxLayer();
        if (1 <= maxLayer) {
            System.out.println("     ... Layer 1: all");
            pathCore.runForLayer1(field, searchingPieces, maxClearLine, maxDepth);
        }

        if (2 <= maxLayer) {
            System.out.println("     ... Layer 2: unique");
            pathCore.runForLayer2(field, maxClearLine);
        }

        if (3 <= maxLayer) {
            System.out.println("     ... Layer 3: minimal");
            pathCore.runForLayer3(field, maxClearLine, generator, settings.isUsingHold());
        }

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        output();
        // ========================================
        output("# Output file");

        // 全パスの出力
        if (1 <= maxLayer) {
            TreeSet<Operations> allOperations = pathCore.getAllOperations();
            output("Found path [all] = " + allOperations.size());
            outputOperations(field, maxClearLine, outputFile, allOperations);
        }

        // 同一ミノ配置を取り除いたパスの出力
        if (2 <= maxLayer) {
            List<Operations> uniqueOperations = pathCore.getUniqueOperations();
            output("Found path [unique] = " + uniqueOperations.size());
            outputOperations(field, maxClearLine, outputUniqueFile, uniqueOperations);
        }

        // 同一ミノ配置を取り除いたパスの出力
        if (3 <= maxLayer) {
            List<Operations> minimalOperations = pathCore.getMinimalOperations().stream()
                    .map(javafx.util.Pair::getKey)
                    .collect(Collectors.toList());
            output("Found path [minimal] = " + minimalOperations.size());
            outputOperations(field, maxClearLine, outputMinimalFile, minimalOperations);
        }

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

    private String getExtension() {
        OutputType outputType = settings.getOutputType();
        switch (outputType) {
            case CSV:
                return ".csv";
            case Link:
                return ".html";
            default:
                throw new UnsupportedOperationException("Unsupport output type: " + outputType);
        }
    }

    private void outputOperations(Field field, int maxClearLine, File file, Collection<Operations> operations) {
        OutputType outputType = settings.getOutputType();
        switch (outputType) {
            case CSV:
                outputOperationsToCSV(file, operations);
                break;
            case Link:
                outputOperationsToSimpleHTML(field, maxClearLine, file, operations);
                break;
            default:
                throw new UnsupportedOperationException("Unsupport output type: " + outputType);
        }
    }

    private void outputOperationsToCSV(File file, Collection<Operations> operations) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
            for (Operations allOperation : operations) {
                String operationLine = OperationHelper.parseToString(allOperation);
                writer.write(operationLine);
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
    }

    private void outputOperationsToSimpleHTML(Field field, int maxClearLine, File file, Collection<Operations> operations) {
        // テト譜用のフィールド作成
        ColoredField initField = ColoredFieldFactory.createField(24);
        for (int y = 0; y < maxClearLine; y++) {
            for (int x = 0; x < 10; x++) {
                if (!field.isEmpty(x, y))
                    initField.setColorType(ColorType.Gray, x, y);
            }
        }

        ColorConverter colorConverter = new ColorConverter();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
            // headerの出力
            writer.write("<!DOCTYPE html>");
            writer.newLine();
            writer.write("<html lang=ja><head><meta charset=\"UTF-8\"></head><body>");
            writer.newLine();

            // パターン数の出力
            writer.write(String.format("<div>%dパターン</div>", operations.size()));
            writer.newLine();

            // 手順の出力
            for (Operations allOperation : operations) {
                // テト譜の作成
                ArrayList<String> texts = new ArrayList<>();
                ArrayList<TetfuElement> elements = new ArrayList<>();
                ColoredField prevField = initField;
                for (Operation operation : allOperation.getOperations()) {
                    Block block = operation.getBlock();
                    Rotate rotate = operation.getRotate();
                    int x = operation.getX();
                    int y = operation.getY();
                    elements.add(new TetfuElement(prevField, colorConverter.parseToColorType(block), rotate, x, y));
                    texts.add(String.format("%s-%s %d,%d", block, rotate, x, y));
                    prevField = null;
                }

                // 組めるパターンを列挙
                // すべての入れ替えた手順で組み直してみる
                List<OperationWithKey> operationWithKeys = BuildUp.createOperationWithKeys(field, allOperation, minoFactory, maxClearLine);
                HashSet<List<Block>> set = new HashSet<>();
                PermutationIterable<OperationWithKey> permutationIterable = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
                for (List<OperationWithKey> targetCheckOperationsWithKey : permutationIterable) {
                    boolean cansBuild = BuildUp.cansBuild(field, targetCheckOperationsWithKey, maxClearLine, reachable);
                    if (cansBuild) {
                        // 手順を入れ替えても組むことができる
                        List<Block> blocks = targetCheckOperationsWithKey.stream()
                                .map(o -> o.getMino().getBlock())
                                .collect(Collectors.toList());

                        set.add(blocks);
                    }
                }

                String text = String.join(", ", texts) + set;
                Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                String encode = tetfu.encode(elements);

                writer.write(String.format("<div><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a></div>", encode, text));
                writer.newLine();
            }

            // footerの出力
            writer.write("<html lang=ja><head><meta charset=\"UTF-8\"></head><body>");
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
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

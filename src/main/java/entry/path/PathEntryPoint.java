package entry.path;

import common.Stopwatch;
import common.SyntaxException;
import common.buildup.BuildUpListUp;
import common.datastore.*;
import common.datastore.pieces.NumberPieces;
import common.pattern.PiecesGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.action.reachable.LockedReachable;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.EntryPoint;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsFactory;
import searcher.pack.task.*;
import searcher.pack.task.Result;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PathEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CHARSET = "utf-8";
    private static final int BASIC_SOLUTION_WIDTH = 3;

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
        if (maxClearLine < 2 || 10 < maxClearLine)
            throw new IllegalArgumentException("Field Height should be 2 <= height <= 10");

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
        String namePath = getRemoveExtensionFromPath(outputBaseCanonicalPath);
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        String outputFilePath = String.format("%s%s", namePath, extension);
        File outputFile = new File(outputFilePath);

        // 親ディレクトリがない場合は作成
        if (!outputFile.getParentFile().exists()) {
            boolean mkdirsSuccess = outputFile.getParentFile().mkdirs();
            if (!mkdirsSuccess) {
                throw new IllegalStateException("Failed to make output directory");
            }
        }

        if (outputFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output base file path");
        if (outputFile.exists() && !outputFile.canWrite())
            throw new IllegalArgumentException("Cannot write output base file");

        // uniqueファイル
        String uniqueOutputFilePath = String.format("%s_unique%s", namePath, extension);
        File outputUniqueFile = new File(uniqueOutputFilePath);
        if (outputUniqueFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output unique file path");
        if (outputUniqueFile.exists() && !outputUniqueFile.canWrite())
            throw new IllegalArgumentException("Cannot write output unique file");

        // minimalファイル
        String minimalOutputFilePath = String.format("%s_minimal%s", namePath, extension);
        File outputMinimalFile = new File(minimalOutputFilePath);
        if (outputMinimalFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as output minimal file path");
        if (outputMinimalFile.exists() && !outputMinimalFile.canWrite())
            throw new IllegalArgumentException("Cannot write output minimal file");

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();

        PiecesGenerator generator = new PiecesGenerator(patterns);

        output("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
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
        boolean isUsingHold = settings.isUsingHold();
        output("Piece pop count = " + (isUsingHold && maxDepth < generator.getDepth() ? maxDepth + 1 : maxDepth));

        // ミノのリストを作成する
        SizedBit sizedBit = new SizedBit(BASIC_SOLUTION_WIDTH, maxClearLine);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        SeparableMinos separableMinos = new SeparableMinos(factory.create());

        // 検索条件を決める
        SolutionFilter solutionFilter = new ForPathSolutionFilter(patterns, maxClearLine);

        output();
        // ========================================
        output("# Cache");
        output("  -> Stopwatch start");

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        // 基本パターンを読み込む
        File file = new File("cache/basic" + maxClearLine);
        BasicSolutions solutions = null;
        if (file.exists()) {
            output("     ... loading");
            solutions = BasicSolutionsFactory.readAndCreateSolutions(file, solutionFilter, separableMinos, sizedBit);
        }

        // 基本パターンを読み込めていない場合は、計算し保存する
        if (solutions == null) {
            output("     ... calculating and writing");
            solutions = BasicSolutionsFactory.createAndWriteSolutions(file, solutionFilter, separableMinos, sizedBit);
        }

        output("     ... done");

        stopwatch1.stop();
        output("  -> Stopwatch stop : " + stopwatch1.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // 探索して、列挙する準備を行う
        output("     ... packing");
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit.getWidth(), sizedBit.getHeight(), field);
        TaskResultHelper taskResultHelper = createTaskResultHelper(maxClearLine);
        PackSearcher searcher = new PackSearcher(inOutPairFields, solutions, sizedBit, solutionFilter, taskResultHelper);
        PathCore pathCore = new PathCore(patterns, searcher, maxDepth, isUsingHold);

        // 絞り込みを行う
        int maxLayer = settings.getMaxLayer();

        if (1 <= maxLayer) {
            output("     ... Layer 1: unique");
            pathCore.runUnique(field, maxClearLine);
        }

        if (2 <= maxLayer) {
            output("     ... Layer 2: minimal");
            pathCore.runMinimal();
        }

        output("     ... done");

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Output file");

        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // 同一ミノ配置を取り除いたパスの出力
        if (1 <= maxLayer) {
            List<Pair<Result, HashSet<NumberPieces>>> unique = pathCore.getUnique();
            output("Found path [unique] = " + unique.size());
            outputOperations(field, maxClearLine, outputUniqueFile, minoFactory, reachable, unique);
        }

        // 少ないパターンでカバーできるパスを出力
        if (2 <= maxLayer) {
            List<Pair<Result, HashSet<NumberPieces>>> minimal = pathCore.getMinimal();
            output("Found path [minimal] = " + minimal.size());
            outputOperations(field, maxClearLine, outputMinimalFile, minoFactory, reachable, minimal);
        }

        output("done");

        output();
        // ========================================
        output("# Finalize");
        output("done");

        flush();
    }

    private TaskResultHelper createTaskResultHelper(int height) {
        if (height == 4)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
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

    private void outputOperations(Field field, int maxClearLine, File file, MinoFactory minoFactory, Reachable reachable, List<Pair<Result, HashSet<NumberPieces>>> operations) {
        OutputType outputType = settings.getOutputType();
        switch (outputType) {
            case CSV:
                outputOperationsToCSV(file, operations);
                break;
            case Link:
                outputOperationsToSimpleHTML(field, maxClearLine, file, minoFactory, reachable, operations);
                break;
            default:
                throw new UnsupportedOperationException("Unsupport output type: " + outputType);
        }
    }

    private void outputOperationsToCSV(File file, List<Pair<Result, HashSet<NumberPieces>>> operations) {
        throw new NotImplementedException();
//        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
//            for (Operations allOperation : operations) {
//                String operationLine = OperationHelper.parseToString(allOperation);
//                writer.write(operationLine);
//                writer.newLine();
//            }
//            writer.flush();
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to output file", e);
//        }
    }

    private class OperationsObj {
        private final List<OperationWithKey> sample;
        private final Pair<Result, HashSet<NumberPieces>> pair;

        private OperationsObj(Pair<Result, HashSet<NumberPieces>> pair, Field field, BuildUpListUp buildUpListUp) {
            this.pair = pair;
            LinkedList<OperationWithKey> origin = pair.getKey().getMemento().getOperationsStream().collect(Collectors.toCollection(LinkedList::new));

            Optional<List<OperationWithKey>> first = buildUpListUp
                    .existsValidBuildPatternDirectly(field, origin)
                    .findFirst();
            this.sample = first.orElse(Collections.emptyList());
        }
    }

    private void outputOperationsToSimpleHTML(Field field, int maxClearLine, File file, MinoFactory minoFactory, Reachable reachable, List<Pair<Result, HashSet<NumberPieces>>> operations) {
        // テト譜用のフィールド作成
        ColoredField initField = ColoredFieldFactory.createField(24);
        for (int y = 0; y < maxClearLine; y++) {
            for (int x = 0; x < 10; x++) {
                if (!field.isEmpty(x, y))
                    initField.setColorType(ColorType.Gray, x, y);
            }
        }

        // ライン消去ありとなしに振り分ける
        BuildUpListUp buildUpListUp = new BuildUpListUp(reachable, maxClearLine);
        List<OperationsObj> noDeletedOperations = new ArrayList<>();
        List<OperationsObj> deletedOperations = new ArrayList<>();

        for (Pair<Result, HashSet<NumberPieces>> operation : operations) {
            boolean isNoDeleted = operation.getKey().getMemento().getRawOperationsStream()
                    .allMatch(operationWithKey -> operationWithKey.getNeedDeletedKey() == 0L);
            OperationsObj operationsObj = new OperationsObj(operation, field, buildUpListUp);
            if (isNoDeleted)
                noDeletedOperations.add(operationsObj);
            else
                deletedOperations.add(operationsObj);
        }

        Comparator<OperationsObj> comparator = (o1, o2) -> {
            List<OperationWithKey> operations1 = o1.sample;
            List<OperationWithKey> operations2 = o2.sample;

            int compareSize = Integer.compare(operations1.size(), operations2.size());
            if (compareSize != 0)
                return compareSize;

            for (int index = 0; index < operations1.size(); index++) {
                Mino mino1 = operations1.get(index).getMino();
                Mino mino2 = operations2.get(index).getMino();

                int compareBlock = mino1.getBlock().compareTo(mino2.getBlock());
                if (compareBlock != 0)
                    return compareBlock;

                int compareRotate = mino1.getRotate().compareTo(mino2.getRotate());
                if (compareRotate != 0)
                    return compareRotate;
            }

            return 0;
        };
        noDeletedOperations.sort(comparator);
        deletedOperations.sort(comparator);

        // 出力
        ColorConverter colorConverter = new ColorConverter();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
            // headerの出力
            writer.write("<!DOCTYPE html>");
            writer.newLine();
            writer.write("<html lang=ja><head><meta charset=\"UTF-8\"></head><body>");
            writer.newLine();

            // パターン数の出力
            writer.write(String.format("<div>%dパターン</div>", operations.size()));
            writer.newLine();

            // 手順の出力 (ライン消去なし)
            writer.write("<h2>ライン消去なし</h2>");
            writer.newLine();

            for (OperationsObj obj : noDeletedOperations) {
                String link = createALink(obj, field, minoFactory, colorConverter, maxClearLine);
                writer.write(String.format("<div>%s</div>", link));
                writer.newLine();
            }

            // 手順の出力 (ライン消去あり)
            writer.write("<h2>ライン消去あり</h2>");
            writer.newLine();

            for (OperationsObj obj : deletedOperations) {
                String link = createALink(obj, field, minoFactory, colorConverter, maxClearLine);
                writer.write(String.format("<div>%s</div>", link));
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

    private String createALink(OperationsObj obj, Field field, MinoFactory minoFactory, ColorConverter colorConverter, int maxClearLine) {
        List<OperationWithKey> operations = obj.sample;

        // BlockField と そのパターンを表す名前 を生成
        BlockField blockField = new BlockField(maxClearLine);
        String linkText = operations.stream()
                .peek(key -> {
                    Field test = FieldFactory.createField(maxClearLine);
                    Mino mino = key.getMino();
                    test.putMino(mino, key.getX(), key.getY());
                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                    blockField.merge(test, mino.getBlock());
                })
                .map(key -> {
                    Mino mino = key.getMino();
                    return mino.getBlock().getName() + "-" + mino.getRotate().name();
                })
                .collect(Collectors.joining(" "));
        String blocksName = operations.stream()
                .map(OperationWithKey::getMino)
                .map(Mino::getBlock)
                .map(Block::getName)
                .collect(Collectors.joining());

        TetfuElement tetfuElement = parseBlockFieldToTetfuElement(field, colorConverter, blockField, blocksName);

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String encode = tetfu.encode(Collections.singletonList(tetfuElement));

        HashSet<NumberPieces> pieces = obj.pair.getValue();
        String validOrders = pieces.stream()
                .map(NumberPieces::getBlocks)
                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining()))
                .collect(Collectors.joining(", "));

        return String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [%s]", encode, linkText, validOrders);
    }

    private String parseRotateToChar(Rotate rotate) {
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


    private static TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, BlockField blockField, String comment) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField);

        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target);
        }

        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
    }

    private static void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }
}

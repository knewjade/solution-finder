package entry.path;

import common.Stopwatch;
import common.SyntaxException;
import common.buildup.BuildUp;
import common.buildup.BuildUpStream;
import common.datastore.*;
import common.datastore.pieces.LongPieces;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import common.pattern.PiecesGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import entry.EntryPoint;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.*;
import searcher.pack.task.Result;

import java.io.*;
import java.util.*;
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
        if (maxClearLine < 2 || 10 < maxClearLine)
            throw new IllegalArgumentException("Field Height should be 2 <= height <= 10");

        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
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
        int width = maxClearLine <= 4 ? 3 : 2;
        SizedBit sizedBit = new SizedBit(width, maxClearLine);
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

        // 基本パターンを計算
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate, solutionFilter);

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
        PathLayer pathLayer = settings.getPathLayer();

        if (pathLayer.contains(PathLayer.Unique)) {
            output("     ... Layer: unique");
            pathCore.runUnique(field, sizedBit);
        }

        if (pathLayer.contains(PathLayer.Minimal)) {
            output("     ... Layer: minimal");
            pathCore.runMinimal();
        }

        output("     ... done");

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Output file");

        // 同一ミノ配置を取り除いたパスの出力
        if (pathLayer.contains(PathLayer.Unique)) {
            List<Pair<Result, HashSet<LongPieces>>> unique = pathCore.getUnique();
            output("Found path [unique] = " + unique.size());
            outputOperations(field, outputUniqueFile, minoFactory, unique, sizedBit);
        }

        // 少ないパターンでカバーできるパスを出力
        if (pathLayer.contains(PathLayer.Minimal)) {
            List<Pair<Result, HashSet<LongPieces>>> minimal = pathCore.getMinimal();
            output("Found path [minimal] = " + minimal.size());
            outputOperations(field, outputMinimalFile, minoFactory, minimal, sizedBit);
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

    private void outputOperations(Field field, File file, MinoFactory minoFactory, List<Pair<Result, HashSet<LongPieces>>> operations, SizedBit sizedBit) {
        OutputType outputType = settings.getOutputType();
        switch (outputType) {
            case CSV:
                outputOperationsToCSV(field, file, operations, sizedBit);
                break;
            case Link:
                boolean isTetfuSplit = settings.isTetfuSplit();
                outputOperationsToSimpleHTML(field, file, minoFactory, operations, sizedBit, isTetfuSplit);
                break;
            default:
                throw new UnsupportedOperationException("Unsupport output type: " + outputType);
        }
    }

    private void outputOperationsToCSV(Field field, File file, List<Pair<Result, HashSet<LongPieces>>> resultPairs, SizedBit sizedBit) {
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(sizedBit.getHeight());
        List<List<OperationWithKey>> samples = resultPairs.parallelStream()
                .map(resultPair -> {
                    Result result = resultPair.getKey();
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));

                    BuildUpStream buildUpStream = threadLocal.get();

                    return buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());
                })
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
            for (List<OperationWithKey> operationWithKeys : samples) {
                Operations operations = OperationTransform.parseToOperations(field, operationWithKeys, sizedBit.getHeight());
                String operationLine = OperationInterpreter.parseToString(operations);
                writer.write(operationLine);
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
    }

    private void outputOperationsToSimpleHTML(Field field, File file, MinoFactory minoFactory, List<Pair<Result, HashSet<LongPieces>>> resultPairs, SizedBit sizedBit, boolean isTetfuSplit) {
        int maxClearLine = sizedBit.getHeight();

        // テト譜用のフィールド作成
        ColoredField initField = ColoredFieldFactory.createField(24);
        for (int y = 0; y < maxClearLine; y++) {
            for (int x = 0; x < 10; x++) {
                if (!field.isEmpty(x, y))
                    initField.setColorType(ColorType.Gray, x, y);
            }
        }

        // ライン消去ありとなしに振り分ける // true: ライン消去あり, false: ライン消去なし
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(sizedBit.getHeight());
        Map<Boolean, List<LinkInformation>> groupByDelete = resultPairs.stream()
                .map(resultPair -> {
                    Result result = resultPair.getKey();
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));
                    BuildUpStream buildUpStream = threadLocal.get();
                    List<OperationWithKey> operationWithKeys = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());
                    return new LinkInformation(resultPair, operationWithKeys);
                })
                .collect(Collectors.groupingBy(LinkInformation::containsDeletedLine));

        // それぞれで並び替える
        Comparator<LinkInformation> comparator = (o1, o2) -> {
            List<OperationWithKey> operations1 = o1.getSample();
            List<OperationWithKey> operations2 = o2.getSample();

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
        for (List<LinkInformation> objs : groupByDelete.values())
            objs.sort(comparator);

        // 出力
        ColorConverter colorConverter = new ColorConverter();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), CHARSET))) {
            // headerの出力
            writer.write("<!DOCTYPE html>");
            writer.newLine();
            writer.write("<html lang=ja><head><meta charset=\"UTF-8\"></head><body>");
            writer.newLine();

            // パターン数の出力
            writer.write(String.format("<div>%dパターン</div>", resultPairs.size()));
            writer.newLine();

            // 手順の出力 (ライン消去なし)
            writer.write("<h2>ライン消去なし</h2>");
            writer.newLine();

            for (LinkInformation information : groupByDelete.getOrDefault(false, Collections.emptyList())) {
                String link = createALink(information, field, minoFactory, colorConverter, maxClearLine, isTetfuSplit);
                writer.write(String.format("<div>%s</div>", link));
                writer.newLine();
            }

            // 手順の出力 (ライン消去あり)
            writer.write("<h2>ライン消去あり</h2>");
            writer.newLine();

            for (LinkInformation information : groupByDelete.getOrDefault(true, Collections.emptyList())) {
                String link = createALink(information, field, minoFactory, colorConverter, maxClearLine, isTetfuSplit);
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

    private String createALink(LinkInformation information, Field field, MinoFactory minoFactory, ColorConverter colorConverter, int maxClearLine, boolean isTetfuSplit) {
        if (isTetfuSplit)
            return createALinkOrder(information, field, minoFactory, colorConverter, maxClearLine);
        return createALinkOnePage(information, field, minoFactory, colorConverter, maxClearLine);
    }

    private String createALinkOnePage(LinkInformation information, Field field, MinoFactory minoFactory, ColorConverter colorConverter, int maxClearLine) {
        List<OperationWithKey> operations = information.getSample();

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
                .map(OperationWithKey::getMino)
                .map(mino -> mino.getBlock().getName() + "-" + mino.getRotate().name())
                .collect(Collectors.joining(" "));

        String blocksName = operations.stream()
                .map(OperationWithKey::getMino)
                .map(Mino::getBlock)
                .map(Block::getName)
                .collect(Collectors.joining());

        // テト譜1ページを作成
        TetfuElement tetfuElement = parseBlockFieldToTetfuElement(field, colorConverter, blockField, blocksName);

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String encode = tetfu.encode(Collections.singletonList(tetfuElement));

        // 有効なミノ順をまとめる
        HashSet<LongPieces> pieces = information.getPiecesSet();
        String validOrders = pieces.stream()
                .map(LongPieces::getBlocks)
                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining()))
                .collect(Collectors.joining(", "));

        // 出力
        return String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [%s]", encode, linkText, validOrders);
    }

    private static TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, BlockField blockField, String comment) {
        ColoredField coloredField = createInitColoredField(initField);

        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target);
        }

        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
    }

    private static ColoredField createInitColoredField(Field initField) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField);
        return coloredField;
    }

    private static void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }

    private String createALinkOrder(LinkInformation information, Field field, MinoFactory minoFactory, ColorConverter colorConverter, int maxClearLine) {
        Operations operations = OperationTransform.parseToOperations(field, information.getSample(), maxClearLine);
        List<Operation> operationsList = operations.getOperations();

        // ブロック順に変換
        List<Block> blockList = operationsList.stream()
                .map(Operation::getBlock)
                .collect(Collectors.toList());

        // そのパターンを表す名前を生成
        String linkText = operationsList.stream()
                .map(operation -> operation.getBlock().getName() + "-" + operation.getRotate().name())
                .collect(Collectors.joining(" "));

        // テト譜を作成
        String quiz = Tetfu.encodeForQuiz(blockList);
        ArrayList<TetfuElement> tetfuElements = new ArrayList<>();

        // 最初のelement
        Operation firstKey = operationsList.get(0);
        ColorType colorType1 = colorConverter.parseToColorType(firstKey.getBlock());
        ColoredField coloredField = createInitColoredField(field);
        TetfuElement firstElement = new TetfuElement(coloredField, colorType1, firstKey.getRotate(), firstKey.getX(), firstKey.getY(), quiz);
        tetfuElements.add(firstElement);

        // 2番目以降のelement
        if (1 < operationsList.size()) {
            operationsList.subList(1, operationsList.size()).stream()
                    .map(operation -> {
                        ColorType colorType = colorConverter.parseToColorType(operation.getBlock());
                        return new TetfuElement(colorType, operation.getRotate(), operation.getX(), operation.getY(), quiz);
                    })
                    .forEach(tetfuElements::add);
        }

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String encode = tetfu.encode(tetfuElements);

        // 有効なミノ順をまとめる
        HashSet<LongPieces> pieces = information.getPiecesSet();
        String validOrders = pieces.stream()
                .map(LongPieces::getBlocks)
                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining()))
                .collect(Collectors.joining(", "));

        // 出力
        return String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [%s]", encode, linkText, validOrders);
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

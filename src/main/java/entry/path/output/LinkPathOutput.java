package entry.path.output;

import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.datastore.pieces.LongBlocks;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import searcher.pack.SizedBit;
import searcher.pack.task.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class LinkPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".html";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final StandardOpenOption[] FILE_OPEN_OPTIONS = new StandardOpenOption[]{
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
    };

    private final PathEntryPoint pathEntryPoint;
    private final PathSettings settings;
    private final MinoFactory minoFactory;

    private final File outputMinimalFile;
    private final File outputUniqueFile;

    public LinkPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, MinoFactory minoFactory) throws FinderInitializeException {
        this.pathEntryPoint = pathEntryPoint;
        this.settings = pathSettings;
        this.minoFactory = minoFactory;

        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = settings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        File outputFile = new File(outputFilePath);

        // 親ディレクトリがない場合は作成
        if (!outputFile.getParentFile().exists()) {
            boolean mkdirsSuccess = outputFile.getParentFile().mkdirs();
            if (!mkdirsSuccess) {
                throw new FinderInitializeException("Failed to make output directory: OutputBase=" + outputBaseFilePath);
            }
        }

        // uniqueファイル
        String uniqueOutputFilePath = String.format("%s_unique%s", namePath, FILE_EXTENSION);
        this.outputUniqueFile = new File(uniqueOutputFilePath);

        if (outputUniqueFile.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output unique file path: OutputBase=" + outputBaseFilePath);
        if (outputUniqueFile.exists() && !outputUniqueFile.canWrite())
            throw new FinderInitializeException("Cannot write output unique file: OutputBase=" + outputBaseFilePath);

        // minimalファイル
        String minimalOutputFilePath = String.format("%s_minimal%s", namePath, FILE_EXTENSION);
        this.outputMinimalFile = new File(minimalOutputFilePath);

        if (outputMinimalFile.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output minimal file path: OutputBase=" + outputBaseFilePath);
        if (outputMinimalFile.exists() && !outputMinimalFile.canWrite())
            throw new FinderInitializeException("Cannot write output minimal file: OutputBase=" + outputBaseFilePath);
    }

    private String getCanonicalPath(String path) throws FinderInitializeException {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    private String getRemoveExtensionFromPath(String filePath) throws FinderInitializeException {
        String canonicalPath = getCanonicalPath(filePath);

        int pointIndex = canonicalPath.lastIndexOf('.');
        int separatorIndex = canonicalPath.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return canonicalPath;

        // .があるとき
        if (pointIndex != -1)
            return canonicalPath.substring(0, pointIndex);

        return canonicalPath;
    }

    @Override
    public void output(List<PathPair> pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException {
        PathLayer pathLayer = settings.getPathLayer();
        boolean isTetfuSplit = settings.isTetfuSplit();

        // 同一ミノ配置を取り除いたパスの出力
        if (pathLayer.contains(PathLayer.Unique)) {
            outputLog("Found path [unique] = " + pathPairs.size());
            outputOperationsToSimpleHTML(field, outputUniqueFile, pathPairs, sizedBit, isTetfuSplit);
        }

        // 少ないパターンでカバーできるパスを出力
        if (pathLayer.contains(PathLayer.Minimal)) {
            Selector<PathPair, LongBlocks> selector = new Selector<>(pathPairs);
            List<PathPair> minimal = selector.select();
            outputLog("Found path [minimal] = " + minimal.size());
            outputOperationsToSimpleHTML(field, outputMinimalFile, minimal, sizedBit, isTetfuSplit);
        }
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }

    private void outputOperationsToSimpleHTML(Field field, File file, List<PathPair> resultPairs, SizedBit sizedBit, boolean isTetfuSplit) throws FinderExecuteException {
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
                    Result result = resultPair.getResult();
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

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), CHARSET, FILE_OPEN_OPTIONS)) {
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
            throw new FinderExecuteException("Failed to output file", e);
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
                    test.put(mino, key.getX(), key.getY());
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
        HashSet<LongBlocks> pieces = information.getPiecesSet();
        String validOrders = pieces.stream()
                .map(LongBlocks::getBlocks)
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
        List<? extends Operation> operationsList = operations.getOperations();

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
        HashSet<LongBlocks> pieces = information.getPiecesSet();
        String validOrders = pieces.stream()
                .map(LongBlocks::getBlocks)
                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining()))
                .collect(Collectors.joining(", "));

        // 出力
        return String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> [%s]", encode, linkText, validOrders);
    }
}

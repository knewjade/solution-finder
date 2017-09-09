package _experimental.main;

import common.buildup.BuildUpStream;
import common.datastore.*;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import concurrent.LockedReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.*;
import searcher.pack.task.Result;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SquaresMain {
    private static Field createSquare(int width, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = width; x < 10; x++)
                field.setBlock(x, y);
        return field;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        // Specify square size
        int squareHeight = 6;
        int squareWidth = 6;

        // ============================

        Field field = createSquare(squareWidth, squareHeight);

        int width = decideWidth(squareHeight);
        SizedBit sizedBit = new SizedBit(width, squareHeight);

        String squareName = String.format("%dx%d", squareWidth, squareHeight);
        String title = String.format("All squares: %s", squareName);

        File file = new File(String.format("output/allsquares%s.html", squareName));

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, squareHeight, field);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        TaskResultHelper taskResultHelper = getMinoPackingHelper(squareHeight);
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(squareHeight);
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        List<Result> results = searcher.toList();

        Map<BlockCounter, List<Result>> eachBlockCounter = results.stream()
                .filter(result -> {
                    // BlockCounterに変換
                    BlockCounter blockCounter = new BlockCounter(result.getMemento().getRawOperationsStream()
                            .map(OperationWithKey::getMino)
                            .map(Mino::getBlock));
                    return isWith7BagSystem(squareHeight, blockCounter);
                })
                .collect(Collectors.groupingBy(result -> new BlockCounter(result.getMemento().getRawOperationsStream()
                        .map(OperationWithKey::getMino)
                        .map(Mino::getBlock))));

        ColorConverter colorConverter = new ColorConverter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, squareHeight);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            // headerの出力
            writer.write("<!DOCTYPE html>");
            writer.newLine();
            writer.write(String.format("<html lang=ja><head><meta charset=\"UTF-8\"><title>%s</title></head><body>", title));
            writer.newLine();

            ArrayList<BlockCounter> keys = new ArrayList<>(eachBlockCounter.keySet());
            Comparator<BlockCounter> blockCounterComparator = (o1, o2) -> {
                List<Map.Entry<Block, Integer>> list1 = new ArrayList<>(o1.getEnumMap().entrySet());
                List<Map.Entry<Block, Integer>> list2 = new ArrayList<>(o2.getEnumMap().entrySet());

                Comparator<Map.Entry<Block, Integer>> entryComparator = (o11, o21) -> {
                    int size = -o11.getValue().compareTo(o21.getValue());
                    if (size != 0)
                        return size;
                    return o11.getKey().compareTo(o21.getKey());
                };
                list1.sort(entryComparator);
                list2.sort(entryComparator);

                for (int index = 0; index < list1.size(); index++) {
                    Map.Entry<Block, Integer> entry1 = list1.get(index);
                    Map.Entry<Block, Integer> entry2 = list2.get(index);
                    int compare = entry1.getValue().compareTo(entry2.getValue());
                    if (compare != 0)
                        return compare;
                }

                for (int index = 0; index < list1.size(); index++) {
                    Map.Entry<Block, Integer> entry1 = list1.get(index);
                    Map.Entry<Block, Integer> entry2 = list2.get(index);
                    int compare2 = entry1.getKey().compareTo(entry2.getKey());
                    if (compare2 != 0)
                        return compare2;
                }

                return 0;
            };
            keys.sort(blockCounterComparator);

            writer.write(String.format("<h1>%s</h1>", title));
            writer.newLine();

            // パターン数の出力
            int keySize = eachBlockCounter.keySet().size();
            int valuesSumSize = eachBlockCounter.values().stream().mapToInt(List::size).sum();
            writer.write(String.format("<section>%d piece combinations, total %d diagrams</section>", keySize, valuesSumSize));
            writer.newLine();

            writer.write("<section><font color='#999999'>Created by newjade (<a href='https://twitter.com/1millim' target='_blank'>twitter @1millim</a>)</font></section>");
            writer.write("<section><font color='#999999'>Based on solution-finder (<a href='https://github.com/knewjade/solution-finder target='_blank'>github</a>)</font></section>");
            writer.newLine();

            // 目次
            writer.write("<h2 id='index'>Index</h2>");
            writer.write("<nav><ul>");
            writer.newLine();

            for (BlockCounter counterKey : keys) {
                ArrayList<Map.Entry<Block, Integer>> entries = new ArrayList<>(counterKey.getEnumMap().entrySet());
                entries.sort((o1, o2) -> {
                    Integer value1 = o1.getValue();
                    Integer value2 = o2.getValue();
                    return -value1.compareTo(value2);
                });

                String keyName = entries.stream()
                        .map(entry -> {
                            String str = "";
                            String blockName = entry.getKey().getName();
                            for (int count = 0; count < entry.getValue(); count++)
                                str += blockName;
                            return str;
                        })
                        .collect(Collectors.joining());

                writer.write(String.format("<li><a href='#%s'>%s</a></li>", keyName, keyName));
                writer.newLine();
            }

            writer.write("</ul></nav><hr><hr>");
            writer.newLine();

            for (BlockCounter counterKey : keys) {
                ArrayList<Map.Entry<Block, Integer>> entries = new ArrayList<>(counterKey.getEnumMap().entrySet());
                entries.sort((o1, o2) -> {
                    Integer value1 = o1.getValue();
                    Integer value2 = o2.getValue();
                    return -value1.compareTo(value2);
                });

                String keyName = entries.stream()
                        .map(entry -> {
                            String str = "";
                            String blockName = entry.getKey().getName();
                            for (int count = 0; count < entry.getValue(); count++)
                                str += blockName;
                            return str;
                        })
                        .collect(Collectors.joining());

                // 手順の出力
                writer.write(String.format("<h2 id='%s'>%s</h2><section><ul>", keyName, keyName));
                writer.newLine();

                List<Result> values = eachBlockCounter.get(counterKey);

                for (int index = 0; index < values.size(); index++) {
                    Result result = values.get(index);
                    List<OperationWithKey> operationWithKeys = result.getMemento().getOperationsStream(width).collect(Collectors.toList());

                    // パターンを表す名前 を生成
                    // BlockField を生成
                    BlockField blockField = new BlockField(squareHeight);
                    operationWithKeys
                            .forEach(key -> {
                                Field test = FieldFactory.createField(squareHeight);
                                Mino mino = key.getMino();
                                test.put(mino, key.getX(), key.getY());
                                test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                                blockField.merge(test, mino.getBlock());
                            });

                    // テト譜の生成 (on 1page)
                    TetfuElement elementOnePage = parseBlockFieldToTetfuElement(field, colorConverter, blockField, keyName);
                    Tetfu tetfuOnePage = new Tetfu(minoFactory, colorConverter);
                    String encodeOnePage = tetfuOnePage.encode(Collections.singletonList(elementOnePage));

                    // テト譜の生成 (for operations)
                    BuildUpStream buildUpStream = new BuildUpStream(reachable, squareHeight);
                    Optional<List<OperationWithKey>> first = buildUpStream.existsValidBuildPattern(field, operationWithKeys).findFirst();

                    assert first.isPresent();

                    Operations operations = OperationTransform.parseToOperations(field, first.get(), squareHeight);
                    List<? extends Operation> operationsList = operations.getOperations();

                    ArrayList<TetfuElement> tetfuElements = new ArrayList<>();

                    // 最初のelement
                    Operation firstKey = operationsList.get(0);
                    ColorType colorType1 = colorConverter.parseToColorType(firstKey.getBlock());
                    ColoredField coloredField = createInitColoredField(field);
                    TetfuElement firstElement = new TetfuElement(coloredField, colorType1, firstKey.getRotate(), firstKey.getX(), firstKey.getY(), keyName);
                    tetfuElements.add(firstElement);

                    // 2番目以降のelement
                    if (1 < operationsList.size()) {
                        operationsList.subList(1, operationsList.size()).stream()
                                .map(operation -> {
                                    ColorType colorType = colorConverter.parseToColorType(operation.getBlock());
                                    return new TetfuElement(colorType, operation.getRotate(), operation.getX(), operation.getY(), keyName);
                                })
                                .forEach(tetfuElements::add);
                    }

                    Tetfu tetfuOperations = new Tetfu(minoFactory, colorConverter);
                    String encodeOperations = tetfuOperations.encode(tetfuElements);


                    writer.write(String.format("<li><a href='http://harddrop.com/fumen/?v115@%s' target='_blank'>pattern %d</a> (<a href='http://harddrop.com/fumen/?v115@%s' target='_blank'>detail</a>)</li>", encodeOnePage, index + 1, encodeOperations));
//                    System.out.println(encode);
                }

                writer.write("</ul></section><a href='#index'>go to top</a><hr>");
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

    private static boolean isWith7BagSystem(int squareHeight, BlockCounter blockCounter) {
        switch (squareHeight) {
            case 4:
                return isWith7BagSystemHeight4(blockCounter);
            case 6:
                return isWith7BagSystemHeight6(blockCounter);
        }
        throw new UnsupportedOperationException("No support square height: " + squareHeight);
    }

    private static boolean isWith7BagSystemHeight4(BlockCounter blockCounter) {
        // ミノの個数（最も多い・2番めに多い）を取得
        EnumMap<Block, Integer> map = blockCounter.getEnumMap();
        List<Integer> values = new ArrayList<>(map.values());
        values.add(0);  // ミノが2種類以下の場合はこの0を取得する
        values.add(0);
        values.sort(Comparator.reverseOrder());

        int first = values.get(0);
        int second = values.get(1);
        int third = values.get(2);

        return (first == 4 && second <= 2 && third <= 1) ||
                (first == 3 && second == 3 && third <= 1) ||
                (first == 3 && second <= 2) ||
                (first <= 2);
    }

    private static boolean isWith7BagSystemHeight6(BlockCounter blockCounter) {
        // ミノの個数（最も多い・5番めに多い）を取得
        EnumMap<Block, Integer> map = blockCounter.getEnumMap();
        List<Integer> values = new ArrayList<>(map.values());
        values.add(0);  // ミノが2種類以下の場合はこの0を取得する
        values.add(0);
        values.add(0);
        values.add(0);
        values.sort(Comparator.reverseOrder());

        int first = values.get(0);
        int fifth = values.get(4);

        return (first == 4 && fifth < 3) || (first <= 3 && fifth < 3);
    }

    private static TaskResultHelper getMinoPackingHelper(int squareHeight) {
        if (squareHeight == 4)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
    }

    private static int decideWidth(int height) {
        if (height <= 4)
            return 3;
        return 2;
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
}

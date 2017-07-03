package _experimental.main;

import common.datastore.BlockCounter;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Squares6x4Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Field field = FieldFactory.createField("" +
                "______XXXX" +
                "______XXXX" +
                "______XXXX" +
                "______XXXX"
        );

        File file = new File("output/6x4sample.html");

        String title = "All squares: 6x4";

        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, field);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(height);
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        List<Result> results = searcher.toList();

        Map<BlockCounter, List<Result>> eachBlockCounter = results.stream()
                .filter(result -> {
                    // BlockCounterに変換
                    BlockCounter blockCounter = new BlockCounter(result.getMemento().getRawOperationsStream()
                            .map(OperationWithKey::getMino)
                            .map(Mino::getBlock));

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
                })
                .collect(Collectors.groupingBy(result -> new BlockCounter(result.getMemento().getRawOperationsStream()
                        .map(OperationWithKey::getMino)
                        .map(Mino::getBlock))));

        ColorConverter colorConverter = new ColorConverter();
        MinoFactory minoFactory = new MinoFactory();

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
            writer.write(String.format("<section>%d block combinations, total %d diagrams</section>", keySize, valuesSumSize));
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

                // 手順の出力 (ライン消去なし)
                writer.write(String.format("<h2 id='%s'>%s</h2><section><ul>", keyName, keyName));
                writer.newLine();

                List<Result> values = eachBlockCounter.get(counterKey);

                for (int index = 0; index < values.size(); index++) {
                    Result result = values.get(index);
                    // パターンを表す名前 を生成
                    // BlockField を生成
                    BlockField blockField = new BlockField(height);
                    result.getMemento().getOperationsStream(width)
                            .forEach(key -> {
                                Field test = FieldFactory.createField(height);
                                Mino mino = key.getMino();
                                test.putMino(mino, key.getX(), key.getY());
                                test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                                blockField.merge(test, mino.getBlock());
                            });


                    // テト譜の生成
                    TetfuElement element = parseBlockFieldToTetfuElement(field, colorConverter, blockField, keyName);
                    Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                    String encode = tetfu.encode(Collections.singletonList(element));

                    writer.write(String.format("<li><a href='http://harddrop.com/fumen/?v115@%s' target='_blank'>pattern %d</a></li>", encode, index + 1));
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

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
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

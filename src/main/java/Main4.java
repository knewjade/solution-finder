import common.datastore.BlockField;
import common.datastore.OperationWithKey;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main4 {
    public static void main(String[] args) throws InterruptedException {
        HashMap<String, Block> blockMap = new HashMap<>();
        for (Block block : Block.values())
            blockMap.put(block.getName(), block);

        HashMap<String, Rotate> rotateMap = new HashMap<>();
        rotateMap.put("0", Rotate.Spawn);
        rotateMap.put("L", Rotate.Left);
        rotateMap.put("2", Rotate.Reverse);
        rotateMap.put("R", Rotate.Right);

        int maxClearLine = 4;
        Field initField = FieldFactory.createField("" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                ""
        );
        ColorConverter colorConverter = new ColorConverter();

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        MinoFactory minoFactory = new MinoFactory();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./listup"), StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(line -> Arrays.asList(line.split(";")))
                    .map(strings -> strings.stream().map(s -> {
                        String[] split = s.split(",");
                        Block block = blockMap.get(split[0]);
                        Rotate rotate = rotateMap.get(split[1]);
                        Mino mino = minoFactory.create(block, rotate);
                        Integer x = Integer.valueOf(split[2]);
                        Integer y = Integer.valueOf(split[3]);
                        Long deleteKey = Long.valueOf(split[4]);
                        Long usingKey = Long.valueOf(split[5]);
                        return new OperationWithKey(mino, x, y, deleteKey, usingKey);
                    }))
                    .map(operationWithKeyStream -> {
                        BlockField blockField = new BlockField(maxClearLine);
                        StringBuilder blocks = new StringBuilder();
                        operationWithKeyStream
                                .sequential()
                                .sorted(Comparator.comparing(o -> o.getMino().getBlock()))
                                .forEach(key -> {
                                    Field test = FieldFactory.createField(maxClearLine);
                                    Mino mino = key.getMino();
                                    test.putMino(mino, key.getX(), key.getY());
                                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                                    blockField.merge(test, mino.getBlock());
                                    blocks.append(key.getMino().getBlock());
                                });
                        return parseBlockFieldToTetfuElement(initField, colorConverter, blockField, blocks.toString());
                    })
                    .map(element -> {
                        String encode = new Tetfu(minoFactory, colorConverter).encode(Collections.singletonList(element));
                        return element.getComment() + ": http://fumen.zui.jp/?v115@" + encode;
                    })
                    .sorted()
                    .forEach(s -> singleThreadExecutor.submit(() -> System.out.println(s)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        singleThreadExecutor.shutdown();
        singleThreadExecutor.awaitTermination(1L, TimeUnit.HOURS);  // 十分に長い時間待つ
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

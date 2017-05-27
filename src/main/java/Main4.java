import common.buildup.BuildUpListUp;
import common.datastore.*;
import common.iterable.CombinationIterable;
import common.pattern.PatternElement;
import common.pattern.PiecesGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Main4 {
    public static void main(String[] args) throws InterruptedException {
        // 7種1巡で可能性のあるツモ順
        List<String> allOnHold = Arrays.asList(
                "*p7, *p4"
//                "*, *p3, *p7",
//                "*, *p7, *p3",
//                "*, *p4, *p6",
//                "*, *, *p7, *p2",
//                "*, *p5, *p5",
//                "*, *p2, *p7, *",
//                "*, *p6, *p4"
        );

        // 可能性のあるミノの個数をリストアップ
        HashSet<BlockCounter> validBlockCounters = createValidBlockCounters(allOnHold, 10);
        System.out.println(validBlockCounters.size());

        // ブロック名からBlockへのマップ
        HashMap<String, Block> blockMap = new HashMap<>();
        for (Block block : Block.values())
            blockMap.put(block.getName(), block);

        // 回転文字からRotateへのマップ
        HashMap<String, Rotate> rotateMap = new HashMap<>();
        rotateMap.put("0", Rotate.Spawn);
        rotateMap.put("L", Rotate.Left);
        rotateMap.put("2", Rotate.Reverse);
        rotateMap.put("R", Rotate.Right);

        // ツモ順の列挙
        PiecesGenerator piecesGenerator = new PiecesGenerator(allOnHold);
        HashSet<List<Block>> leastBlocks = StreamSupport.stream(piecesGenerator.spliterator(), true)
                .map(SafePieces::getBlocks)
                .collect(Collectors.toCollection(HashSet::new));
        System.out.println(leastBlocks.size());
        for (List<Block> leastBlock : leastBlocks) {
            System.out.println(leastBlock);
        }

        // 初期設定
        int maxClearLine = 4;
        Field initField = FieldFactory.createField("" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                ""
        );

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        ColorConverter colorConverter = new ColorConverter();

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./output/pack7m1c"), StandardCharsets.UTF_8))) {
            String l = reader.readLine();
            List<OperationWithKey> collect = getSimpleOperationWithKeyStream(blockMap, rotateMap, minoFactory, l).collect(Collectors.toList());
            System.out.println(collect);

            LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);
            BuildUpListUp buildUpListUp = new BuildUpListUp(reachable, maxClearLine);
            List<List<OperationWithKey>> solutions = buildUpListUp.existsValidBuildPattern(initField, collect);
            solutions.stream()
                    .map(operationWithKeys -> {
                        return operationWithKeys.stream()
                                .map(OperationWithKey::getMino)
                                .map(Mino::getBlock)
                                .map(Block::getName)
                                .collect(Collectors.joining(""));
                    })
                    .forEach(System.out::println);
            System.out.println(solutions.size());
//            System.exit(0);

//            reader.lines()
            Stream.of(l)
                    .parallel()
                    .limit(100L)
                    .map(line -> getSimpleOperationWithKeyStream(blockMap, rotateMap, minoFactory, line))
                    .map(simpleOperationWithKeyStream -> simpleOperationWithKeyStream.collect(Collectors.toCollection(LinkedList::new)))
                    .filter(simpleOperationWithKeys -> {
                        Stream<Block> blocks = simpleOperationWithKeys.stream()
                                .map(key -> key.getMino().getBlock());
                        BlockCounter counter = new BlockCounter(blocks);
                        return validBlockCounters.contains(counter);
                    })
                    .map(Collection::stream)
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
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        singleThreadExecutor.shutdown();
        singleThreadExecutor.awaitTermination(1L, TimeUnit.HOURS);  // 十分に長い時間待つ
    }

    private static Stream<SimpleOperationWithKey> getSimpleOperationWithKeyStream(HashMap<String, Block> blockMap, HashMap<String, Rotate> rotateMap, MinoFactory minoFactory, String line) {
        return Stream.of(line.split(";")).map(s -> {
            String[] split = s.split(",");
            Block block = blockMap.get(split[0]);
            Rotate rotate = rotateMap.get(split[1]);
            Mino mino = minoFactory.create(block, rotate);
            Integer x = Integer.valueOf(split[2]);
            Integer y = Integer.valueOf(split[3]);
            Long deleteKey = Long.valueOf(split[4]);
            Long usingKey = Long.valueOf(split[5]);
            return new SimpleOperationWithKey(mino, x, y, deleteKey, usingKey);
        });
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

    private static HashSet<BlockCounter> createValidBlockCounters(List<String> patterns, int min) {
        List<String> pattern2 = patterns.stream()
                .map(str -> {
                    if (str.contains("#"))
                        return str.substring(0, str.indexOf('#'));
                    return str;
                })
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());

        HashSet<BlockCounter> counters = new HashSet<>();
        for (String str : pattern2) {
            LinkedList<BlockCounter> list = new LinkedList<>();
            list.add(new BlockCounter(Collections.emptyList()));

            String[] split = str.split(",");
            for (String s : split) {
                LinkedList<BlockCounter> nexts = new LinkedList<>();
                Optional<PatternElement> elementOptinal = PatternElement.parseWithoutCheck(s);
                final LinkedList<BlockCounter> fixed = list;
                elementOptinal.ifPresent(patternElement -> {
                    List<Block> blocks = patternElement.getBlocks();

                    int popCount = patternElement.getPopCount();
                    CombinationIterable<Block> combinationIterable = new CombinationIterable<>(blocks, popCount);
                    for (List<Block> blockList : combinationIterable) {
                        for (BlockCounter prev : fixed) {
                            BlockCounter next = prev.add(blockList);
                            nexts.add(next);
                        }
                    }
                });

                if (elementOptinal.isPresent())
                    list = nexts;
            }

            counters.addAll(list);

        }
        return expandValidBlockCounters(counters, min);
    }

    private static HashSet<BlockCounter> expandValidBlockCounters(HashSet<BlockCounter> counters, int min) {
        HashSet<BlockCounter> validBlockCounters = new HashSet<>();

        for (BlockCounter counter : counters) {
            List<Block> usingBlocks = counter.getBlocks();
            for (int size = min; size <= usingBlocks.size(); size++) {
                CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
                for (List<Block> blocks : combinationIterable) {
                    BlockCounter newCounter = new BlockCounter(blocks);
                    validBlockCounters.add(newCounter);
                }
            }
        }

        return validBlockCounters;
    }
}

package _experimental.cycle1;

import common.buildup.BuildUp;
import common.comparator.PiecesNameComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.order.ForwardOrderLookUp;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.task.Result;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    private static final List<Block> ALL_BLOCKS = Block.valueList();

    static {
        ALL_BLOCKS.sort(Comparator.comparing(Block::getName));
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // フィールドの指定
        Field initField = FieldFactory.createField("" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____"
        );

        BlockCounter allBlockCounter = new BlockCounter(Arrays.asList(Block.I, Block.I, Block.T, Block.S, Block.Z, Block.O, Block.J, Block.L));

        int width = 3;
        int height = 4;
        EasyPath easyPath = new EasyPath();
        List<Result> results = easyPath.calculate(initField, width, height).stream()
                .filter(result -> {
                    MinoFieldMemento memento = result.getMemento();
                    BlockCounter blockCounter = memento.getSumBlockCounter();
                    return allBlockCounter.containsAll(blockCounter);
                })
                .collect(Collectors.toList());

        EasyTetfu easyTetfu = new EasyTetfu();
        for (Result result : results) {
            List<OperationWithKey> operationWithKeys = result.getMemento().getOperationsStream(width)
                    .collect(Collectors.toList());
            System.out.println(easyTetfu.encode(initField, operationWithKeys, height));
        }

        System.out.println(results.size());

        System.out.println("sequence," + ALL_BLOCKS.stream().map(Block::getName).collect(Collectors.joining(",")));

        int cnt = 0;
        int suc = 0;
        LockedReachable reachable = new LockedReachable(new MinoFactory(), new MinoShifter(), new MinoRotation(), height);
        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(4, 5);

        IBlocksGenerator blocksGenerator = new BlocksGenerator("I, *p4");
        List<Blocks> allBlocks = blocksGenerator.blocksStream().collect(Collectors.toList());
        allBlocks.sort(new PiecesNameComparator());

        for (Blocks blocks : allBlocks) {
            BlockCounter counter = new BlockCounter(blocks.blockStream());
            Set<Block> holdBlocks = results.stream()
                    .map(Result::getMemento)
                    .filter(memento -> {
                        BlockCounter blockCounter = memento.getSumBlockCounter();
                        return counter.containsAll(blockCounter);
                    })
                    .filter(memento -> {
                        BlockCounter perfectUsingBlockCounter = memento.getSumBlockCounter();
                        return lookUp.parse(blocks.getBlocks())
                                .map(stream -> stream.limit(4L))
                                .map(LongBlocks::new)
                                .filter(longBlocks -> {
                                    BlockCounter blockCounter = new BlockCounter(longBlocks.blockStream());
                                    return perfectUsingBlockCounter.containsAll(blockCounter);
                                })
                                .anyMatch(longBlocks -> BuildUp.existsValidByOrder(initField, memento.getOperationsStream(width), longBlocks.getBlocks(), height, reachable));
                    })
                    .map(memento -> {
                        BlockCounter blockCounter = memento.getSumBlockCounter();
                        return counter.removeAndReturnNew(blockCounter);
                    })
                    .map(blockCounter -> blockCounter.getBlocks().get(0))
                    .collect(Collectors.toSet());

            System.out.println(parseToString(blocks.getBlocks(), holdBlocks));

            cnt++;
            if (!holdBlocks.isEmpty())
                suc++;
        }

        System.out.println((double) suc / cnt);
        System.out.println(suc);

//
//        // 準備
//        MinoFactory minoFactory = new MinoFactory();
//        MinoShifter minoShifter = new MinoShifter();
//        MinoRotation minoRotation = new MinoRotation();
//        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
//        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
//        int maxDepth = 6;
//        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(maxDepth, 7);
//        ColorConverter colorConverter = new ColorConverter();
//
//
//        PiecesGenerator blocksGenerator = new BlocksGenerator("*p7");
//        List<Blocks> allBlocks = MyIterables.toList(piecesGenerator);

    }

    private static String parseToString(List<Block> blocks, Set<Block> hold) {
        String blockName = String.format("[%s]%s", blocks.get(0), blocks.subList(1, blocks.size()).stream().map(Block::getName).collect(Collectors.joining()));

        if (hold.isEmpty()) {
            // 失敗時
            return String.format("-%s,-,-,-,-,-,-,-", blockName);
        } else {
            // 成功時
            String values = ALL_BLOCKS.stream().map(block -> {
                if (hold.contains(block))
                    return "O";
                else if (!blocks.subList(1,blocks.size()).contains(block))
                    return "*";
                return "";
            }).collect(Collectors.joining(","));
            return String.format("%s,%s", blockName, values);
        }
    }
}

package _experimental.cycle2;

import _experimental.cycle1.EasyPath;
import _experimental.cycle1.EasyPool;
import _experimental.cycle1.EasyTetfu;
import common.buildup.BuildUpStream;
import common.datastore.*;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.iterable.CombinationIterable;
import common.iterable.PermutationIterable;
import common.order.ForwardOrderLookUp;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import common.tree.AnalyzeTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.LockedReachableThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.action.reachable.LockedReachable;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class IfSelector {
    private static class Data {
        private final Field field;
        private final double percent;
        private final int count;

        Data(long field, double percent) {
            this(field, percent, -1);
        }

        Data(long field, double percent, int count) {
            this.field = new SmallField(field);
            this.percent = percent;
            this.count = count;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        int width = 3;
        int height = 4;
        int maxDepth = 6;

        EasyPool easyPool = new EasyPool();
        EasyPath easyPath = new EasyPath(easyPool);
        EasyTetfu easyTetfu = new EasyTetfu(easyPool);
        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(maxDepth, 7);
        IBlocksGenerator blocksGenerator = new BlocksGenerator("*p7");
        List<Blocks> allBlocks2 = blocksGenerator.blocksStream().collect(Collectors.toList());

        BlockCounter allIncluded = new BlockCounter(Block.valueList());

        // すべての100%地形を読み込み
        List<Data> list = Files.lines(Paths.get("output/cycle2/IJSO.csv"))
                .map(line -> line.split(","))
                .map(split -> new Data(Long.valueOf(split[0]), Double.valueOf(split[1])))
                .filter(data -> 0.94 <= data.percent)
                .collect(Collectors.toList());

        System.out.println(list.size());

        for (Data data : list) {
            Field initField = data.field;
            System.out.println(FieldView.toString(initField));

            // すべての検索するBlocks
            // パフェできる手順が対象
            ConcurrentCheckerUsingHoldInvoker invoker = new ConcurrentCheckerUsingHoldInvoker(executorService, new LockedCandidateThreadLocal(height), new CheckerUsingHoldThreadLocal<>());
            List<Pair<Blocks, Boolean>> search = invoker.search(initField, allBlocks2, height, maxDepth);
            List<Blocks> targetBlocks = search.stream()
                    .filter(Pair::getValue)
                    .map(Pair::getKey)
                    .collect(Collectors.toList());
            System.out.println("target block size: " + targetBlocks.size());

            // 条件で手順を分割
            // 2分岐させる
            EnumMap<Block, EnumMap<Block, Map<Boolean, List<Blocks>>>> firstChoice = new EnumMap<>(Block.class);
            PermutationIterable<Block> blockPermutations = new PermutationIterable<>(Block.valueList(), 2);
            for (List<Block> blocks : blockPermutations) {
                assert blocks.size() == 2;
                Block firstBlock = blocks.get(0);
                Block secondBlock = blocks.get(1);

                Map<Boolean, List<Blocks>> conditional = targetBlocks.stream()
                        .collect(Collectors.groupingBy(o -> {
                            List<Block> blockList = o.getBlocks();
                            return blockList.indexOf(firstBlock) < blockList.indexOf(secondBlock);
                        }));

                EnumMap<Block, Map<Boolean, List<Blocks>>> secondChoice = firstChoice.computeIfAbsent(firstBlock, block -> new EnumMap<>(Block.class));
                secondChoice.put(secondBlock, conditional);
            }

            // すべてのパフェ手順を取得
            List<Result> results = easyPath.calculate(initField, width, height)
                    .stream()
                    .filter(result -> allIncluded.containsAll(new BlockCounter(result.getMemento().getOperationsStream(width).map(OperationWithKey::getMino).map(Mino::getBlock))))
                    .collect(Collectors.toList());
            System.out.println("result size: " + results.size());

            for (Result result : results) {
                // パフェ手順から組み立てられるミノ順を抽出
                List<OperationWithKey> operationWithKeys = result.getMemento()
                        .getOperationsStream(width)
                        .collect(Collectors.toList());
                assert operationWithKeys.size() == maxDepth;

                String tetfu = easyTetfu.encode(initField, operationWithKeys, height);
                System.out.println(tetfu);

                LockedReachable reachable = easyPool.getLockedReachable(height);
                BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
                AnalyzeTree possibleTree = getPossibleBuildingTree(initField, operationWithKeys, buildUpStream);

                for (Map.Entry<Block, EnumMap<Block, Map<Boolean, List<Blocks>>>> firstEntry : firstChoice.entrySet()) {
                    Block firstBlock = firstEntry.getKey();
                    for (Map.Entry<Block, Map<Boolean, List<Blocks>>> secondEntry : firstEntry.getValue().entrySet()) {
                        Block secondBlock = secondEntry.getKey();
                        for (Map.Entry<Boolean, List<Blocks>> splitMap : secondEntry.getValue().entrySet()) {
                            Boolean isSatisfy = splitMap.getKey();
                            List<Blocks> blocksList = splitMap.getValue();
//                            boolean isFound = checksAll(lookUp, blocksList, possibleTree);
                            boolean isFound = checksAllPercent(lookUp, blocksList, possibleTree, 0.9);
//                            System.out.printf("%s < %s == %s -> %s%n", firstBlock, secondBlock, isSatisfy, isSucceed);

                            if (isFound)
                                System.out.println("##########################");
                        }
                    }
                }
            }
        }

        executorService.shutdown();
        System.exit(0);

        // 準備
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);


        ArrayList<Result> results = new ArrayList<>();
        Field initField = FieldFactory.createField(4);

        int size = results.size();

        assert 0 < size;

        System.out.println(size);
        System.out.println(size * (size - 1) / 2);

        CombinationIterable<Result> combination = new CombinationIterable<>(results, 2);
        for (List<Result> resultList : combination) {
            assert resultList.size() == 2;

            // パフェ手順から組み立てられるミノ順を抽出
            Result result1 = resultList.get(0);
            List<OperationWithKey> operationWithKeys1 = result1.getMemento()
                    .getOperationsStream(width)
                    .collect(Collectors.toList());
            assert operationWithKeys1.size() == maxDepth;
            AnalyzeTree possibleTree1 = getPossibleBuildingTree(initField, operationWithKeys1, buildUpStream);

//            boolean noDeleteLine1 = result1.getMemento().getRawOperationsStream().allMatch(operationWithKey -> operationWithKey.getNeedDeletedKey() == 0L);

            // パフェ手順から組み立てられるミノ順を抽出
            Result result2 = resultList.get(1);
            List<OperationWithKey> operationWithKeys2 = result2.getMemento()
                    .getOperationsStream(width)
                    .collect(Collectors.toList());
            assert operationWithKeys2.size() == maxDepth;
            AnalyzeTree possibleTree2 = getPossibleBuildingTree(initField, operationWithKeys2, buildUpStream);

//            boolean noDeleteLine2 = result2.getMemento().getRawOperationsStream().allMatch(operationWithKey -> operationWithKey.getNeedDeletedKey() == 0L);
//
//            // 両方ともライン消去が絡むときは探索カット
//            if (!noDeleteLine1 && !noDeleteLine2) {
//                System.out.print(".");
//                continue;
//            }

//            // すべてのパターンを網羅できるかチェック
//            boolean canAll = checksAll90(lookUp, allBlocks, possibleTree1, possibleTree2);
//            if (!canAll) {
//                System.out.print("*");
//                continue;
//            }

            CombinationIterable<Block> blockCombinations = new CombinationIterable<>(Block.valueList(), 2);
            for (List<Block> blockCombination : blockCombinations) {
                assert blockCombination.size() == 2;
                System.out.println(blockCombination);
                Block block1 = blockCombination.get(0);
                Block block2 = blockCombination.get(1);

                // 条件で手順を分割
//                Map<Boolean, List<Blocks>> conditional = allBlocks.stream()
//                        .collect(Collectors.groupingBy(o -> {
//                            List<Block> blockList = o.getBlockList();
//                            return blockList.indexOf(block1) < blockList.indexOf(block2);
//                        }));
                // 条件ごとパフェ成功確率を計算する
//                Pair<AnalyzeTree, AnalyzeTree> pair1 = calcPerfectPercent(lookUp, conditional, possibleTree1);
//                Pair<AnalyzeTree, AnalyzeTree> pair2 = calcPerfectPercent(lookUp, conditional, possibleTree2);

                System.out.println();
                System.out.println(blockCombination);
//                System.out.printf("pattern1 | %.2f | %.2f %n", pair1.getKey().getSuccessPercent(), pair1.getValue().getSuccessPercent());
//                System.out.printf("pattern2 | %.2f | %.2f %n", pair2.getKey().getSuccessPercent(), pair2.getValue().getSuccessPercent());
//
                String tetfu1 = easyTetfu.encode(initField, operationWithKeys1, height);
                System.out.println(tetfu1);

                String tetfu2 = easyTetfu.encode(initField, operationWithKeys1, height);
                System.out.println(tetfu2);
            }
        }
    }

    private static boolean checksAll90(ForwardOrderLookUp lookUp, List<Blocks> allBlocks, AnalyzeTree possibleTree1, AnalyzeTree possibleTree2) {
//        int need = (int) (allBlocks.size() * 0.8 );
        int count = 0;
        int no = 0;
        for (Blocks target : allBlocks) {
            boolean anyMatch = lookUp.parse(target.getBlocks())
                    .map(LongBlocks::new)
                    .anyMatch(blocks -> possibleTree1.isVisited(blocks) || possibleTree2.isVisited(blocks));
            if (anyMatch) {
                count++;
            } else {
                no++;
//                if (need < no)
//                    return false;
            }
        }
        double percent = (double) count / allBlocks.size();
        return 0.8 < percent;
    }

    private static boolean checksAll(ForwardOrderLookUp lookUp, List<Blocks> allBlocks, AnalyzeTree possibleTree) {
        return allBlocks.parallelStream()
                .map(Blocks::getBlocks)
                .map(lookUp::parse)
                .map(stream -> stream.map(LongBlocks::new))
                .allMatch(stream -> stream.anyMatch(possibleTree::isVisited));
    }

    private static boolean checksAllPercent(ForwardOrderLookUp lookUp, List<Blocks> allBlocks, AnalyzeTree possibleTree, double percent) {
        long succeedCount = allBlocks.parallelStream()
                .map(Blocks::getBlocks)
                .map(lookUp::parse)
                .map(stream -> stream.map(LongBlocks::new))
                .filter(stream -> stream.anyMatch(possibleTree::isVisited))
                .count();
        return percent <= succeedCount / allBlocks.size();
    }

    private static boolean checksAll(ForwardOrderLookUp lookUp, List<Blocks> allBlocks, AnalyzeTree possibleTree1, AnalyzeTree possibleTree2) {
        for (Blocks target : allBlocks) {
            boolean anyMatch = lookUp.parse(target.getBlocks())
                    .map(LongBlocks::new)
                    .anyMatch(blocks -> possibleTree1.isVisited(blocks) || possibleTree2.isVisited(blocks));
            if (!anyMatch)
                return false;
        }
        return true;
    }

    private static BasicSolutions createMappedBasicSolutions(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        return new MappedBasicSolutions(calculate);
    }

    private static SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }

    private static Pair<AnalyzeTree, AnalyzeTree> calcPerfectPercent(ForwardOrderLookUp lookUp, Map<Boolean, List<Blocks>> conditional, AnalyzeTree possibleTree1) {
        AnalyzeTree resultTreeTrue = new AnalyzeTree();
        for (Blocks blocks : conditional.get(true)) {
            boolean anyMatch = lookUp.parse(blocks.getBlocks())
                    .map(LongBlocks::new)
                    .anyMatch(possibleTree1::isVisited);
            resultTreeTrue.set(anyMatch, blocks);
        }

        AnalyzeTree resultTreeFalse = new AnalyzeTree();
        for (Blocks blocks : conditional.get(false)) {
            boolean anyMatch = lookUp.parse(blocks.getBlocks())
                    .map(LongBlocks::new)
                    .anyMatch(possibleTree1::isVisited);
            resultTreeFalse.set(anyMatch, blocks);
        }

        return new Pair<>(resultTreeTrue, resultTreeFalse);
    }

    private static AnalyzeTree getPossibleBuildingTree(Field field, List<OperationWithKey> operationWithKeys, BuildUpStream buildUpStream) {
        Set<LongBlocks> canBuild = buildUpStream.existsValidBuildPattern(field, operationWithKeys)
                .map(List::stream)
                .map(stream -> stream.map(OperationWithKey::getMino))
                .map(stream -> stream.map(Mino::getBlock))
                .map(LongBlocks::new)
                .collect(Collectors.toSet());

        AnalyzeTree tree1 = new AnalyzeTree();
        for (LongBlocks blocks : canBuild) {
            tree1.success(blocks);
        }
        return tree1;
    }

    private static List<TetfuElement> parseTetfuElements(Field initField, ColorConverter colorConverter, Operations operations) {
        ColoredField grayField = ColoredFieldFactory.createGrayField(initField);
        List<? extends Operation> operationsList = operations.getOperations();
        ArrayList<TetfuElement> elements = new ArrayList<>();
        for (int index = 0; index < operationsList.size(); index++) {
            Operation o = operationsList.get(index);
            if (index == 0)
                elements.add(new TetfuElement(grayField, colorConverter.parseToColorType(o.getBlock()), o.getRotate(), o.getX(), o.getY()));
            else
                elements.add(new TetfuElement(colorConverter.parseToColorType(o.getBlock()), o.getRotate(), o.getX(), o.getY()));
        }
        return elements;
    }
}

package _experimental;

import common.buildup.BuildUp;
import common.buildup.BuildUpStream;
import common.datastore.*;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.iterable.CombinationIterable;
import common.order.ForwardOrderLookUp;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import common.pattern.PiecesGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import common.tree.AnalyzeTree;
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
import lib.MyIterables;
import lib.Randoms;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class IfSelector {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 準備
        int height = 4;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        BuildUpStream buildUpStream = new BuildUpStream(reachable, height);
        int maxDepth = 6;
        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(maxDepth, 7);
        ColorConverter colorConverter = new ColorConverter();

        // フィールドの指定
        Field initField = FieldFactory.createField("" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____"
        );

        // SRS: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=Mapped
        int width = 3;
        SizedBit sizedBit = new SizedBit(width, height);
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // Assert
        SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);

        // パフェ手順の列挙
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        List<Result> results = searcher.toList();

        BlockCounter allIncluded = new BlockCounter(Block.valueList());
        results = results.stream()
                .filter(result -> allIncluded.containsAll(new BlockCounter(result.getMemento().getOperationsStream(width).map(OperationWithKey::getMino).map(Mino::getBlock))))
                .collect(Collectors.toList());

        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        List<Blocks> allBlocks = MyIterables.toList(piecesGenerator);

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

            // すべてのパターンを網羅できるかチェック
            boolean canAll = checksAll90(lookUp, allBlocks, possibleTree1, possibleTree2);
            if (!canAll) {
                System.out.print("*");
                continue;
            }

            CombinationIterable<Block> blockCombinations = new CombinationIterable<>(Block.valueList(), 2);
            for (List<Block> blockCombination : blockCombinations) {
                assert blockCombination.size() == 2;
                System.out.println(blockCombination);
                Block block1 = blockCombination.get(0);
                Block block2 = blockCombination.get(1);

                // 条件で手順を分割
                Map<Boolean, List<Blocks>> conditional = allBlocks.stream()
                        .collect(Collectors.groupingBy(o -> {
                            List<Block> blockList = o.getBlockList();
                            return blockList.indexOf(block1) < blockList.indexOf(block2);
                        }));
                // 条件ごとパフェ成功確率を計算する
                Pair<AnalyzeTree, AnalyzeTree> pair1 = calcPerfectPercent(lookUp, conditional, possibleTree1);
                Pair<AnalyzeTree, AnalyzeTree> pair2 = calcPerfectPercent(lookUp, conditional, possibleTree2);

                System.out.println();
                System.out.println(blockCombination);
                System.out.printf("pattern1 | %.2f | %.2f %n", pair1.getKey().getSuccessPercent(), pair1.getValue().getSuccessPercent());
                System.out.printf("pattern2 | %.2f | %.2f %n", pair2.getKey().getSuccessPercent(), pair2.getValue().getSuccessPercent());

                BlockField blockField1 = createBlockField(height, operationWithKeys1);
                String tetfu1 = parseTetfu(minoFactory, colorConverter, initField, blockField1);
                System.out.println(tetfu1);

                BlockField blockField2 = createBlockField(height, operationWithKeys2);
                String tetfu2 = parseTetfu(minoFactory, colorConverter, initField, blockField2);
                System.out.println(tetfu2);
            }
        }
    }


    private static BlockField createBlockField(int height, List<OperationWithKey> operationWithKeys1) {
        BlockField blockField1 = new BlockField(height);
        operationWithKeys1
                .forEach(key -> {
                    Field test = FieldFactory.createField(height);
                    Mino mino = key.getMino();
                    test.put(mino, key.getX(), key.getY());
                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                    blockField1.merge(test, mino.getBlock());
                });
        return blockField1;
    }

    private static boolean checksAll90(ForwardOrderLookUp lookUp, List<Blocks> allBlocks, AnalyzeTree possibleTree1, AnalyzeTree possibleTree2) {
//        int need = (int) (allBlocks.size() * 0.8 );
        int count = 0;
        int no = 0;
        for (Blocks target : allBlocks) {
            boolean anyMatch = lookUp.parse(target.getBlockList())
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

    private static boolean checksAll(ForwardOrderLookUp lookUp, List<Blocks> allBlocks, AnalyzeTree possibleTree1, AnalyzeTree possibleTree2) {
        for (Blocks target : allBlocks) {
            boolean anyMatch = lookUp.parse(target.getBlockList())
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
            boolean anyMatch = lookUp.parse(blocks.getBlockList())
                    .map(LongBlocks::new)
                    .anyMatch(possibleTree1::isVisited);
            resultTreeTrue.set(anyMatch, blocks);
        }

        AnalyzeTree resultTreeFalse = new AnalyzeTree();
        for (Blocks blocks : conditional.get(false)) {
            boolean anyMatch = lookUp.parse(blocks.getBlockList())
                    .map(LongBlocks::new)
                    .anyMatch(possibleTree1::isVisited);
            resultTreeFalse.set(anyMatch, blocks);
        }

        return new Pair<>(resultTreeTrue, resultTreeFalse);
    }

    private static List<OperationWithKey> getOperationWithKeys(int height, MinoFactory minoFactory, LockedReachable reachable, Field field, List<String> operationLines) {
        String operationString = operationLines.stream().collect(Collectors.joining(";"));
        Operations operations = OperationInterpreter.parseToOperations(operationString);
        List<OperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, height);
        assert BuildUp.cansBuild(field, operationWithKeys, height, reachable);
        return operationWithKeys;
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

    private static String parseTetfu(MinoFactory minoFactory, ColorConverter colorConverter, Field initField, BlockField blockField) {
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
//        List<TetfuElement> tetfuElements = parseTetfuElements(initField, colorConverter, operations1);
        TetfuElement elementOnePage = parseBlockFieldToTetfuElement(initField, colorConverter, blockField, "");
        return "http://fumen.zui.jp/?v115@" + tetfu.encode(Collections.singletonList(elementOnePage));
    }

    private static TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, BlockField blockField, String comment) {
        ColoredField coloredField = ColoredFieldFactory.createGrayField(initField);

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

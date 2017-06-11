package searcher.pack.task;

import _implements.parity_based_pack.ParityBasedPackSearcher;
import common.OperationWithKeyHelper;
import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.iterable.CombinationIterable;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.pack.InOutPairField;
import searcher.pack.MinoField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.memento.UsingBlockAndValidKeySolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsCalculator;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class PackSearcherComparingParityBasedTest {
    private static final int FIELD_WIDTH = 10;

    // パリティベースとの探索結果を比較する (同一ミノは2つまで)
    @Test
    public void testAllSRSValidPacks() throws Exception {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, Set<MinoField>> calculate = calculator.calculate();

        List<Block> allBlocks = new ArrayList<>();
        allBlocks.addAll(Arrays.asList(Block.values()));
        allBlocks.addAll(Arrays.asList(Block.values()));

        for (int popCount = 1; popCount <= 7; popCount++) {
            CombinationIterable<Block> iterable = new CombinationIterable<>(allBlocks, popCount);
            Set<BlockCounter> searching = StreamSupport.stream(iterable.spliterator(), false)
                    .map(BlockCounter::new)
                    .collect(Collectors.toSet());

            for (BlockCounter blockCounter : searching) {
                // 準備
                List<Block> usingBlocks = blockCounter.getBlocks();
                Set<BlockCounter> blockCounters = Collections.singleton(blockCounter);
                Field initField = createSquareEmptyField(popCount, height);

                System.out.println(usingBlocks);

                // packで探索
                SolutionFilter solutionFilter = createUsingBlockAndValidKeyMementoFilter(initField, sizedBit, blockCounters);
                BasicSolutions basicSolutions = BasicSolutions.createFromSet(calculate, solutionFilter);
                List<Result> results = calculateSRSValidCount(sizedBit, basicSolutions, initField, solutionFilter);
                Set<String> packSolutions = results.stream()
                        .map(Result::getMemento)
                        .map(MinoFieldMemento::getOperations)
                        .map(operationWithKeys -> {
                            operationWithKeys.sort(OperationWithKeyComparator::compareOperationWithKey);
                            return operationWithKeys;
                        })
                        .map(OperationWithKeyHelper::parseToString)
                        .collect(Collectors.toSet());

                int packCounter = packSolutions.size();
                System.out.println(packCounter);

                // parity-based packで探索
                ParityBasedPackSearcher searcher = new ParityBasedPackSearcher(initField, height);
                Set<String> paritySolutions = searcher.search(usingBlocks)
                        .map(operationWithKeys -> {
                            operationWithKeys.sort(OperationWithKeyComparator::compareOperationWithKey);
                            return operationWithKeys;
                        })
                        .sorted(this::compareOperationWithKeyList)
                        .map(OperationWithKeyHelper::parseToString)
                        .collect(Collectors.toSet());
                int parityCounter = paritySolutions.size();

                // packの解の数は、parity以上である
                assertThat(packCounter, is(greaterThanOrEqualTo(parityCounter)));

                // parityからpackでひく
                // 少なくともparityで見つかる解は、packでも見つけられることを保証
                paritySolutions.removeAll(packSolutions);

                if (!paritySolutions.isEmpty()) {
                    // 間違っている可能性がある解
                    for (String solution : paritySolutions)
                        System.out.println(solution);
                }

                assertThat(paritySolutions, hasSize(0));
            }
        }
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private List<Result> calculateSRSValidCount(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField, SolutionFilter solutionFilter) throws InterruptedException, ExecutionException {
        // フィールドの変換
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);

        // 探索準備
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);

        // 探索
        return searcher.toList();
    }

    // パフェするまでに有効なブロック数を列挙する
    private SolutionFilter createUsingBlockAndValidKeyMementoFilter(Field initField, SizedBit sizedBit, Set<BlockCounter> counters) {
        HashSet<Long> validBlockCounters = new HashSet<>();

        for (BlockCounter counter : counters) {
            List<Block> usingBlocks = counter.getBlocks();
            for (int size = 1; size <= usingBlocks.size(); size++) {
                CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
                for (List<Block> blocks : combinationIterable) {
                    BlockCounter newCounter = new BlockCounter(blocks);
                    validBlockCounters.add(newCounter.getCounter());
                }
            }
        }

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new UsingBlockAndValidKeySolutionFilter(initField, validBlockCounters, reachableThreadLocal, sizedBit.getHeight());
    }

    private Field createSquareEmptyField(int emptyWidth, int emptyHeight) {
        Field field = FieldFactory.createField(emptyHeight);
        for (int x = emptyWidth; x < FIELD_WIDTH; x++)
            for (int y = 0; y < emptyHeight; y++)
                field.setBlock(x, y);
        return field;
    }

    private int compareOperationWithKeyList(List<OperationWithKey> o1, List<OperationWithKey> o2) {
        int size1 = o1.size();
        int size2 = o2.size();
        int compareSize = Integer.compare(size1, size2);
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < size1; index++) {
            int compare = OperationWithKeyComparator.compareOperationWithKey(o1.get(index), o2.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }
}
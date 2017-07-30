package searcher.pack.task;

import common.datastore.BlockCounter;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.iterable.CombinationIterable;
import common.parser.BlockInterpreter;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.memento.UsingBlockAndValidKeySolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PackSearcherComparingParityBasedTest {
    private static class TestData {
        private final Pieces pieces;
        private final long count;

        private TestData(Pieces pieces, long count) {
            this.pieces = pieces;
            this.count = count;
        }

        public List<Block> getBlocks() {
            return pieces.getBlocks();
        }

        public long getCount() {
            return count;
        }
    }

    private static final int FIELD_WIDTH = 10;

    // 高さ4: パリティベースとの探索結果を比較する (同一ミノは2つまで)
    @Test
    void testAllSRSValidPacksHeight4() throws Exception {
        int width = 3;
        int height = 4;

        String resultPath = ClassLoader.getSystemResource("perfects/pack_height4.txt").getPath();
        List<TestData> testCases = Files.lines(Paths.get(resultPath))
                .map(line -> line.split("//")[0])
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split("="))
                .map(split -> {
                    Stream<Block> blocks = BlockInterpreter.parse(split[0]);
                    LongPieces pieces = new LongPieces(blocks);
                    int count = Integer.valueOf(split[1]);
                    return new TestData(pieces, count);
                })
                .collect(Collectors.toList());

        compareCount(width, height, testCases);
    }

    // 高さ5: パリティベースとの探索結果を比較する (同一ミノは2つまで)
    @Test
    void testAllSRSValidPacksHeight5() throws Exception {
        int width = 2;
        int height = 5;

        String resultPath = ClassLoader.getSystemResource("perfects/pack_height5.txt").getPath();
        List<TestData> testCases = Files.lines(Paths.get(resultPath))
                .map(line -> line.split("//")[0])
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split("="))
                .map(split -> {
                    Stream<Block> blocks = BlockInterpreter.parse(split[0]);
                    LongPieces pieces = new LongPieces(blocks);
                    int count = Integer.valueOf(split[1]);
                    return new TestData(pieces, count);
                })
                .collect(Collectors.toList());

        compareCount(width, height, testCases);
    }

    // 高さ6: パリティベースとの探索結果を比較する (同一ミノは2つまで)
    @Test
    void testAllSRSValidPacksHeight6() throws Exception {
        int width = 2;
        int height = 6;

        String resultPath = ClassLoader.getSystemResource("perfects/pack_height6.txt").getPath();
        List<TestData> testCases = Files.lines(Paths.get(resultPath))
                .map(line -> line.split("//")[0])
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split("="))
                .map(split -> {
                    Stream<Block> blocks = BlockInterpreter.parse(split[0]);
                    LongPieces pieces = new LongPieces(blocks);
                    int count = Integer.valueOf(split[1]);
                    return new TestData(pieces, count);
                })
                .collect(Collectors.toList());

        compareCount(width, height, testCases);
    }

    private void compareCount(int width, int height, List<TestData> testDataList) throws InterruptedException, ExecutionException {
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();

        for (TestData data : testDataList) {
            // 準備
            List<Block> usingBlocks = data.getBlocks();
            int popCount = usingBlocks.size();
            Field initField = createSquareEmptyField(height, popCount);

            // packで探索
            Set<BlockCounter> blockCounters = Collections.singleton(new BlockCounter(usingBlocks));
            SolutionFilter solutionFilter = createUsingBlockAndValidKeyMementoFilter(initField, sizedBit, blockCounters);
            BasicSolutions basicSolutions = new MappedBasicSolutions(calculate, solutionFilter);
            long packCounter = calculateSRSValidCount(sizedBit, basicSolutions, initField, solutionFilter);

            System.out.println(usingBlocks);

            assertThat(packCounter).isEqualTo(data.getCount());
        }
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private long calculateSRSValidCount(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField, SolutionFilter solutionFilter) throws InterruptedException, ExecutionException {
        // フィールドの変換
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);

        // 探索準備
        TaskResultHelper taskResultHelper = createTaskResultHelper(sizedBit);
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);

        // 探索
        return searcher.count();
    }

    private TaskResultHelper createTaskResultHelper(SizedBit sizedBit) {
        if (sizedBit.getWidth() == 4 && sizedBit.getWidth() == 3)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
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
        return new UsingBlockAndValidKeySolutionFilter(initField, validBlockCounters, reachableThreadLocal, sizedBit);
    }

    private Field createSquareEmptyField(int emptyHeight, int popCount) {
        int needBlock = 4 * popCount;
        int emptyWidth = (needBlock - 1) / emptyHeight + 1;
        Field field = FieldFactory.createField(emptyHeight);
        for (int x = emptyWidth; x < FIELD_WIDTH; x++)
            for (int y = 0; y < emptyHeight; y++)
                field.setBlock(x, y);

        for (int y = 0; y < emptyHeight * emptyWidth - needBlock; y++)
            field.setBlock(0, y);
        return field;
    }
}
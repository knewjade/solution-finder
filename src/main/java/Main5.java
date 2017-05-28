import common.Stopwatch;
import common.pattern.PiecesGenerator;
import core.column_field.ColumnField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import pack.separable_mino.SeparableMino;
import pack.separable_mino.SeparableMinoFactory;
import searcher.pack.MinoField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Main5 {
    public static void main(String[] args) {
        // 7種1巡で可能性のあるツモ順
        List<String> patterns = Arrays.asList(
                "*p7, *p4",
                "*, *p3, *p7",
                "*, *p7, *p3",
                "*, *p4, *p6",
                "*, *, *p7, *p2",
                "*, *p5, *p5",
                "*, *p2, *p7, *",
                "*, *p6, *p4"
        );

        // ツモ順の列挙
        PiecesGenerator piecesGenerator = new PiecesGenerator(patterns);
//        long count = piecesGenerator.stream().parallel().count();
//        System.out.println(count);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 3;
        int height = 5;
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, width, height);
        List<SeparableMino> minos = factory.create();
        SeparableMinos separableMinos = new SeparableMinos(minos);
        System.out.println(minos.size());
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, new SizedBit(width, height));
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        System.out.println("calc");
        Map<ColumnField, Set<MinoField>> calculate = calculator.calculate();
        System.out.println(calculate.size());
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));


//        HashSet<List<Block>> leastBlocks = StreamSupport.stream(piecesGenerator.spliterator(), true)
//                .map(SafePieces::getBlocks)
//                .collect(Collectors.toCollection(HashSet::new));
//        System.out.println(leastBlocks.size());
//        for (List<Block> leastBlock : leastBlocks) {
//            System.out.println(leastBlock);
//        }

//        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();
//        PiecesStreamBuilder builder = new PiecesStreamBuilder("*p7, *p4");
//        long count = builder.stream().parallel().count();
//        System.out.println(count);
//        stopwatch1.stop();
//
//        System.out.println(stopwatch1.toMessage(TimeUnit.MILLISECONDS));
//
//        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();
//        builder = new PiecesStreamBuilder("*p7, *p4");
//        List<String> collect = builder.stream()
//                .parallel()
//                .map(Pieces::getBlocks)
//                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining("")))
//                .peek(System.out::println)
//                .collect(Collectors.toList());
//        System.out.println(collect.size());
//        stopwatch2.stop();

//        System.out.println(stopwatch2.toMessage(TimeUnit.MILLISECONDS));
    }
}

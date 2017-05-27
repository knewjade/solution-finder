import common.pattern.PiecesGenerator;

import java.util.Arrays;
import java.util.List;

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
        long count = piecesGenerator.stream().parallel().count();
        System.out.println(count);
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

import common.Stopwatch;
import common.datastore.Pieces;
import common.datastore.SafePieces;
import common.pattern.PiecesGenerator;
import core.mino.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Main5 {
    public static void main(String[] args) {
        // 7種1巡で可能性のあるツモ順
        List<String> patterns = Arrays.asList(
                "*p7, *p4"
//                "*p7, *p4",
//                "*, *p3, *p7",
//                "*, *p7, *p3",
//                "*, *p4, *p6",
//                "*, *, *p7, *p2",
//                "*, *p5, *p5",
//                "*, *p2, *p7, *",
//                "*, *p6, *p4"
        );

        List<String> pattern2 = patterns.stream()
                .map(str -> {
                    if (str.contains("#"))
                        return str.substring(0, str.indexOf('#'));
                    return str;
                })
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());

        // ツモ順の列挙
//        PiecesGenerator piecesGenerator = new PiecesGenerator(pattern2);
//        HashSet<List<Block>> leastBlocks = StreamSupport.stream(piecesGenerator.spliterator(), true)
//                .map(SafePieces::getBlocks)
//                .collect(Collectors.toCollection(HashSet::new));
//        System.out.println(leastBlocks.size());
//        for (List<Block> leastBlock : leastBlocks) {
//            System.out.println(leastBlock);
//        }

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();
        PiecesStreamBuilder builder = new PiecesStreamBuilder("*p7, *p4");
        long count = builder.stream().parallel().count();
        System.out.println(count);
        stopwatch1.stop();

        System.out.println(stopwatch1.toMessage(TimeUnit.MILLISECONDS));

        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();
        builder = new PiecesStreamBuilder("*p7, *p4");
        List<String> collect = builder.stream()
                .parallel()
                .map(Pieces::getBlocks)
                .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining("")))
                .peek(System.out::println)
                .collect(Collectors.toList());
        System.out.println(collect.size());
        stopwatch2.stop();

        System.out.println(stopwatch2.toMessage(TimeUnit.MILLISECONDS));
    }
}

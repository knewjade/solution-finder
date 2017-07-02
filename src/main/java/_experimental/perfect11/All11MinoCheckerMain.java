package _experimental.perfect11;

import common.datastore.BlockCounter;
import common.datastore.pieces.LongPieces;
import common.order.ForwardOrderLookUp;
import core.mino.Block;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class All11MinoCheckerMain {
    public static void main(String[] args) throws IOException {
        System.out.println("start");

        // パフェできない組みあわせ
        Path allNGOrders = Paths.get("output/combAllNG.csv");
        HashSet<BlockCounter> blockCounters = Files.lines(allNGOrders)
                .map(All11MinoCheckerMain::parse10)
                .map(BlockCounter::new)
                .collect(Collectors.toCollection(HashSet::new));

        // パフェできない順序
        Path includeNGOrder = Paths.get("output/orderNG.csv");
        Set<LongPieces> ngPieces = Files.lines(includeNGOrder)
                .map(All11MinoCheckerMain::parse10)
                .map(LongPieces::new)
                .collect(Collectors.toSet());

        //
        Path all11onhold = Paths.get("output/all11onhold.csv");
        AtomicInteger counter = new AtomicInteger();
        List<String> failed = Files.lines(all11onhold)
                .parallel()
                .peek(s -> {
                    int i = counter.incrementAndGet();
                    if (i % 10000000 == 0)
                        System.out.println(i);
                })
                .filter(line -> {
                    // パフェできないものは true で次に送る

                    // パフェできない組み合わせである
                    BlockCounter blockCounter = new BlockCounter(parse10(line));
                    if (blockCounters.contains(blockCounter))
                        return true;

                    // パフェできない順序である
                    LongPieces pieces = new LongPieces(parse10(line));
                    return ngPieces.contains(pieces);
                })
                .filter(line -> {
                    List<Block> blocks = parse11(line).collect(Collectors.toList());
                    ForwardOrderLookUp orderLookUp = new ForwardOrderLookUp(10, true);

                    // すべてのパターンでパフェできないものは true で次に送る
                    return orderLookUp.parse(blocks)
                            .map(LongPieces::new)
                            .allMatch(pieces -> {
                                // パフェできないものは true で次に送る

                                // パフェできない順序である
                                if (ngPieces.contains(pieces))
                                    return true;

                                // パフェできない組み合わせである
                                BlockCounter blockCounter = new BlockCounter(pieces.getBlockStream());
                                return blockCounters.contains(blockCounter);
                            });

                })
                .peek(System.out::println)
                .collect(Collectors.toList());

        System.out.println("check count: " + counter);

        System.out.println("failed: " + failed.size());
        failed.forEach(System.out::println);

        System.out.println("end");
    }

    private static Stream<Block> parse10(String a) {
        return Stream.of(
                parse(a.charAt(0)),
                parse(a.charAt(1)),
                parse(a.charAt(2)),
                parse(a.charAt(3)),
                parse(a.charAt(4)),
                parse(a.charAt(5)),
                parse(a.charAt(6)),
                parse(a.charAt(7)),
                parse(a.charAt(8)),
                parse(a.charAt(9))
        );
    }

    private static Stream<Block> parse11(String a) {
        assert a.length() == 11;
        return Stream.of(
                parse(a.charAt(0)),
                parse(a.charAt(1)),
                parse(a.charAt(2)),
                parse(a.charAt(3)),
                parse(a.charAt(4)),
                parse(a.charAt(5)),
                parse(a.charAt(6)),
                parse(a.charAt(7)),
                parse(a.charAt(8)),
                parse(a.charAt(9)),
                parse(a.charAt(10))
        );
    }

    private static Block parse(char ch) {
        switch (ch) {
            case 'T':
                return Block.T;
            case 'S':
                return Block.S;
            case 'Z':
                return Block.Z;
            case 'O':
                return Block.O;
            case 'I':
                return Block.I;
            case 'L':
                return Block.L;
            case 'J':
                return Block.J;
        }
        throw new IllegalStateException("No reachable");
    }
}

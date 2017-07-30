package common.parser;

import core.mino.Block;

import java.util.stream.Stream;

import static common.parser.StringEnumTransform.toBlock;

public class BlockInterpreter {
    public static Stream<Block> parse10(String a) {
        return Stream.of(
                toBlock(a.charAt(0)),
                toBlock(a.charAt(1)),
                toBlock(a.charAt(2)),
                toBlock(a.charAt(3)),
                toBlock(a.charAt(4)),
                toBlock(a.charAt(5)),
                toBlock(a.charAt(6)),
                toBlock(a.charAt(7)),
                toBlock(a.charAt(8)),
                toBlock(a.charAt(9))
        );
    }

    public static Stream<Block> parse11(String a) {
        return Stream.of(
                toBlock(a.charAt(0)),
                toBlock(a.charAt(1)),
                toBlock(a.charAt(2)),
                toBlock(a.charAt(3)),
                toBlock(a.charAt(4)),
                toBlock(a.charAt(5)),
                toBlock(a.charAt(6)),
                toBlock(a.charAt(7)),
                toBlock(a.charAt(8)),
                toBlock(a.charAt(9)),
                toBlock(a.charAt(10))
        );
    }

    public static Stream<Block> parse(String str) {
        Stream.Builder<Block> builder = Stream.builder();
        for (int index = 0; index < str.length(); index++) {
            char c = str.charAt(index);
            Block block = StringEnumTransform.toBlock(c);
            builder.accept(block);
        }
        return builder.build();
    }
}

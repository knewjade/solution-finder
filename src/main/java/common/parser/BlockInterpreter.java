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
        switch (str.length()) {
            case 10:
                return parse10(str);
            case 11:
                return parse11(str);
        }
        throw new UnsupportedOperationException();
    }
}

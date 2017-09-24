package common.pattern;


import common.SyntaxException;
import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlocksGenerator implements IBlocksGenerator {
    public static void verify(String pattern) throws SyntaxException {
        verify(Collections.singletonList(pattern));
    }

    public static void verify(List<String> patterns) throws SyntaxException {
        int depth = -1;
        for (int index = 0; index < patterns.size(); index++) {
            String pattern = patterns.get(index);
            if (pattern.contains("#"))
                pattern = pattern.substring(0, pattern.indexOf('#'));

            pattern = pattern.trim();

            if (pattern.equals(""))
                continue;

            int currentDepth = 0;
            String[] splits = pattern.split(",");
            for (String split : splits) {
                try {
                    currentDepth += PatternElement.verify(split);
                } catch (SyntaxException e) {
                    String message = String.format("'%s' on %d line :: cause = %s", split.trim(), index + 1, e.getMessage());
                    throw new SyntaxException(message);
                }
            }

            if (depth == -1) {
                depth = currentDepth;  // First depth
            } else if (depth != currentDepth) {
                String message = String.format("'%s' on %d line :: cause = %s", pattern.trim(), index + 1, "Num of blocks is not equal to others");
                throw new SyntaxException(message);
            }
        }
    }

    private final List<List<PatternElement>> elementsList;

    public BlocksGenerator(String pattern) {
        this(Collections.singletonList(pattern));
    }

    public BlocksGenerator(List<String> patterns) {
        this.elementsList = patterns.stream()
                .map(String::trim)
                .map(str -> {
                    if (str.startsWith("'") && str.endsWith("'"))
                        return str.substring(1, str.length() - 1);
                    return str;
                })
                .map(str -> {
                    if (str.contains("#"))
                        return str.substring(0, str.indexOf('#'));
                    return str;
                })
                .map(String::trim)
                .filter(pattern -> !pattern.isEmpty())
                .map(pattern -> {
                    return Arrays.stream(pattern.split(","))
                            .map(PatternElement::parseWithoutCheck)
                            .map(Optional::get)
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
    }

    @Override
    public int getDepth() {
        if (elementsList.isEmpty())
            return 0;
        return new PiecesStreamBuilder(elementsList.get(0)).getDepths();
    }

    @Override
    public Stream<Blocks> blocksStream() {
        Stream<Blocks> stream = Stream.empty();
        for (List<PatternElement> elements : elementsList)
            stream = Stream.concat(stream, new PiecesStreamBuilder(elements).blocksStream());
        return stream;
    }

    @Override
    public Stream<BlockCounter> blockCountersStream() {
        Stream<BlockCounter> stream = Stream.empty();
        for (List<PatternElement> elements : elementsList)
            stream = Stream.concat(stream, new PiecesStreamBuilder(elements).blockCountersStream());
        return stream;
    }
}



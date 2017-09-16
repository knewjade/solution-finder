package common.pattern;

import common.SyntaxException;
import common.datastore.BlockCounter;
import core.mino.Block;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternElement {
    private static final List<Block> ALL_BLOCKS = Block.valueList();
    private static final Map<String, Block> nameToBlock = new HashMap<>();

    static {
        for (Block block : Block.values())
            nameToBlock.put(block.getName(), block);
    }

    static Optional<PatternElement> parseWithoutCheck(String pattern) {
        try {
            return Optional.of(parse(pattern));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    static int verify(String pattern) throws SyntaxException {
        return parse(pattern).getPopCount();
    }

    private static PatternElement parse(String pattern) throws SyntaxException {
        PatternElement parser = createParser(pattern);

        if (parser.blocks.size() <= 0)
            throw new SyntaxException("no blocks");

        if (parser.popCount <= 0)
            throw new SyntaxException("no pop");

        if (parser.blocks.size() < parser.popCount)
            throw new SyntaxException("num of blocks < pop count");

        return parser;
    }

    private static PatternElement createParser(String pattern) throws SyntaxException {
        String trim = pattern.toUpperCase().replaceAll(" ", "");

        if (trim.equals("*")) {
            return new PatternElement(ALL_BLOCKS, 1);
        } else if (trim.equals("*!")) {
            return new PatternElement(ALL_BLOCKS, ALL_BLOCKS.size());
        } else if (trim.startsWith("*P")) {
            String number = trim.substring(trim.indexOf("P") + 1, trim.length());
            int popCount = getNumber(number);
            return new PatternElement(ALL_BLOCKS, popCount);
        } else if (trim.contains("[") && trim.contains("]")) {
            int startBracket = trim.indexOf("[");
            if (startBracket != 0)
                throw new SyntaxException("'[' on at the head");

            int endBracket = trim.lastIndexOf("]");
            String blockNames = trim.substring(startBracket + 1, endBracket);
            if (blockNames.isEmpty())
                throw new SyntaxException("Empty in []");

            if (blockNames.contains("["))
                throw new SyntaxException("Too much '['");

            if (blockNames.contains("]"))
                throw new SyntaxException("Too much ']'");

            if (!blockNames.matches("[TIOSZJL]+"))
                throw new SyntaxException("Unknown colorType in []");

            List<Block> blocks = Stream.of(blockNames.split(""))
                    .map(nameToBlock::get)
                    .collect(Collectors.toList());

            // Duplicate validation
            HashSet<Block> blockSet = new HashSet<>();
            for (Block block : blocks) {
                if (blockSet.contains(block))
                    throw new SyntaxException(String.format("Duplicate '%s' blocks in []", block.getName()));
                blockSet.add(block);
            }

            if (trim.contains("P")) {
                if (!trim.contains("]P"))
                    throw new SyntaxException("Should not exist character between [] and p");
                String number = trim.substring(trim.indexOf("P") + 1, trim.length());
                int popCount = getNumber(number);
                return new PatternElement(blocks, popCount);
            } else if (trim.contains("!")) {
                if (!trim.contains("]!"))
                    throw new SyntaxException("Should not exist character between [] and !");
                return new PatternElement(blocks, blocks.size());
            } else if (trim.startsWith("[") && trim.endsWith("]")) {
                return new PatternElement(blocks, 1);
            } else {
                throw new SyntaxException("No found 'p'");
            }
        } else if (trim.length() != 1) {
            if (trim.matches("[TIOSZJL]"))
                throw new SyntaxException("Too long blocks, use [] if select in blocks");
            else
                throw new SyntaxException("Unknown format");
        } else if (nameToBlock.containsKey(trim)) {
            List<Block> blocks = Collections.singletonList(nameToBlock.get(trim));
            return new PatternElement(blocks, 1);
        } else {
            throw new SyntaxException("Unknown colorType");
        }
    }

    private static int getNumber(String number) throws SyntaxException {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            throw new SyntaxException("Unexpected number format");
        }
    }

    private final List<Block> blocks;
    private final int popCount;

    private PatternElement(List<Block> blocks, int popCount) {
        this.blocks = blocks;
        this.popCount = popCount;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public int getPopCount() {
        return popCount;
    }
}

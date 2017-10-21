package entry;

import common.SyntaxException;
import common.datastore.BlockField;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import core.field.Field;
import exceptions.FinderInitializeException;

import java.util.List;

public class Verify {
    public static void field(Field field) throws FinderInitializeException {
        if (field == null)
            throw new FinderInitializeException("Should specify field");
    }

    public static void needFilledField(Field field) throws FinderInitializeException {
        field(field);

        if (field.isPerfect())
            throw new FinderInitializeException("Should specify filled field");
    }

    public static void maxClearLineUnder10(int maxClearLine) throws FinderInitializeException {
        if (maxClearLine < 2 || 10 < maxClearLine)
            throw new FinderInitializeException("Clear-Line should be 2 <= line <= 10: line=" + maxClearLine);
    }

    public static void maxClearLineUnder12(int maxClearLine) throws FinderInitializeException {
        if (maxClearLine < 2 || 12 < maxClearLine)
            throw new FinderInitializeException("Clear-Line should be 2 <= line <= 12: line=" + maxClearLine);
    }

    public static void reservedBlocks(BlockField blockField) throws FinderInitializeException {
        if (blockField == null)
            throw new FinderInitializeException("Invalid reserved blocks");
    }

    public static IBlocksGenerator patterns(List<String> patterns, int depth) throws FinderInitializeException {
        if (patterns.isEmpty())
            throw new FinderInitializeException("Should specify patterns, not allow empty");

        IBlocksGenerator generator = createBlocksGenerator(patterns);

        int piecesDepth = generator.getDepth();
        if (piecesDepth < depth)
            throw new FinderInitializeException(String.format("Should specify equal to or more than %d pieces: CurrentPieces=%d", depth, piecesDepth));

        return generator;
    }

    private static IBlocksGenerator createBlocksGenerator(List<String> patterns) throws FinderInitializeException {
        try {
            return new BlocksGenerator(patterns);
        } catch (SyntaxException e) {
            throw new FinderInitializeException("Pattern syntax error", e);
        }
    }

    public static int maxDepth(Field field, int maxClearLine) throws FinderInitializeException {
        int emptyCount = emptyCount(field, maxClearLine);
        return emptyCount / 4;
    }

    public static int minDepth(Field field) throws FinderInitializeException {
        return (int) Math.ceil(field.getNumOfAllBlocks() / 4.0);
    }

    private static int emptyCount(Field field, int maxClearLine) throws FinderInitializeException {
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
        if (emptyCount % 4 != 0)
            throw new FinderInitializeException("Empty block in field should be multiples of 4: EmptyCount=" + emptyCount);
        return emptyCount;
    }
}

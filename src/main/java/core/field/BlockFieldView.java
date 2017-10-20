package core.field;

import common.datastore.BlockField;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlockFieldView {
    private static final int FIELD_WIDTH = 10;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final char EMPTY_CHAR = '_';

    public static String toString(BlockField blockField) {
        return toString(blockField, blockField.getHeight());
    }

    public static String toString(BlockField blockField, int maxHeight) {
        return toStrings(blockField, maxHeight).stream().collect(Collectors.joining(LINE_SEPARATOR));
    }

    private static List<String> toStrings(BlockField blockField, int maxHeight) {
        assert maxHeight <= blockField.getHeight();
        ArrayList<String> lines = new ArrayList<>();
        for (int y = maxHeight - 1; y >= 0; y--) {
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < FIELD_WIDTH; x++) {
                Block block = blockField.getBlock(x, y);
                builder.append(block == null ? EMPTY_CHAR : block.getName());
            }
            lines.add(builder.toString());
        }
        return lines;
    }
}

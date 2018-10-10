package entry.path.output;

import common.datastore.MinoOperationWithKey;
import common.datastore.Pair;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BufferedFumenParser {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final OneFumenParser oneFumenParser;
    private final List<Pair<Long, TetfuElement>> elements = new ArrayList<>();

    public BufferedFumenParser(MinoFactory minoFactory, ColorConverter colorConverter, OneFumenParser oneFumenParser) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.oneFumenParser = oneFumenParser;
    }

    public synchronized void add(List<MinoOperationWithKey> operations, Field initField, int maxClearLine, String comment, long priority) {
        ColoredField coloredField = oneFumenParser.parseToColoredField(operations, initField, maxClearLine);
        TetfuElement element = new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
        elements.add(new Pair<>(priority, element));
    }

    public String parse() {
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        Comparator<Pair<Long, TetfuElement>> comparator = Comparator.comparingLong(Pair::getKey);
        elements.sort(comparator.reversed());
        return tetfu.encode(elements.stream().map(Pair::getValue).collect(Collectors.toList()));
    }
}

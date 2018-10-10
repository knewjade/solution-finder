package entry.path;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.datastore.blocks.LongPieces;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import entry.path.output.OneFumenParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PathPairs {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final List<PathPair> pathPairList;
    private final OneFumenParser oneFumenParser;
    private final int numOfAllPatternSequences;

    PathPairs(MinoFactory minoFactory, ColorConverter colorConverter, List<PathPair> pathPairList, OneFumenParser oneFumenParser, int numOfAllPatternSequences) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.pathPairList = pathPairList;
        this.oneFumenParser = oneFumenParser;
        this.numOfAllPatternSequences = numOfAllPatternSequences;
    }

    public int getNumOfAllPatternSequences() {
        return numOfAllPatternSequences;
    }

    public List<PathPair> getUniquePathPairList() {
        return pathPairList;
    }

    public List<PathPair> getMinimalPathPairList() {
        Selector<PathPair, LongPieces> selector = new Selector<>(pathPairList);
        return selector.select();
    }

    public String createMergedFumen(List<PathPair> pathPairList, Field initField, int maxClearLine) {
        Comparator<Pair<Long, TetfuElement>> comparator = Comparator.comparingLong(Pair::getKey);
        List<TetfuElement> elements = pathPairList.stream()
                .map(pathPair -> {
                    List<MinoOperationWithKey> operations = pathPair.getSampleOperations();
                    ColoredField coloredField = oneFumenParser.parseToColoredField(operations, initField, maxClearLine);

                    // パターンを表す名前 を生成
                    String blocksName = operations.stream()
                            .map(OperationWithKey::getPiece)
                            .map(Piece::getName)
                            .collect(Collectors.joining());


                    // 入力パターンのうち有効なミノ順を確率に変換
                    long counter = pathPair.blocksStreamForPattern().count();
                    double validPercent = (double) counter / numOfAllPatternSequences * 100.0;

                    String comment = String.format("%.1f %%: %s", validPercent, blocksName);
                    TetfuElement element = new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
                    return new Pair<>(counter, element);
                })
                .sorted(comparator.reversed())
                .map(Pair::getValue)
                .collect(Collectors.toList());

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        return tetfu.encode(elements);
    }
}

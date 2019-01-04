package entry.path.output;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OneFumenParser implements FumenParser {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public OneFumenParser(MinoFactory minoFactory, ColorConverter colorConverter) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
    }

    @Override
    public String parse(List<MinoOperationWithKey> operations, Field field, int maxClearLine) {
        // パターンを表す名前 を生成
        String blocksName = operations.stream()
                .map(OperationWithKey::getPiece)
                .map(Piece::getName)
                .collect(Collectors.joining());
        return parse(operations, field, maxClearLine, blocksName);
    }

    public String parse(List<MinoOperationWithKey> operations, Field field, int maxClearLine, String comment) {
        // テト譜1ページを作成
        ColoredField coloredField = parseToColoredField(operations, field, maxClearLine);
        TetfuElement tetfuElement = new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        return tetfu.encode(Collections.singletonList(tetfuElement));
    }

    public ColoredField parseToColoredField(List<MinoOperationWithKey> operations, Field initField, int maxClearLine) {
        // BlockField を生成
        BlockField blockField = createBlockField(operations, maxClearLine);

        ColoredField coloredField = createInitColoredField(initField, maxClearLine);

        for (Piece piece : Piece.values()) {
            Field target = blockField.get(piece);
            ColorType colorType = this.colorConverter.parseToColorType(piece);
            fillInField(coloredField, colorType, target, maxClearLine);
        }

        return coloredField;
    }

    private BlockField createBlockField(List<MinoOperationWithKey> operations, int maxClearLine) {
        BlockField blockField = new BlockField(maxClearLine);

        for (MinoOperationWithKey key : operations) {
            Field test = createField(key, maxClearLine);
            Mino mino = key.getMino();
            blockField.merge(test, mino.getPiece());
        }

        return blockField;
    }

    private Field createField(MinoOperationWithKey key, int maxClearLine) {
        Mino mino = key.getMino();
        Field test = FieldFactory.createField(maxClearLine);
        test.put(mino, key.getX(), key.getY());
        test.insertWhiteLineWithKey(key.getNeedDeletedKey());
        return test;
    }

    private ColoredField createInitColoredField(Field initField, int maxClearLine) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField, maxClearLine);
        return coloredField;
    }

    private void fillInField(ColoredField coloredField, ColorType colorType, Field target, int maxClearLine) {
        for (int y = 0; y < maxClearLine; y++)
            for (int x = 0; x < 10; x++)
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
    }
}

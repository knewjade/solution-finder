package entry.path.output;

import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
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
    public String parse(List<OperationWithKey> operations, Field field, int maxClearLine) {
        // BlockField を生成
        BlockField blockField = createBlockField(operations, maxClearLine);

        // パターンを表す名前 を生成
        String blocksName = operations.stream()
                .map(OperationWithKey::getMino)
                .map(Mino::getBlock)
                .map(Block::getName)
                .collect(Collectors.joining());

        // テト譜1ページを作成
        TetfuElement tetfuElement = createTetfuElement(field, blockField, blocksName, maxClearLine);

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        return tetfu.encode(Collections.singletonList(tetfuElement));
    }

    private BlockField createBlockField(List<OperationWithKey> operations, int maxClearLine) {
        BlockField blockField = new BlockField(maxClearLine);

        for (OperationWithKey key : operations) {
            Field test = createField(key, maxClearLine);
            Mino mino = key.getMino();
            blockField.merge(test, mino.getBlock());
        }

        return blockField;
    }

    private Field createField(OperationWithKey key, int maxClearLine) {
        Mino mino = key.getMino();
        Field test = FieldFactory.createField(maxClearLine);
        test.put(mino, key.getX(), key.getY());
        test.insertWhiteLineWithKey(key.getNeedDeletedKey());
        return test;
    }

    private TetfuElement createTetfuElement(Field initField, BlockField blockField, String comment, int maxClearLine) {
        ColoredField coloredField = createInitColoredField(initField, maxClearLine);

        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = this.colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target, maxClearLine);
        }

        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
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

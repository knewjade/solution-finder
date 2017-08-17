package _experimental.cycle1;

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

public class EasyTetfu {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public EasyTetfu() {
        this(new EasyPool());
    }

    public EasyTetfu(EasyPool easyPool) {
        this.minoFactory = easyPool.getMinoFactory();
        this.colorConverter = easyPool.getColorConverter();
    }

    public String encode(Field initField, List<OperationWithKey> operationWithKeys, int height) {
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        BlockField blockField = createBlockField(operationWithKeys, height);
        TetfuElement elementOnePage = parseBlockFieldToTetfuElement(initField, colorConverter, blockField, "");
        return "http://fumen.zui.jp/?v115@" + tetfu.encode(Collections.singletonList(elementOnePage));
    }

    private static BlockField createBlockField(List<OperationWithKey> operationWithKeys, int height) {
        BlockField blockField = new BlockField(height);
        operationWithKeys
                .forEach(key -> {
                    Field test = FieldFactory.createField(height);
                    Mino mino = key.getMino();
                    test.put(mino, key.getX(), key.getY());
                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                    blockField.merge(test, mino.getBlock());
                });
        return blockField;
    }

    private TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, BlockField blockField, String comment) {
        ColoredField coloredField = ColoredFieldFactory.createGrayField(initField);

        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target);
        }

        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
    }

    private void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }
}

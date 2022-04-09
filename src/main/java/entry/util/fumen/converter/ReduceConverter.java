package entry.util.fumen.converter;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operations;
import common.datastore.SimpleMinoOperation;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReduceConverter implements FumenConverter {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public ReduceConverter(MinoFactory minoFactory, ColorConverter colorConverter) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
    }

    @Override
    public List<String> parse(List<String> fumens) throws FinderParseException {
        List<String> outputs = new ArrayList<>();

        for (String fumen : fumens) {
            Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
            List<TetfuPage> pages = tetfu.decode(fumen);

            List<SimpleMinoOperation> operationList = new ArrayList<>();
            for (TetfuPage page : pages) {
                if (page.isPutMino()) {
                    Piece piece = colorConverter.parseToBlock(page.getColorType());
                    Mino mino = minoFactory.create(piece, page.getRotate());
                    operationList.add(new SimpleMinoOperation(mino, page.getX(), page.getY()));

                    if (page.isBlockUp() || page.isMirror()) {
                        break;
                    }
                }
            }

            int height = 24;

            TetfuPage headPage = pages.get(0);
            ColoredField coloredField = headPage.getField();
            Field field = toField(coloredField, height);

            List<MinoOperationWithKey> operationsWithKey = OperationTransform.parseToOperationWithKeys(
                    field, new Operations(operationList), minoFactory, height
            );

            BlockField blockField = new BlockField(height);
            for (MinoOperationWithKey operationWithKey : operationsWithKey) {
                Field minoField = operationWithKey.createMinoField(height);
                blockField.merge(minoField, operationWithKey.getPiece());
            }

            ColoredField freeze = coloredField.freeze();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < 10; x++) {
                    Piece block = blockField.getBlock(x, y);
                    if (block != null) {
                        freeze.setColorType(colorConverter.parseToColorType(block), x, y);
                    }
                }
            }

            String encode = tetfu.encode(Collections.singletonList(
                    new TetfuElement(freeze, headPage.getComment())
            ));

            outputs.add(String.format("v115@%s", encode));
        }

        return outputs;
    }

    private Field toField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        return field;
    }
}

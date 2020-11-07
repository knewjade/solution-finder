package entry.util.fumen;

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
import entry.EntryPoint;
import exceptions.FinderException;
import exceptions.FinderExecuteException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FumenUtilEntryPoint implements EntryPoint {
    private final FumenUtilSettings settings;

    public FumenUtilEntryPoint(FumenUtilSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() throws FinderException {
        FumenUtilModes mode = settings.getFumenUtilModes();
        switch (mode) {
            case Reduce: {
                MinoFactory minoFactory = new MinoFactory();
                ColorConverter colorConverter = new ColorConverter();
                for (String fumen : settings.getFumens()) {
                    Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                    List<TetfuPage> pages = tetfu.decode(fumen);

                    List<SimpleMinoOperation> operationList = pages.stream()
                            .filter(TetfuPage::isPutMino)
                            .map(page -> {
                                Piece piece = colorConverter.parseToBlock(page.getColorType());
                                Mino mino = minoFactory.create(piece, page.getRotate());
                                return new SimpleMinoOperation(mino, page.getX(), page.getY());
                            })
                            .collect(Collectors.toList());

                    int height = 24;

                    ColoredField coloredField = pages.get(0).getField();
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
                            new TetfuElement(freeze)
                    ));

                    output(String.format("v115@%s", encode));
                }
                break;
            }
            default:
                throw new FinderExecuteException("Unknown mode: " + mode);
        }
    }

    private Field toField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        return field;
    }

    @Override
    public void close() {
    }

    private void output() {
        output("");
    }

    private void output(String str) {
        System.out.println(str);
    }

    private static class Quiz {
        private static final Quiz EMPTY = new Quiz("", -1);

        private final String comment;
        private final int index;

        private Quiz(String comment, int index) {
            this.comment = comment;
            this.index = index;
        }
    }
}



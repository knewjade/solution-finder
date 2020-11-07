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
import entry.path.output.MyFile;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FumenUtilEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final FumenUtilSettings settings;
    private final BufferedWriter logWriter;

    public FumenUtilEntryPoint(FumenUtilSettings settings) throws FinderInitializeException {
        this.settings = settings;

        // ログファイルの出力先を整備
        String logFilePath = settings.getLogFilePath();
        MyFile logFile = new MyFile(logFilePath);

        logFile.mkdirs();
        logFile.verify();

        try {
            this.logWriter = logFile.newBufferedWriter();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
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

    public void output(String str) throws FinderExecuteException {
        try {
            logWriter.append(str).append(LINE_SEPARATOR);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws FinderExecuteException {
        try {
            logWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    @Override
    public void close() throws FinderTerminateException {
        try {
            flush();
            logWriter.close();
        } catch (IOException | FinderExecuteException e) {
            throw new FinderTerminateException(e);
        }
    }
}



package entry.move;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.datastore.order.Order;
import common.parser.OperationTransform;
import common.pattern.PatternGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import searcher.PutterNoHold;
import searcher.common.validator.PerfectValidator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final MoveSettings settings;
    private final BufferedWriter logWriter;

    public MoveEntryPoint(MoveSettings settings) throws FinderInitializeException {
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
        output("# Setup Field");

        // Setup field
        Field field = settings.getField();
        Verify.field(field);

        int maxClearLine = settings.getMaxClearLine();
        output(FieldView.toString(field, maxClearLine));

        // Setup max depth
        int maxDepth = Verify.maxDepth(field, maxClearLine);  // パフェに必要なミノ数

        output();

        // ========================================
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns);

        // Output patterns
        for (String pattern : patterns)
            output("  " + pattern);

        output();

        // ========================================

        // baseファイル
        MyFile base = new MyFile(settings.getOutputBaseFilePath());
        base.mkdirs();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = MinoRotation.create();
        ColorConverter colorConverter = new ColorConverter();
        PerfectValidator perfectValidator = new PerfectValidator();
        PutterNoHold<Action> putter = new PutterNoHold<>(minoFactory, perfectValidator);

        output("# Calculate");
        try (BufferedWriter bw = base.newBufferedWriter()) {
            List<Pieces> pieces = generator.blocksStream().collect(Collectors.toList());
            for (Pieces piece : pieces) {
                String using = piece.blockStream().map(Piece::getName).collect(Collectors.joining());
                output("   -> " + using);
                TreeSet<Order> first = putter.first(field, piece.getPieceArray(), new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine), maxClearLine, maxDepth);
                for (Order order : first) {
                    Stream<Operation> operationStream = order.getHistory().getOperationStream();
                    List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, new Operations(operationStream), minoFactory, maxClearLine);
                    BlockField blockField = OperationTransform.parseToBlockField(operationWithKeys, minoFactory, maxClearLine);

                    String encodeColor = encodeColor(field, minoFactory, colorConverter, blockField);
                    String encodeGray = encodeGray(order.getField(), minoFactory, colorConverter);

                    bw.write(String.format("%s,%s,%s", using, encodeColor, encodeGray));
                    bw.newLine();
                }
            }
            bw.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }

    private String encodeColor(Field initField, MinoFactory minoFactory, ColorConverter colorConverter, BlockField blockField) {
        TetfuElement tetfuElement = parseColorElement(initField, colorConverter, blockField, "");
        return encodeOnePage(minoFactory, colorConverter, tetfuElement);
    }

    private TetfuElement parseColorElement(Field initField, ColorConverter colorConverter, BlockField blockField, String comment) {
        ColoredField coloredField = ColoredFieldFactory.createGrayField(initField);

        for (Piece piece : Piece.values()) {
            Field target = blockField.get(piece);
            ColorType colorType = colorConverter.parseToColorType(piece);
            fillInField(coloredField, colorType, target);
        }

        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
    }

    private String encodeGray(Field field, MinoFactory minoFactory, ColorConverter colorConverter) {
        TetfuElement tetfuElement = parseGrayElement(field);
        return encodeOnePage(minoFactory, colorConverter, tetfuElement);
    }

    private TetfuElement parseGrayElement(Field field) {
        ColoredField coloredField = ColoredFieldFactory.createGrayField(field);
        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, "");
    }

    private void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }

    private String encodeOnePage(MinoFactory minoFactory, ColorConverter colorConverter, TetfuElement tetfuElement) {
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        List<TetfuElement> elementOnePage = Collections.singletonList(tetfuElement);
        return "v115@" + tetfu.encode(elementOnePage);
    }

    private void output() throws FinderExecuteException {
        output("");
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

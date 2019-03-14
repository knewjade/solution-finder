package entry.spin;

import common.datastore.PieceCounter;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import concurrent.LockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.MinoRotationDetail;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import entry.spin.output.FullSpinOutput;
import entry.spin.output.NoRoofSpinOutput;
import entry.spin.output.SpinOutput;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import searcher.spins.*;
import searcher.spins.candidates.Candidate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpinEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final SpinSettings settings;
    private final BufferedWriter logWriter;
    private final MyFile base;

    public SpinEntryPoint(SpinSettings settings) throws FinderInitializeException {
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

        {
            // 出力ファイルが正しく出力できるか確認
            String outputBaseFilePath = settings.getOutputBaseFilePath();
            String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

            // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
            if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
                namePath += "spin";

            // baseファイル
            String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
            MyFile base = new MyFile(outputFilePath);
            base.mkdirs();

            this.base = base;
        }
    }

    @Override
    public void run() throws FinderException {
        output("# Setup Field");

        // Setup field
        Field field = settings.getField();
        Verify.field(field);

        // Output field
        output(FieldView.toReducedString(field));

        output();

        // ========================================

        verifyTarget();

        // Output user-defined
        int fillBottom = settings.getFillBottom();
        int fillTop = settings.getFillTop();
        int marginHeight = settings.getMarginHeight();
        int fieldHeight = settings.getFieldHeight();
        int requiredClearLine = settings.getRequiredClearLine();
        boolean searchRoof = settings.getSearchRoof();
        int maxRoofNum = settings.setMaxRoofNum();

        output("# Initialize / User-defined");
        output("Fill: [" + fillBottom + "," + fillTop + ")");
        output("Margin height: " + marginHeight);
        output("Field height: " + fieldHeight);
        output("Required clear line: " + requiredClearLine);
        output("Search roof: " + (searchRoof ? "yes" : "no"));
        output("Max roof num: " + (maxRoofNum != Integer.MAX_VALUE ? maxRoofNum : "no limit"));

//        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
//        output("Drop: " + settings.getDropType().name().toLowerCase());

        output("Searching sequence:");

        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns);
        List<PieceCounter> pieceCounters = generator.blockCountersStream().collect(Collectors.toList());

        if (1 < pieceCounters.size())
            throw new FinderInitializeException("Should specify one combination");

        PieceCounter pieceCounter = pieceCounters.get(0);

        output("  " + pieceCounter.getBlocks().stream().map(Piece::getName).collect(Collectors.joining()));
        output();

        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = new MinoRotation();
        MinoRotationDetail minoRotationDetail = new MinoRotationDetail(minoFactory, minoRotation);
        MinoShifter minoShifter = new MinoShifter();
        ColorConverter colorConverter = new ColorConverter();
        OneFumenParser oneFumenParser = new OneFumenParser(minoFactory, colorConverter);
        RotateReachableThreadLocal rotateReachableThreadLocal = new RotateReachableThreadLocal(minoFactory, minoShifter, minoRotation, fieldHeight);

        Field initField = FieldFactory.createField(fieldHeight);
        initField.merge(settings.getField());

        FirstPreSpinRunner firstPreSpinRunner = new FirstPreSpinRunner(minoFactory, minoShifter, rotateReachableThreadLocal, fillBottom, fillTop, marginHeight, fieldHeight);
        SecondPreSpinRunner secondPreSpinRunner = new SecondPreSpinRunner(firstPreSpinRunner, initField, pieceCounter, maxRoofNum);
        SpinRunner spinRunner = searchRoof ? new FullSpinRunner() : new NoRoofSpinRunner();

        // ========================================

        output("# Search");
        output("  -> Stopwatch start");
        output("     ... searching");

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<Candidate> results = spinRunner.search(secondPreSpinRunner, requiredClearLine).collect(Collectors.toList());

        stopwatch.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output");

        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, fieldHeight);
        SpinOutput output;
        if (searchRoof) {
            output = new FullSpinOutput(oneFumenParser, minoFactory, minoRotationDetail, lockedReachableThreadLocal, rotateReachableThreadLocal);
        } else {
            output = new NoRoofSpinOutput(oneFumenParser, lockedReachableThreadLocal, rotateReachableThreadLocal);
        }
        output.output(base, results, initField, fieldHeight);
    }

    private void verifyTarget() throws FinderInitializeException {
        int fillBottom = settings.getFillBottom();
        int fillTop = settings.getFillTop();
        int marginHeight = settings.getMarginHeight();
        int fieldHeight = settings.getFieldHeight();
        int requiredClearLine = settings.getRequiredClearLine();

        if (fillBottom < 0 || 24 < fillBottom)
            throw new FinderInitializeException("Fill-bottom should be 0 <= y < 24: bottom=" + fillBottom);

        if (fillTop < 0 || 24 < fillTop)
            throw new FinderInitializeException("Fill-top should be 0 <= y < 24: top=" + fillTop);

        if (fillTop < fillBottom)
            throw new FinderInitializeException("Fill-top should be greater than or equal to fill-bottom: bottom=" + fillBottom + ", top=" + fillTop);

        if (marginHeight < 0 || 24 < marginHeight)
            throw new FinderInitializeException("Margin-height should be 0 <= y < 24: margin=" + marginHeight);

        if (marginHeight < fillTop + 2)
            throw new FinderInitializeException("Margin-height should be greater than or equal to (fill-top+2): top=" + fillTop + ", margin=" + marginHeight);

        if (fieldHeight < 0 || 24 < fieldHeight)
            throw new FinderInitializeException("Field-height should be 0 <= y < 24: field=" + fieldHeight);

        if (fieldHeight < marginHeight)
            throw new FinderInitializeException("Field-height should be greater than or equal to margin-height: margin=" + marginHeight + ", field=" + fieldHeight);

        if (requiredClearLine < 1 || 3 < requiredClearLine)
            throw new FinderInitializeException("Required-clear-line should be 1 <= line <= 3: line=" + requiredClearLine);
    }

    private static final String FILE_EXTENSION = ".html";

    private String getRemoveExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        return path.substring(0, pointIndex);
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

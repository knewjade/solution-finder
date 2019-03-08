package entry.spin;

import common.datastore.MinoOperationWithKey;
import common.datastore.PieceCounter;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.*;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import output.HTMLBuilder;
import output.HTMLColumn;
import searcher.spins.SpinRunner;
import searcher.spins.results.Result;
import searcher.spins.roof.results.RoofResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
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

        output("# Initialize / User-defined");
        output("Fill: [" + fillBottom + "," + fillTop + ")");
        output("Margin height: " + marginHeight);
        output("Field height: " + fieldHeight);
        output("Required clear line: " + requiredClearLine);

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

        SpinRunner spinRunner = new SpinRunner(minoFactory, minoShifter, fillBottom, fillTop, marginHeight, fieldHeight);

        // ========================================

        output("# Search");
        output("  -> Stopwatch start");
        output("     ... searching");

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // TODO: Tスピンに関係あるミノで抜くことができれば他の解で十分
        Field initField = FieldFactory.createField(fieldHeight);
        initField.merge(field);
        List<RoofResult> results = spinRunner.search(initField, pieceCounter, requiredClearLine).collect(Collectors.toList());

        stopwatch.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output");

        HTMLBuilder<K> htmlBuilder = new HTMLBuilder<>("Spin Result");
        htmlBuilder.addHeader("Spin Result");

        System.out.println(results.size());
        HashMap<Integer, K> keyMap = new HashMap<>();
        try (BufferedWriter writer = this.base.newBufferedWriter()) {
            for (RoofResult result : results) {
                Result lastResult = result.getLastResult();

                SimpleOriginalPiece operationT = result.getOperationT();
                Mino mino = operationT.getMino();
                Piece piece = mino.getPiece();
                int x = operationT.getX();
                int y = operationT.getY();

                ArrayList<SpinResult> spinResults = new ArrayList<>();
                for (RotateDirection rotateDirection : RotateDirection.values()) {
                    int[][] patternsFrom = minoRotationDetail.getPatternsFrom(mino, rotateDirection);
                    for (int[] pattern : patternsFrom) {
                        Rotate beforeRotate = mino.getRotate().get(rotateDirection);
                        Mino minoBefore = minoFactory.create(piece, beforeRotate);
                        SpinResult kicks = minoRotationDetail.getKicks(field, rotateDirection, minoBefore, x + pattern[0], y + pattern[1]);
                        if (kicks != SpinResult.NONE) {
                            spinResults.add(kicks);
                        }
                    }
                }

                int clearedLine = Long.bitCount(lastResult.getAllMergedFilledLine() & operationT.getUsingKey());
                K column = keyMap.computeIfAbsent(clearedLine, K::new);

                List<MinoOperationWithKey> operations = lastResult.operationStream().collect(Collectors.toList());
                String fumenData = oneFumenParser.parse(operations, field, fieldHeight);

                String name = operations.stream()
                        .map(operation -> String.format("%s-%s", operation.getPiece(), operation.getRotate()))
                        .collect(Collectors.joining(" "));
                String aLink = String.format("<div><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a></div>", fumenData, name);

                htmlBuilder.addColumn(column, aLink);
//                System.out.println(BlockFieldView.toString(result.getLastResult().parseToBlockField()));
            }

            ArrayList<K> sorted = new ArrayList<>(htmlBuilder.getRegisteredColumns());
            sorted.sort(Comparator.reverseOrder());

            List<String> strings = htmlBuilder.toList(sorted, true);
//
//            htmlBuilder.addHeader("header");

            for (String string : strings) {
                writer.write(string);
//                System.out.println(string);
            }
            writer.flush();
        } catch (Exception e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
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

    static class K implements HTMLColumn, Comparable<K> {
        private final int clearedLine;

        K(int clearedLine) {
            this.clearedLine = clearedLine;
        }

        @Override
        public String getTitle() {
            return clearedLine + " Lines";
        }

        @Override
        public String getId() {
            return clearedLine + "line";
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.empty();
        }

        @Override
        public int compareTo(K o) {
            return Integer.compare(this.clearedLine, o.clearedLine);
        }
    }
}

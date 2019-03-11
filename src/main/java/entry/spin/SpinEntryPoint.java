package entry.spin;

import common.buildup.BuildUp;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.PieceCounter;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import concurrent.LockedReachableThreadLocal;
import core.action.reachable.LockedReachable;
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
import searcher.spins.*;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;
import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpinNames;
import searcher.spins.spin.TSpins;

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

        Field initField = FieldFactory.createField(fieldHeight);
        initField.merge(settings.getField());

        FirstPreSpinRunner firstPreSpinRunner = new FirstPreSpinRunner(minoFactory, minoShifter, fillBottom, fillTop, marginHeight, fieldHeight);
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

        HTMLBuilder<K> htmlBuilder = new HTMLBuilder<>("Spin Result");
        htmlBuilder.addHeader(String.format("%d solutions", results.size()));

        System.out.println(results.size());
        HashMap<Spin, K> keyMap = new HashMap<>();
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, fieldHeight);
        try (BufferedWriter writer = this.base.newBufferedWriter()) {
            for (Candidate candidate : results) {
                Result lastResult = candidate.getResult();

                LockedReachable lockedReachable = lockedReachableThreadLocal.get();

                SimpleOriginalPiece operationT = candidate.getOperationT();

                Field fieldWithoutT = lastResult.getAllMergedField().freeze();
                fieldWithoutT.reduce(operationT.getMinoField());

                int clearedLineOnlyT = Long.bitCount(lastResult.getAllMergedFilledLine() & operationT.getUsingKey());

                Spin maxSpin = null;
                int priority = -1;
                // 左回転, 右回転
                if (searchRoof) {
                    for (RotateDirection direction : RotateDirection.values()) {
                        RotateDirection beforeDirection = RotateDirection.reverse(direction);

                        Piece piece = operationT.getPiece();
                        Rotate rotate = operationT.getRotate();
                        Mino before = minoFactory.create(piece, rotate.get(beforeDirection));
                        int[][] patterns2 = minoRotationDetail.getPatternsFrom(before, direction);

                        List<Spin> spins = get(minoRotationDetail, lockedReachable, fieldWithoutT, operationT, before, patterns2, direction, fieldHeight, clearedLineOnlyT);
                        for (Spin spin : spins) {
                            int p = getPriority(spin);
                            if (maxSpin == null || priority < p) {
                                maxSpin = spin;
                                priority = p;
                            }
                        }
                    }
                } else {
                    maxSpin = new Spin(TSpins.Regular, TSpinNames.NoName, clearedLineOnlyT);
                    priority = getPriority(maxSpin);
                }

                assert maxSpin != null;

                final int priority2 = priority;
                K column = keyMap.computeIfAbsent(maxSpin, spin -> new K(spin, priority2, getString(spin)));

                List<MinoOperationWithKey> operations = lastResult.operationStream().collect(Collectors.toList());
                String fumenData = oneFumenParser.parse(operations, field, fieldHeight);

                String name = operations.stream()
                        .map(operation -> String.format("%s-%s", operation.getPiece(), operation.getRotate()))
                        .collect(Collectors.joining(" "));

                Field allMergedField = lastResult.getAllMergedField();
                Field freeze = allMergedField.freeze();
                freeze.clearLine();
                int numOfHoles = getNumOfHoles(freeze);
                int numOfPieces = operations.size();

                int p1 = numOfHoles * 100 * 100 * 100
                        + (Long.bitCount(allMergedField.getFilledLine()) - clearedLineOnlyT) * 100 * 100
                        + operationT.getY() * 100
                        + numOfPieces;

                boolean b = BuildUp.existsValidBuildPattern(initField, operations.stream().filter(op -> !operationT.equals(op)), fieldHeight, lockedReachable);
                String aLink = String.format("<div><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a> %s %d</div>", fumenData, name, b, p1);

                htmlBuilder.addColumn(column, aLink, p1);

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

    private List<Spin> get(MinoRotationDetail minoRotationDetail, LockedReachable lockedReachable, Field fieldWithoutT, Operation operation, Mino before, int[][] patterns, RotateDirection direction, int maxHeight, int clearedLine) {
        List<Spin> spins = new ArrayList<>();

        for (int[] pattern : patterns) {
            // 開店前の位置に移動
            int beforeX = operation.getX() - pattern[0];
            int beforeY = operation.getY() - pattern[1];

            if (beforeX + before.getMinX() < 0 || 10 <= beforeX + before.getMaxX()) {
                continue;
            }

            if (beforeY + before.getMinY() < 0) {
                continue;
            }

            if (!fieldWithoutT.canPut(before, beforeX, beforeY)) {
                continue;
            }

            SpinResult spinResult = minoRotationDetail.getKicks(fieldWithoutT, direction, before, beforeX, beforeY);

            if (spinResult == SpinResult.NONE) {
                continue;
            }

            // 回転後に元の場所に戻る
            if (spinResult.getToX() != operation.getX() || spinResult.getToY() != operation.getY()) {
                continue;
            }

            // 回転前の位置に移動できる
            if (!lockedReachable.checks(fieldWithoutT, before, beforeX, beforeY, maxHeight)) {
                continue;
            }

            Spin spin = SpinCommons.getSpins(fieldWithoutT, spinResult, clearedLine);
            spins.add(spin);
        }

        return spins;
    }

    private int getPriority(Spin spin) {
        int clearedLine = spin.getClearedLine();

        switch (spin.getSpin()) {
            case Mini: {
                return clearedLine * 10 + 1;
            }
            case Regular: {
                switch (spin.getName()) {
                    case Iso: {
                        return clearedLine * 10 + 2;
                    }
                    case Fin: {
                        return clearedLine * 10 + 3;
                    }
                    case Neo: {
                        return clearedLine * 10 + 4;
                    }
                    case NoName: {
                        return clearedLine * 10 + 5;
                    }
                }
            }
        }

        throw new IllegalStateException();
    }

    private String getString(Spin spin) {
        int clearedLine = spin.getClearedLine();
        String lineString = getLineString(clearedLine);
        switch (spin.getSpin()) {
            case Mini: {
                return lineString + " [Mini]";
            }
            case Regular: {
                return lineString + " [" + spin.getName().getName() + "]";
            }
        }

        throw new IllegalStateException();
    }

    private String getLineString(int clearedLine) {
        assert 1 <= clearedLine && clearedLine <= 3;
        switch (clearedLine) {
            case 1:
                return "Single";
            case 2:
                return "Double";
            case 3:
                return "Triple";
        }
        throw new IllegalStateException();
    }

    private int getNumOfHoles(Field field) {
        Field freeze = field.freeze();
        freeze.slideDown();
        freeze.reduce(field);
        return freeze.getNumOfAllBlocks();
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
        private final Spin spin;
        private final int priority;
        private final String title;

        K(Spin spin, int priority, String title) {
            this.spin = spin;
            this.title = title;
            this.priority = priority;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getId() {
            return title.toLowerCase().replace(' ', '-');
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.empty();
        }

        @Override
        public int compareTo(K o) {
            return Integer.compare(this.priority, o.priority);
        }
    }
}

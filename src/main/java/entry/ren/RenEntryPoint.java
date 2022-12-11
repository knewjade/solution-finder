package entry.ren;

import common.datastore.Operations;
import common.datastore.RenResult;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import core.FinderConstant;
import core.action.candidate.Candidate;
import core.action.candidate.CandidateFacade;
import core.action.candidate.HarddropCandidate;
import core.action.candidate.SoftdropTOnlyCandidate;
import core.field.Field;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import entry.path.output.SequenceFumenParser;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import output.HTMLBuilder;
import output.HTMLColumn;
import searcher.ren.RenNoHold;
import searcher.ren.RenSearcher;
import searcher.ren.RenUsingHold;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RenEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final RenSettings settings;
    private final BufferedWriter logWriter;
    private final MyFile base;

    public RenEntryPoint(RenSettings settings) throws FinderInitializeException {
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
                namePath += "ren";

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

        // Output user-defined
        output("# Initialize / User-defined");
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Kicks: " + settings.getKicksName().toLowerCase());
        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Version: " + FinderConstant.VERSION);

        output("Searching sequence:");

        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns);
        List<Pieces> piecesList = generator.blocksStream().collect(Collectors.toList());

        if (1 < piecesList.size())
            throw new FinderInitializeException("Should specify one sequence, not allow pattern(*) for multi sequences");

        Pieces pieces = piecesList.get(0);
        List<Piece> pieceList = pieces.getPieces();

        output("  " + pieceList.stream().map(Piece::getName).collect(Collectors.joining()));
        output();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = settings.createMinoRotationSupplier().get();

        RenSearcher<Action> renSearcher = getRenSearcher(minoFactory);
        Candidate<Action> candidate = getCandidate(minoFactory, minoShifter, minoRotation);

        // ========================================

        output("# Search");
        output("  -> Stopwatch start");
        output("     ... searching");

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<RenResult> results = renSearcher.check(field, pieceList, candidate, pieceList.size());

        stopwatch.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output");

        // HTMLを出力
        {
            HTMLBuilder<HTMLColumn> htmlBuilder = new HTMLBuilder<>("Ren Result");
            htmlBuilder.addHeader(String.format("%d solutions", results.size()));

            ColorConverter colorConverter = new ColorConverter();
            boolean use180Rotation = settings.getDropType().uses180Rotation();
            SequenceFumenParser fumenParser = new SequenceFumenParser(minoFactory, minoRotation, colorConverter, use180Rotation);

            HashSet<Integer> renKeys = new HashSet<>();
            for (RenResult result : results) {
                Operations operations = new Operations(result.getRenOrder().getHistory().getOperationStream());

                String fumenData = fumenParser.parse(operations, field, 24);

                String name = operations.getOperations().stream()
                        .map(operation -> String.format("%s-%s", operation.getPiece(), operation.getRotate()))
                        .collect(Collectors.joining(" "));
                String aLink = String.format("<div><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a></div>", fumenData, name);

                int renCount = result.getRenCount();
                htmlBuilder.addColumn(new RenHTMLColumn(renCount), aLink);

                renKeys.add(renCount);
            }

            // HTMLのキー一覧を取得
            ArrayList<Integer> renKeyList = new ArrayList<>(renKeys);
            Collections.reverse(renKeyList);
            List<HTMLColumn> allColumns = renKeyList.stream().map(RenHTMLColumn::new).collect(Collectors.toList());

            if (allColumns.isEmpty())
                output("  Found solutions = 0");
            else
                output(String.format("  Found solutions = %d (Max %d Ren)", results.size(), renKeyList.get(0)));

            // 出力
            try (BufferedWriter writer = this.base.newBufferedWriter()) {
                for (String line : htmlBuilder.toList(allColumns, true))
                    writer.write(line);
                writer.flush();
            } catch (Exception e) {
                throw new FinderExecuteException("Failed to output file", e);
            }
        }
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

    private RenSearcher<Action> getRenSearcher(MinoFactory minoFactory) {
        if (settings.isUsingHold()) {
            return new RenUsingHold<>(minoFactory);
        } else {
            return new RenNoHold<>(minoFactory);
        }
    }

    private Candidate<Action> getCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation) throws FinderInitializeException {
        DropType dropType = settings.getDropType();
        boolean use180Rotation = dropType.uses180Rotation();

        switch (dropType) {
            case Harddrop:
                return new HarddropCandidate(minoFactory, minoShifter);
            case Softdrop:
            case Softdrop180:
                return CandidateFacade.createLocked(minoFactory, minoShifter, minoRotation, 24, use180Rotation);
            case SoftdropTOnly:
                return new SoftdropTOnlyCandidate(minoFactory, minoShifter, minoRotation, 24, use180Rotation);
            default:
                throw new FinderInitializeException("Unsupport droptype: droptype=" + dropType);
        }
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

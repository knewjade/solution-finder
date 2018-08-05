package entry.ren;

import common.datastore.MinoOperationWithKey;
import common.datastore.Operations;
import common.datastore.RenResult;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.parser.OperationTransform;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import core.action.candidate.Candidate;
import core.action.candidate.HarddropCandidate;
import core.action.candidate.LockedCandidate;
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
import searcher.ren.RenNoHold;
import searcher.ren.RenSearcher;
import searcher.ren.RenUsingHold;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RenEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final RenSettings settings;
    private final BufferedWriter logWriter;

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
    }

    @Override
    public void run() throws FinderException {
        output("# Setup Field");

        // Setup field
        Field field = settings.getField();
        Verify.field(field);

        // Output field
        output(FieldView.toString(field));

        output();

        // ========================================

        // Output user-defined
        output("# Initialize / User-defined");
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Drop: " + settings.getDropType().name().toLowerCase());

        output("Searching sequence:");

        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns);
        List<Pieces> piecesList = generator.blocksStream().collect(Collectors.toList());

        if (1 < piecesList.size())
            throw new FinderInitializeException("Should specify one sequence, not allow pattern(*) for multi sequences");

        Pieces pieces = piecesList.get(0);
        List<Piece> pieceList = pieces.getPieces();
        output("  " + pieceList.stream().map(Piece::getName).collect(Collectors.joining()));

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();

        RenSearcher<Action> renSearcher = getRenSearcher(minoFactory);
        Candidate<Action> candidate = getCandidate(minoFactory, minoShifter, minoRotation);
        List<RenResult> results = renSearcher.check(field, pieceList, candidate, pieceList.size());

        // ========================================

        output("# Output");

        System.out.println(results.size());
        results.sort(Comparator.comparingInt(RenResult::getRenCount).reversed());

        ColorConverter colorConverter = new ColorConverter();
        for (RenResult result : results) {
            SequenceFumenParser fumenParser = new SequenceFumenParser(minoFactory, colorConverter);

            Operations operations = new Operations(result.getRenOrder().getHistory().getOperationStream());
            List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, 24);
            System.out.println(operationWithKeys);

            String parse = fumenParser.parse(operationWithKeys, field, 24);
            System.out.println("http://fumen.zui.jp/?v115@" + parse);
        }
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
        switch (dropType) {
            case Softdrop:
                return new LockedCandidate(minoFactory, minoShifter, minoRotation, 24);
            case Harddrop:
                return new HarddropCandidate(minoFactory, minoShifter);
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

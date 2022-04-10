package entry.util.fumen;

import common.parser.StringEnumTransform;
import common.tetfu.common.ColorConverter;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.EntryPoint;
import entry.path.output.MyFile;
import entry.util.fumen.converter.FilterPieceConverter;
import entry.util.fumen.converter.FumenConverter;
import entry.util.fumen.converter.ReduceConverter;
import entry.util.fumen.converter.RemoveCommentConverter;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;

import java.io.BufferedWriter;
import java.io.IOException;

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
        FumenConverter converter = createFumenConverter(settings);
        for (String fumen : settings.getFumens()) {
            output(converter.parse(fumen));
        }
    }

    private FumenConverter createFumenConverter(FumenUtilSettings settings) throws FinderExecuteException {
        FumenUtilModes mode = settings.getFumenUtilModes();
        switch (mode) {
            case Reduce:
                return new ReduceConverter(new MinoFactory(), new ColorConverter());
            case RemoveComment:
                return new RemoveCommentConverter(new MinoFactory(), new ColorConverter());
            case Filter:
                return createFilterConverter(settings);
            default:
                throw new FinderExecuteException("Unknown mode: " + mode);
        }
    }

    private FumenConverter createFilterConverter(FumenUtilSettings settings) throws FinderExecuteException {
        String filter = settings.getFilter();
        assert filter != null;
        Piece piece;
        try {
            piece = StringEnumTransform.toPiece(filter);
        } catch (IllegalArgumentException e) {
            throw new FinderExecuteException("Unsupported filtered piece: filter=" + filter);
        }
        return new FilterPieceConverter(new MinoFactory(), new ColorConverter(), piece);
    }

    private void output(String str) throws FinderExecuteException {
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



package entry.util.fumen;

import common.tetfu.common.ColorConverter;
import core.mino.MinoFactory;
import entry.EntryPoint;
import entry.path.output.MyFile;
import entry.util.fumen.converter.FumenConverter;
import entry.util.fumen.converter.ReduceConverter;
import entry.util.fumen.converter.RemoveCommentConverter;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

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
        FumenConverter converter = createFumenConverter(mode);
        List<String> parsed = converter.parse(settings.getFumens());
        for (String fumen : parsed) {
            output(fumen);
        }
    }

    private FumenConverter createFumenConverter(FumenUtilModes mode) throws FinderExecuteException {
        switch (mode) {
            case Reduce:
                return new ReduceConverter(new MinoFactory(), new ColorConverter());
            case RemoveComment:
                return new RemoveCommentConverter(new MinoFactory(), new ColorConverter());
            default:
                throw new FinderExecuteException("Unknown mode: " + mode);
        }
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



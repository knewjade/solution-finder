package entry.util.fumen;

import exceptions.FinderParseException;

import java.util.Collections;
import java.util.List;

// TODO: unittest: write
public class FumenUtilSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/cover.csv";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private List<String> fumens = Collections.emptyList();
    private FumenUtilModes mode = null;

    // ********* Getter ************
    String getLogFilePath() {
        return logFilePath;
    }

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    FumenUtilModes getFumenUtilModes() {
        return mode;
    }

    List<String> getFumens() {
        return fumens;
    }

    // ********* Setter ************
    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setFumenUtilModes(String mode) throws FinderParseException {
        assert mode != null;
        switch (mode.trim().toLowerCase()) {
            case "reduce":
                this.mode = FumenUtilModes.Reduce;
                return;
            case "remove-comment":
                this.mode = FumenUtilModes.RemoveComment;
                return;
            default:
                throw new FinderParseException("Unsupported mode: mode=" + mode);
        }
    }

    void setFumens(List<String> fumens) {
        this.fumens = fumens;
    }

    boolean isOutputToConsole() {
        return true;
    }
}

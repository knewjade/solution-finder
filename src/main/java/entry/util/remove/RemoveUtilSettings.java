package entry.util.remove;

import java.util.Collections;
import java.util.List;

// TODO: unittest: write
public class RemoveUtilSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/cover.csv";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private List<String> fumens = Collections.emptyList();

    // ********* Getter ************
    String getLogFilePath() {
        return logFilePath;
    }

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    List<String> getFumens() {
        return fumens;
    }

    boolean isOutputToConsole() {
        return true;
    }

    // ********* Setter ************
    void setFumens(List<String> fumens) {
        this.fumens = fumens;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

}

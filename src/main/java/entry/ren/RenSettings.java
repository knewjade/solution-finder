package entry.ren;

import core.field.Field;
import core.field.FieldFactory;
import entry.DropType;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.List;

public class RenSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/move.csv";

    private boolean isUsingHold = true;
    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private Field field = FieldFactory.createField(4);
    private List<String> patterns = new ArrayList<>();
    private DropType dropType = DropType.Softdrop;

    // ********* Getter ************
    public boolean isUsingHold() {
        return isUsingHold;
    }

    Field getField() {
        return field;
    }

    List<String> getPatterns() {
        return patterns;
    }

    String getLogFilePath() {
        return logFilePath;
    }

    public String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    boolean isOutputToConsole() {
        return true;
    }

    DropType getDropType() {
        return dropType;
    }

    // ********* Setter ************
    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setField(Field field) {
        this.field = field;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setDropType(String type) throws FinderParseException {
        switch (type.trim().toLowerCase()) {
            case "soft":
            case "softdrop":
                this.dropType = DropType.Softdrop;
                return;
            case "hard":
            case "harddrop":
                this.dropType = DropType.Harddrop;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }
}

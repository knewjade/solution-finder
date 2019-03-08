package entry.spin;

import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;

import java.util.ArrayList;
import java.util.List;

public class SpinSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/spin.html";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private Field field = FieldFactory.createField(24);
    private List<String> patterns = new ArrayList<>();

    private int fillBottom = 0;
    private int fillTop = 4;
    private int marginHeight = -1;
    private int fieldHeight = -1;
    private int requiredClearLine = 2;

    // ********* Getter ************
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

    public int getFillBottom() {
        return fillBottom;
    }

    public int getFillTop() {
        return fillTop;
    }

    public int getMarginHeight() {
        if (marginHeight == -1)
            return getFillTop() + 4;
        return marginHeight;
    }

    public int getFieldHeight() {
        if (fieldHeight == -1) {
            int maxUsingHeight = getMaxUsingHeight(getField());
            int maxTargetHeight = getMarginHeight();
            return maxTargetHeight < maxUsingHeight ? maxUsingHeight : maxTargetHeight;
        }
        return marginHeight;
    }

    private int getMaxUsingHeight(Field field) {
        long usingKey = field.getUsingKey();
        for (int y = field.getMaxFieldHeight() - 1; 0 <= y; y--) {
            if ((usingKey & KeyOperators.getBitKey(y)) != 0L) {
                return y;
            }
        }
        return -1;
    }

    public int getRequiredClearLine() {
        return requiredClearLine;
    }

    // ********* Setter ************
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

    void setFillBottom(int allowFillMinY) {
        this.fillBottom = allowFillMinY;
    }

    void setFillTop(int allowFillMaxHeight) {
        this.fillTop = allowFillMaxHeight;
    }

    void setMarginHeight(int maxTargetHeight) {
        this.marginHeight = maxTargetHeight;
    }

    void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    void setRequiredClearLine(int requiredClearLine) {
        this.requiredClearLine = requiredClearLine;
    }

    /**
     private boolean isUsingHold = true;
     private DropType dropType = DropType.Softdrop;

     // ********* Getter ************
     public boolean isUsingHold() {
     return isUsingHold;
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
     */
}

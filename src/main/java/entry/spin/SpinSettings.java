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
    private int fillTop = -1;
    private int marginHeight = -1;
    private int fieldHeight = -1;
    private int requiredClearLine = 2;
    private boolean searchRoof = true;
    private int maxRoofNum = -1;
    private boolean isSplit = false;
    private FilterType filter = FilterType.Strict;
    private OutputType outputType = OutputType.HTML;

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

    int getFillBottom() {
        return fillBottom;
    }

    int getFillTop() {
        if (fillTop < 0)
            return getMaxUsingHeight(getField()) + 1;
        return fillTop;
    }

    int getMarginHeight() {
        if (marginHeight < 0)
            return getFillTop() + 2;
        return marginHeight;
    }

    int getFieldHeight() {
        if (fieldHeight < 0) {
            int maxUsingHeight = getMaxUsingHeight(getField());
            int maxTargetHeight = getMarginHeight();
            return Math.max(maxTargetHeight, maxUsingHeight);
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

    int getRequiredClearLine() {
        return requiredClearLine;
    }

    boolean getSearchRoof() {
        return searchRoof;
    }

    int setMaxRoofNum() {
        if (maxRoofNum < 0) {
            return Integer.MAX_VALUE;
        }
        return maxRoofNum;
    }

    boolean isTetfuSplit() {
        return isSplit;
    }

    FilterType getFilterMode() {
        return filter;
    }

    OutputType getOutputType() {
        return outputType;
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

    void setTetfuSplit(boolean isSplit) {
        this.isSplit = isSplit;
    }

    void setRequiredClearLine(int requiredClearLine) {
        this.requiredClearLine = requiredClearLine;
    }

    void setSearchRoof(boolean searchRoof) {
        this.searchRoof = searchRoof;
    }

    void setMaxRoofNum(int maxRoofNum) {
        this.maxRoofNum = maxRoofNum;
    }

    void setFilterMode(String mode) {
        switch (mode.trim().toLowerCase()) {
            case "strict":
                filter = FilterType.Strict;
                break;
            case "ignore-t":
            case "ignore_t":
                filter = FilterType.IgnoreT;
                break;
            case "none":
                filter = FilterType.None;
                break;
            default:
                throw new IllegalArgumentException("Unsupported filter mode: " + mode);
        }
    }

    public void setOutputType(String type) {
        switch (type.trim().toLowerCase()) {
            case "html":
                outputType = OutputType.HTML;
                break;
            case "csv":
                outputType = OutputType.CSV;
                break;
            default:
                throw new IllegalArgumentException("Unsupported output type: " + type);
        }
    }
}

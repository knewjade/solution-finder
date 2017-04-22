package entry.percent;

import core.field.Field;
import core.field.FieldFactory;
import tetfu.common.ColorType;
import tetfu.field.ColoredField;

import java.util.ArrayList;
import java.util.List;

public class PercentSettings {
    private static final int EMPTY_BLOCK_NUMBER = ColorType.Empty.getNumber();
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";

    private boolean isUsingHold = true;
    private int maxClearLine = -1;
    private Field field = null;
    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private List<String> patterns = new ArrayList<>();

    // ********* Getter ************
    public boolean isUsingHold() {
        return isUsingHold;
    }

    boolean isOutputToConsole() {
        return true;
    }

    Field getField() {
        return field;
    }

    int getMaxClearLine() {
        return maxClearLine;
    }

    String getLogFilePath() {
        return logFilePath;
    }

    List<String> getPatterns() {
        return patterns;
    }

    // ********* Setter ************
    public void setMaxClearLine(int maxClearLine) {
        this.maxClearLine = maxClearLine;
    }

    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getBlockNumber(x, y) != EMPTY_BLOCK_NUMBER)
                    field.setBlock(x, y);
        setFieldFilePath(field);
    }

    void setFieldFilePath(Field field) {
        this.field = field;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}

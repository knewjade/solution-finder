package entry.move;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;

import java.util.ArrayList;
import java.util.List;

public class MoveSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/move.csv";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private int maxClearLine = 4;
    private Field field = FieldFactory.createField(4);
    private List<String> patterns = new ArrayList<>();

    // ********* Getter ************
    Field getField() {
        return field;
    }

    int getMaxClearLine() {
        return maxClearLine;
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

    // ********* Setter ************
    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setField(ColoredField coloredField) {
        int height = coloredField.getUsingHeight();
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        setField(field);
        setMaxClearLine(height);
    }

    private void setField(Field field) {
        this.field = field;
    }

    void setMaxClearLine(int maxClearLine) {
        this.maxClearLine = maxClearLine;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    boolean isOutputToConsole() {
        return true;
    }
}

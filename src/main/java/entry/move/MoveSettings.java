package entry.move;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.srs.MinoRotation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MoveSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/move.csv";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private int maxClearLine = 4;
    private ColoredField coloredField = null;
    private List<String> patterns = new ArrayList<>();
    private boolean showsColoredField = false;

    // ********* Getter ************
    Field getField() {
        if (coloredField == null) {
            return FieldFactory.createField(4);
        }
        return parseToField(coloredField);
    }

    public ColoredField getColoredField() {
        return coloredField;
    }

    private Field parseToField(ColoredField coloredField) {
        int usingHeight = coloredField.getUsingHeight();
        int height = Math.max(maxClearLine, usingHeight);
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
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

    public boolean isShowsColoredField() {
        return showsColoredField;
    }

    boolean isOutputToConsole() {
        return true;
    }

    Supplier<MinoRotation> createMinoRotationSupplier() {
        return MinoRotation::create;
    }

    // ********* Setter ************
    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setColoredField(ColoredField coloredField) {
        this.coloredField = coloredField;
    }

    void setMaxClearLine(int maxClearLine) {
        this.maxClearLine = maxClearLine;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public void setShowsColoredField(boolean showsColoredField) {
        this.showsColoredField = showsColoredField;
    }
}

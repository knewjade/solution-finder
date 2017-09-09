package entry.path;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.List;

public class PathSettings {
    private static final int EMPTY_BLOCK_NUMBER = ColorType.Empty.getNumber();
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/path.txt";

    private boolean isUsingHold = true;
    private int maxClearLine = 4;
    private Field field = null;
    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private List<String> patterns = new ArrayList<>();
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private PathLayer pathLayer = PathLayer.Minimal;
    private OutputType outputType = OutputType.Link;
    private boolean isSplit = false;
    private int cachedMinBit = 0;

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

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    PathLayer getPathLayer() {
        return pathLayer;
    }

    OutputType getOutputType() {
        return outputType;
    }

    boolean isTetfuSplit() {
        return isSplit;
    }

    int getCachedMinBit() {
        return cachedMinBit;
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

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setPathLayer(PathLayer pathLayer) {
        this.pathLayer = pathLayer;
    }

    void setOutputType(String type) throws UnsupportedDataTypeException {
        switch (type.trim().toLowerCase()) {
            case "csv":
                this.outputType = OutputType.CSV;
                break;
            case "link":
                this.outputType = OutputType.Link;
                break;
            default:
                throw new UnsupportedDataTypeException("Unsupported format: format=" + type);
        }
    }

    void setTetfuSplit(boolean isSplit) {
        this.isSplit = isSplit;
    }

    void setCachedMinBit(int minBit) {
        this.cachedMinBit = minBit;
    }
}

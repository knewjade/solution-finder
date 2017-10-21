package entry.path;

import common.datastore.BlockField;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import entry.DropType;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.List;

public class PathSettings {
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
    private BlockField reservedBlock = null;
    private boolean isReserved = false;
    private DropType dropType = DropType.Softdrop;

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

    public String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    public PathLayer getPathLayer() {
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

    BlockField getReservedBlock() {
        return reservedBlock;
    }

    boolean isReserved() {
        return isReserved;
    }

    DropType getDropType() {
        return dropType;
    }

    // ********* Setter ************
    void setMaxClearLine(int maxClearLine) {
        this.maxClearLine = maxClearLine;
    }

    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        setField(field);
        setReservedBlock(null);
    }

    void setFieldWithReserved(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        BlockField blockField = new BlockField(height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                ColorConverter colorConverter = new ColorConverter();
                ColorType colorType = colorConverter.parseToColorType(coloredField.getBlockNumber(x, y));

                switch (colorType) {
                    case Empty:
                        break;
                    case Gray:
                        field.setBlock(x, y);
                        break;
                    default:
                        Block block = colorConverter.parseToBlock(colorType);
                        blockField.setBlock(block, x, y);
                        break;
                }
            }
        }
        setField(field);
        setReservedBlock(blockField);
    }

    private void setField(Field field) {
        this.field = field;
    }

    private void setReservedBlock(BlockField blockField) {
        this.reservedBlock = blockField;
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

    void setOutputType(String type, String key) throws UnsupportedDataTypeException {
        switch (type.trim().toLowerCase()) {
            case "csv":
                switch (key.trim().toLowerCase()) {
                    case "none":
                    case "n":
                        this.outputType = OutputType.CSV;
                        return;
                    case "solution":
                    case "s":
                        this.outputType = OutputType.TetfuCSV;
                        return;
                    case "pattern":
                    case "p":
                        this.outputType = OutputType.PatternCSV;
                        return;
                    case "use":
                    case "u":
                        this.outputType = OutputType.UseCSV;
                        return;
                    default:
                        throw new UnsupportedDataTypeException("Unsupported CSV key: key=" + key);
                }
            case "link":
                this.outputType = OutputType.Link;
                return;
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

    void setReserved(Boolean isReversed) {
        this.isReserved = isReversed;
    }

    void setDropType(String type) throws UnsupportedDataTypeException {
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
                throw new UnsupportedDataTypeException("Unsupported droptype: type=" + type);
        }
    }
}

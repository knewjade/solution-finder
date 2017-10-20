package entry.setup;

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

public class SetupSettings {
    private static final int EMPTY_BLOCK_NUMBER = ColorType.Empty.getNumber();
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";

    private boolean isUsingHold = true;
    private int maxClearLine = 4;
    private Field field = null;
    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private List<String> patterns = new ArrayList<>();
    private DropType dropType = DropType.Softdrop;
    private BlockField reservedBlock = null;
    private boolean isReversed = false;

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

    DropType getDropType() {
        return dropType;
    }

    BlockField getReservedBlock() {
        return reservedBlock;
    }

    boolean isRevered() {
        return isReversed;
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

                if (coloredField.getBlockNumber(x, y) == EMPTY_BLOCK_NUMBER)
                    continue;

                switch (colorType) {
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

    void setFieldFilePath(Field field) {
        this.field = field;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setDropType(DropType dropType) {
        this.dropType = dropType;
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

package entry.setup;

import common.datastore.BlockField;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.mino.Block;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.List;

public class SetupSettings {
    private static final int EMPTY_BLOCK_NUMBER = ColorType.Empty.getNumber();
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/path.txt";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private boolean isReserved = false;
    private int maxClearLine = 4;
    private List<String> patterns = new ArrayList<>();
    private Field needFilledField = null;
    private Field notFilledField = null;
    private BlockField reservedBlock = null;
    private ColorType marginColorType = null;

    // ********* Getter ************

    String getLogFilePath() {
        return logFilePath;
    }

    boolean isReserved() {
        return isReserved;
    }

    int getMaxClearLine() {
        return maxClearLine;
    }

    List<String> getPatterns() {
        return patterns;
    }

    boolean isOutputToConsole() {
        return true;
    }

    Field getNeedFilledField() {
        return needFilledField;
    }

    Field getNotFilledField() {
        return notFilledField;
    }

    BlockField getReservedBlock() {
        return reservedBlock;
    }

    ColorType getMarginColorType() {
        return marginColorType;
    }

    // ********* Setter ************
    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
//        this.outputBaseFilePath = path;
    }

    void setReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    void setMaxClearLine(int maxClearLine) {
        this.maxClearLine = maxClearLine;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setFieldWithReserved(Field needFilledField, Field notFilledField, ColoredField coloredField, int maxClearLine) {
        BlockField blockField = new BlockField(maxClearLine);
        for (int y = 0; y < maxClearLine; y++) {
            for (int x = 0; x < 10; x++) {
                ColorConverter colorConverter = new ColorConverter();
                ColorType colorType = colorConverter.parseToColorType(coloredField.getBlockNumber(x, y));
                switch (colorType) {
                    case Gray:
                    case Empty:
                        break;
                    default:
                        Block block = colorConverter.parseToBlock(colorType);
                        blockField.setBlock(block, x, y);
                        break;
                }
            }
        }

        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setReservedBlock(blockField);
    }

    void setField(Field needFilledField, Field notFilledField) {
        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setReservedBlock(null);
    }

    private void setNeedFilledField(Field needFilledField) {
        this.needFilledField = needFilledField;
    }

    private void setNotFilledField(Field notFilledField) {
        this.notFilledField = notFilledField;
    }

    private void setReservedBlock(BlockField reservedBlock) {
        this.reservedBlock = reservedBlock;
    }

    // The Tetris Company standardization
    void setMarginColorType(String marginColor) throws UnsupportedDataTypeException {
        switch (marginColor.trim().toLowerCase()) {
            case "i":
            case "cyan":
            case "cy":
                this.marginColorType = ColorType.I;
                break;
            case "j":
            case "blue":
            case "bl":
                this.marginColorType = ColorType.J;
                break;
            case "l":
            case "orange":
            case "or":
                this.marginColorType = ColorType.L;
                break;
            case "o":
            case "yellow":
            case "ye":
                this.marginColorType = ColorType.O;
                break;
            case "s":
            case "green":
            case "gr":
                this.marginColorType = ColorType.S;
                break;
            case "t":
            case "purple":
            case "pu":
                this.marginColorType = ColorType.T;
                break;
            case "z":
            case "red":
            case "re":
                this.marginColorType = ColorType.Z;
                break;
            default:
                throw new UnsupportedDataTypeException("Unsupported margin color: value=" + marginColor);
        }
    }
}

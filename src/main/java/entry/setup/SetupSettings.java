package entry.setup;

import common.datastore.BlockField;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.mino.Block;
import entry.DropType;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.List;

public class SetupSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/setup.html";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private boolean isReserved = false;
    private int maxHeight = -1;
    private List<String> patterns = new ArrayList<>();
    private Field initField = null;
    private Field needFilledField = null;
    private Field notFilledField = null;
    private BlockField reservedBlock = null;
    private ColorType marginColorType = null;
    private ColorType fillColorType = null;
    private DropType dropType = DropType.Softdrop;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;

    // ********* Getter ************

    String getLogFilePath() {
        return logFilePath;
    }

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    boolean isReserved() {
        return isReserved;
    }

    int getMaxHeight() {
        return maxHeight;
    }

    List<String> getPatterns() {
        return patterns;
    }

    boolean isOutputToConsole() {
        return true;
    }

    Field getInitField() {
        return initField;
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

    ColorType getFillColorType() {
        return fillColorType;
    }

    DropType getDropType() {
        return dropType;
    }

    // ********* Setter ************
    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    private void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setFieldWithReserved(Field initField, Field needFilledField, Field notFilledField, ColoredField coloredField, int maxHeight) {
        BlockField blockField = new BlockField(maxHeight);
        for (int y = 0; y < maxHeight; y++) {
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

        setMaxHeight(maxHeight);
        setInitField(initField);
        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setReservedBlock(blockField);
    }

    void setField(Field initField, Field needFilledField, Field notFilledField, int maxHeight) {
        setMaxHeight(maxHeight);
        setInitField(initField);
        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setReservedBlock(null);
    }

    private void setInitField(Field field) {
        this.initField = field;
    }

    private void setNeedFilledField(Field field) {
        this.needFilledField = field;
    }

    private void setNotFilledField(Field field) {
        this.notFilledField = field;
    }

    private void setReservedBlock(BlockField reservedBlock) {
        this.reservedBlock = reservedBlock;
    }

    void setMarginColorType(String marginColor) throws UnsupportedDataTypeException {
        ColorType colorType = parseToColor(marginColor);
        if (colorType == null)
            throw new UnsupportedDataTypeException("Unsupported margin color: value=" + marginColor);
        this.marginColorType = colorType;
    }

    // The Tetris Company standardization
    private ColorType parseToColor(String color) {
        switch (color.trim().toLowerCase()) {
            case "i":
            case "cyan":
            case "cy":
                return ColorType.I;
            case "j":
            case "blue":
            case "bl":
                return ColorType.J;
            case "l":
            case "orange":
            case "or":
                return ColorType.L;
            case "o":
            case "yellow":
            case "ye":
                return ColorType.O;
            case "s":
            case "green":
            case "gr":
                return ColorType.S;
            case "t":
            case "purple":
            case "pu":
                return ColorType.T;
            case "z":
            case "red":
            case "re":
                return ColorType.Z;
            default:
                return null;
        }
    }

    void setFillColorType(String fillColor) throws UnsupportedDataTypeException {
        ColorType colorType = parseToColor(fillColor);
        if (colorType == null)
            throw new UnsupportedDataTypeException("Unsupported fill color: value=" + fillColor);
        this.fillColorType = colorType;
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

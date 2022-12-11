package entry.ren;

import core.field.Field;
import core.field.FieldFactory;
import core.srs.MinoRotation;
import entry.DropType;
import entry.common.kicks.NamedSupplierMinoRotation;
import entry.common.option.OptionsFacade;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RenSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/ren.html";

    private boolean isUsingHold = true;
    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private Field field = FieldFactory.createField(24);
    private List<String> patterns = new ArrayList<>();
    private DropType dropType = DropType.Softdrop;
    private NamedSupplierMinoRotation namedSupplierMinoRotation = NamedSupplierMinoRotation.createDefault();

    // ********* Getter ************
    public boolean isUsingHold() {
        return isUsingHold;
    }

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

    DropType getDropType() {
        return dropType;
    }

    String getKicksName() {
        return namedSupplierMinoRotation.getName();
    }

    Supplier<MinoRotation> createMinoRotationSupplier() {
        return namedSupplierMinoRotation.getSupplier();
    }

    // ********* Setter ************
    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

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
            case "180":
            case "softdrop180":
                this.dropType = DropType.Softdrop180;
                return;
            case "tsoft":
            case "tsoftdrop":
            case "t-soft":
            case "t-softdrop":
            case "t_soft":
            case "t_softdrop":
                this.dropType = DropType.SoftdropTOnly;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }

    void setKicks(String name) {
        namedSupplierMinoRotation = OptionsFacade.createNamedMinoRotationSupplier(name);
    }
}

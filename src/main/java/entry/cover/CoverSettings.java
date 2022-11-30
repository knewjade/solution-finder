package entry.cover;

import entry.DropType;
import exceptions.FinderParseException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CoverSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/cover.csv";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;
    private List<String> patterns = Collections.emptyList();
    private List<CoverParameter> parameters;
    private DropType dropType = DropType.Softdrop;
    private boolean isUsingHold = true;
    private CoverModes mode = CoverModes.Normal;
    private boolean isUsingPriority = false;
    private int lastSoftdrop = 0;
    private int startingB2B = 0;
    private int maxSoftdropTimes = -1;
    private int maxClearLineTimes = -1;

    // ********* Getter ************
    boolean isUsingHold() {
        return isUsingHold;
    }

    boolean isUsingPriority() {
        return isUsingPriority;
    }

    List<CoverParameter> getParameters() {
        return parameters;
    }

    List<String> getPatterns() {
        return patterns;
    }

    String getLogFilePath() {
        return logFilePath;
    }

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    DropType getDropType() {
        return dropType;
    }

    CoverModes getCoverModes() {
        return mode;
    }

    int getLastSoftdrop() {
        return lastSoftdrop;
    }

    int getStartingB2B() {
        return startingB2B;
    }

    Optional<Integer> getMaxSoftdropTimes() {
        return 0 <= maxSoftdropTimes ? Optional.of(maxSoftdropTimes) : Optional.empty();
    }

    Optional<Integer> getMaxClearLineTimes() {
        return 0 <= maxClearLineTimes ? Optional.of(maxClearLineTimes) : Optional.empty();
    }

    // ********* Setter ************
    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setUsingPriority(Boolean usingPriority) {
        this.isUsingPriority = usingPriority;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    boolean isOutputToConsole() {
        return true;
    }

    public void setParameters(List<CoverParameter> parameters) {
        this.parameters = parameters;
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
                this.dropType = DropType.Rotation180;
                return;
            case "tsoft":
            case "tsoftdrop":
            case "t-soft":
            case "t-softdrop":
            case "t_soft":
            case "t_softdrop":
                this.dropType = DropType.SoftdropTOnly;
                return;
            case "tsz":
            case "tspin0":
                this.dropType = DropType.TSpinZero;
                return;
            case "tsm":
            case "tspinm":
                this.dropType = DropType.TSpinMini;
                return;
            case "tss":
            case "tspin1":
                this.dropType = DropType.TSpinSingle;
                return;
            case "tsd":
            case "tspin2":
                this.dropType = DropType.TSpinDouble;
                return;
            case "tst":
            case "tspin3":
                this.dropType = DropType.TSpinTriple;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }

    void setCoverModes(String mode) throws FinderParseException {
        switch (mode.trim().toLowerCase().replace("_", "-")) {
            case "normal":
                this.mode = CoverModes.Normal;
                return;
            case "b2b":
                this.mode = CoverModes.B2BContinuous;
                return;
            case "any":
            case "any-tspin":
            case "anytspin":
            case "tsm":
            case "tspinm":
                this.mode = CoverModes.TSpinMini;
                return;
            case "tss":
            case "tspin1":
                this.mode = CoverModes.TSpinSingle;
                return;
            case "tsd":
            case "tspin2":
                this.mode = CoverModes.TSpinDouble;
                return;
            case "tst":
            case "tspin3":
                this.mode = CoverModes.TSpinTriple;
                return;
            case "tetris":
                this.mode = CoverModes.Tetris;
                return;
            case "tetris-end":
            case "tetrisend":
                this.mode = CoverModes.TetrisEnd;
                return;
            case "1l":
            case "1line":
            case "1lines":
                this.mode = CoverModes.OneLine;
                return;
            case "1l-or-pc":
            case "1line-or-pc":
            case "1lines-or-pc":
                this.mode = CoverModes.OneLineOrPC;
                return;
            case "2l":
            case "2line":
            case "2lines":
                this.mode = CoverModes.TwoLines;
                return;
            case "2l-or-pc":
            case "2line-or-pc":
            case "2lines-or-pc":
                this.mode = CoverModes.TwoLinesOrPC;
                return;
            case "3l":
            case "3line":
            case "3lines":
                this.mode = CoverModes.ThreeLines;
                return;
            case "3l-or-pc":
            case "3line-or-pc":
            case "3lines-or-pc":
                this.mode = CoverModes.ThreeLinesOrPC;
                return;
            case "4l":
            case "4line":
            case "4lines":
                this.mode = CoverModes.FourLines;
                return;
            case "4l-or-pc":
            case "4line-or-pc":
            case "4lines-or-pc":
                this.mode = CoverModes.FourLinesOrPC;
                return;
            default:
                throw new FinderParseException("Unsupported mode: mode=" + mode);
        }
    }

    public void setLastSoftdrop(int lastSoftdrop) {
        this.lastSoftdrop = lastSoftdrop;
    }

    void setStartingB2B(int startingB2B) {
        this.startingB2B = startingB2B;
    }

    void setMaxSoftdropTimes(int maxSoftdropTimes) {
        this.maxSoftdropTimes = maxSoftdropTimes;
    }

    void setMaxClearLineTimes(int maxClearLineTimes) {
        this.maxClearLineTimes = maxClearLineTimes;
    }
}

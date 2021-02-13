package entry.util.seq;

import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.List;

public class SeqUtilSettings {
    private boolean isDistinct = true;
    private int cuttingSize = -1;
    private List<String> patterns = new ArrayList<>();
    private SeqUtilModes mode = SeqUtilModes.Pass;

    // ********* Getter ************
    List<String> getPatterns() {
        return patterns;
    }

    SeqUtilModes getSeqUtilMode() {
        return mode;
    }

    int getCuttingSize() {
        return this.cuttingSize;
    }

    boolean isDistinct() {
        return this.isDistinct;
    }

    // ********* Setter ************
    void setSeqUtilMode(String mode) throws FinderParseException {
        assert mode != null;
        switch (mode.trim().toLowerCase()) {
            case "pass":
                this.mode = SeqUtilModes.Pass;
                return;
            case "forward":
                this.mode = SeqUtilModes.Forward;
                return;
            default:
                throw new FinderParseException("Unsupported mode: mode=" + mode);
        }
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setCuttingSize(int value) {
        this.cuttingSize = value;
    }

    void setDistinct(boolean value) {
        this.isDistinct = value;
    }
}

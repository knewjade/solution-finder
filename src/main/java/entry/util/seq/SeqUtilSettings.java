package entry.util.seq;

import entry.util.seq.equations.HoldEquation;
import entry.util.seq.equations.PieceEquation;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeqUtilSettings {
    private boolean isDistinct = true;
    private int length = -1;
    private List<String> patterns = new ArrayList<>();
    private SeqUtilModes mode = SeqUtilModes.Pass;
    private String expression = "";
    private String notExpression = "";
    private HoldEquation holdEquation = null;
    private List<PieceEquation> pieceEquations = new ArrayList<>();
    private boolean startsWithoutHold = false;

    // ********* Getter ************
    List<String> getPatterns() {
        return patterns;
    }

    SeqUtilModes getSeqUtilMode() {
        return mode;
    }

    int getLength() {
        return this.length;
    }

    boolean isDistinct() {
        return this.isDistinct;
    }

    String getExpression() {
        return expression;
    }

    String getNotExpression() {
        return notExpression;
    }

    Optional<HoldEquation> getHoldEquation() {
        return Optional.ofNullable(holdEquation);
    }

    List<PieceEquation> getPieceEquations() {
        return pieceEquations;
    }

    boolean isStartsWithoutHold() {
        return startsWithoutHold;
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
            case "backward":
                this.mode = SeqUtilModes.Backward;
                return;
            case "backward-pass":
            case "backward-and-pass":
                this.mode = SeqUtilModes.BackwardAndPass;
                return;
            default:
                throw new FinderParseException("Unsupported mode: mode=" + mode);
        }
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setLength(int value) {
        this.length = value;
    }

    void setDistinct(boolean value) {
        this.isDistinct = value;
    }

    void setExpression(String value) {
        this.expression = value;
    }

    void setNotExpression(String value) {
        this.notExpression = value;
    }

    void setHoldEquation(HoldEquation holdEquation) {
        this.holdEquation = holdEquation;
    }

    void setPieceEquations(List<PieceEquation> pieceEquation) {
        this.pieceEquations = pieceEquation;
    }

    void setStartsWithoutHold(boolean startsWithoutHold) {
        this.startsWithoutHold = startsWithoutHold;
    }
}

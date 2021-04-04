package entry.util.seq.equations;

import core.mino.Piece;

import java.util.EnumMap;
import java.util.Objects;
import java.util.function.Predicate;

public class PieceEquation {
    private final Piece piece;
    private final int value;
    final Operators operator;

    public PieceEquation(Piece piece, int value, Operators operator) {
        this.piece = piece;
        this.value = value;
        this.operator = operator;
    }

    public Predicate<EnumMap<Piece, Integer>> toPredict() {
        switch (operator) {
            case EqualTo:
                return pieceCounterMap -> pieceCounterMap.getOrDefault(piece, 0) == value;
            case NotEqualTo:
                return pieceCounterMap -> pieceCounterMap.getOrDefault(piece, 0) != value;
            case GreaterThan:
                return pieceCounterMap -> value < pieceCounterMap.getOrDefault(piece, 0);
            case GreaterThanOrEqualTo:
                return pieceCounterMap -> value <= pieceCounterMap.getOrDefault(piece, 0);
            case LessThan:
                return pieceCounterMap -> pieceCounterMap.getOrDefault(piece, 0) < value;
            case LessThanOrEqualTo:
                return pieceCounterMap -> pieceCounterMap.getOrDefault(piece, 0) <= value;
        }
        throw new IllegalStateException("Unsupported operator: operator=" + operator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceEquation that = (PieceEquation) o;
        return value == that.value && piece == that.piece && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, value, operator);
    }
}

package entry.util.seq.equations;

public enum Operators {
    GreaterThan,
    GreaterThanOrEqualTo,
    LessThan,
    LessThanOrEqualTo,
    EqualTo,
    NotEqualTo,
    ;

    Operators flip() {
        switch (this) {
            case EqualTo:
                return EqualTo;
            case NotEqualTo:
                return NotEqualTo;
            case GreaterThan:
                return LessThan;
            case LessThan:
                return GreaterThan;
            case GreaterThanOrEqualTo:
                return LessThanOrEqualTo;
            case LessThanOrEqualTo:
                return GreaterThanOrEqualTo;
        }
        throw new IllegalStateException("Unsupported type");
    }
}

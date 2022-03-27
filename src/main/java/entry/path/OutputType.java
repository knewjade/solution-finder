package entry.path;

public enum OutputType {
    CSV,
    HTML,
    TetfuCSV,
    PatternCSV,
    UseCSV,
    ;

    public boolean isCSV() {
        switch (this) {
            case CSV:
            case TetfuCSV:
            case PatternCSV:
            case UseCSV:
                return true;
            case HTML:
                return false;
        }
        throw new IllegalStateException("Unsupported output type");
    }
}

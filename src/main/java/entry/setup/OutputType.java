package entry.setup;

public enum OutputType {
    CSV,
    HTML,
    ;

    public boolean isCSV() {
        return this == CSV;
    }
}

package entry.path;

public enum OutputType {
    CSV("csv"),
    Link("html"),;

    private final String extension;

    OutputType(String extension) {
        this.extension = extension;
    }

    public String getTypeName() {
        return this.name().toLowerCase();
    }

    public String getExtension() {
        return extension;
    }
}

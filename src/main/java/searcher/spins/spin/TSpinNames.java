package searcher.spins.spin;

public enum TSpinNames {
    Neo("NEO"),
    Fin("FIN"),
    Iso("ISO"),
    NoName("Regular"),
    ;

    private final String name;

    TSpinNames(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

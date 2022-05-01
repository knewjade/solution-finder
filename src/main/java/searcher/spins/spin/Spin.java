package searcher.spins.spin;

import java.util.Objects;

public class Spin implements Comparable<Spin> {
    private final TSpins spin;
    private final TSpinNames name;
    private final int clearedLine;

    public Spin(TSpins spin, TSpinNames name, int clearedLine) {
        this.spin = spin;
        this.name = name;
        this.clearedLine = clearedLine;
    }

    public TSpins getSpin() {
        return spin;
    }

    public TSpinNames getName() {
        return name;
    }

    public int getClearedLine() {
        return clearedLine;
    }

    public boolean isMini() {
        return spin == TSpins.Mini;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spin spin1 = (Spin) o;
        return clearedLine == spin1.clearedLine &&
                spin == spin1.spin &&
                name == spin1.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spin, name, clearedLine);
    }

    @Override
    public String toString() {
        return "Spin {" + spin + "(" + clearedLine + ") [" + name + "]}";
    }

    @Override
    public int compareTo(Spin o) {
        int spinCompare = this.spin.compareTo(o.spin);
        if (spinCompare != 0) {
            return spinCompare;
        }

        int nameCompare = this.name.compareTo(o.name);
        if (nameCompare != 0) {
            return nameCompare;
        }

        return Integer.compare(this.clearedLine, o.clearedLine);
    }
}

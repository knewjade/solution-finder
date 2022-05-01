package entry.spin.output;

import output.HTMLColumn;
import searcher.spins.spin.Spin;

import java.util.Objects;
import java.util.Optional;

public class FullSpinColumn implements HTMLColumn, Comparable<FullSpinColumn> {
    private final Spin spin;
    private final int priority;
    private final String title;

    FullSpinColumn(Spin spin, int priority, String clearLineString, String spinName) {
        this.spin = spin;
        this.title = clearLineString + " [" + spinName + "]";
        this.priority = priority;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getId() {
        return title.toLowerCase().replace(' ', '-');
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullSpinColumn that = (FullSpinColumn) o;
        return spin.equals(that.spin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spin);
    }

    @Override
    public int compareTo(FullSpinColumn o) {
        int compare = Integer.compare(this.priority, o.priority);
        if (compare != 0) {
            return compare;
        }
        return spin.compareTo(o.spin);
    }
}

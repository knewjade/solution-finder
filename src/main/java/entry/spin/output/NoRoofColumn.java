package entry.spin.output;

import output.HTMLColumn;
import searcher.spins.spin.Spin;

import java.util.Objects;
import java.util.Optional;

public class NoRoofColumn implements HTMLColumn, Comparable<NoRoofColumn> {
    private final int clearedLine;
    private final String title;

    NoRoofColumn(int clearedLine, String title) {
        this.clearedLine = clearedLine;
        this.title = title;
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
        NoRoofColumn that = (NoRoofColumn) o;
        return clearedLine == that.clearedLine &&
                title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clearedLine, title);
    }

    @Override
    public int compareTo(NoRoofColumn o) {
        return Integer.compare(this.clearedLine, o.clearedLine);
    }
}

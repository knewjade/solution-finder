package entry.ren;

import output.HTMLColumn;

import java.util.Objects;
import java.util.Optional;

class RenHTMLColumn implements HTMLColumn {
    private final int count;

    RenHTMLColumn(int count) {
        this.count = count;
    }

    @Override
    public String getTitle() {
        return String.valueOf(count) + " Ren";
    }

    @Override
    public String getId() {
        return String.valueOf(count) + "ren";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenHTMLColumn testHTML = (RenHTMLColumn) o;
        return count == testHTML.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count);
    }
}

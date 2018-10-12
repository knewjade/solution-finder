package entry.setup;

import core.field.Field;
import core.field.FieldView;
import output.HTMLColumn;

import java.util.Optional;

public class FieldHTMLColumn implements HTMLColumn {
    private final Field field;
    private final int maxHeight;
    private final String title;

    public FieldHTMLColumn(Field field, int maxHeight) {
        assert field != null;
        this.field = field;
        this.maxHeight = maxHeight;
        this.title = createTitle();
    }

    private String createTitle() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < 10; x++) {
            int onX = field.getBlockCountBelowOnX(x, maxHeight);
            builder.append(onX < 10 ? onX : "+");
        }
        return builder.toString();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getId() {
        StringBuilder id = new StringBuilder();
        for (int index = 0; index < field.getBoardCount(); index++)
            id.append(Long.toHexString(field.getBoard(index))).append("_");
        return id.toString();
    }

    @Override
    public Optional<String> getDescription() {
        String description = FieldView.toString(field, maxHeight, "<br />");
        return Optional.of(String.format("<tt>%s</tt>", description));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldHTMLColumn that = (FieldHTMLColumn) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }
}

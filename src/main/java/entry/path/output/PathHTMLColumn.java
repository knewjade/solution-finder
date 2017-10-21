package entry.path.output;

import output.HTMLColumn;

import java.util.Optional;

public enum PathHTMLColumn implements HTMLColumn {
    DeletedLine("ライン消去あり"),
    NotDeletedLine("ライン消去なし");

    private final String title;

    PathHTMLColumn(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getId() {
        return name().toLowerCase();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.empty();
    }
}

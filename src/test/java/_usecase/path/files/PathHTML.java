package _usecase.path.files;

import java.util.ArrayList;
import java.util.List;

public class PathHTML {
    private final String html;
    private final int pattern;
    private final List<String> deletedLineFumens;
    private final List<String> noDeletedLineFumens;

    PathHTML(String html, int pattern, List<String> noDeletedLineFumens, List<String> deletedLineFumens) {
        this.html = html;
        this.pattern = pattern;
        this.noDeletedLineFumens = noDeletedLineFumens;
        this.deletedLineFumens = deletedLineFumens;
    }

    public String getHtml() {
        return html;
    }

    public int pattern() {
        return pattern;
    }

    public List<String> allFumens() {
        ArrayList<String> fumens = new ArrayList<>(deletedLineFumens);
        fumens.addAll(noDeletedLineFumens);
        return fumens;
    }

    public List<String> deletedLineFumens() {
        return deletedLineFumens;
    }

    public List<String> noDeletedLineFumens() {
        return noDeletedLineFumens;
    }
}

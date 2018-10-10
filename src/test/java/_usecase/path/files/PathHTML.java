package _usecase.path.files;

import java.util.ArrayList;
import java.util.List;

public class PathHTML {
    private final String html;
    private final int pattern;
    private final int sequence;
    private final String mergedFumen;
    private final List<String> deletedLineFumens;
    private final List<String> noDeletedLineFumens;

    PathHTML(String html, int pattern, int sequence, String mergedFumen, List<String> noDeletedLineFumens, List<String> deletedLineFumens) {
        this.html = html;
        this.pattern = pattern;
        this.sequence = sequence;
        this.mergedFumen = mergedFumen;
        this.noDeletedLineFumens = noDeletedLineFumens;
        this.deletedLineFumens = deletedLineFumens;
    }

    public String getHtml() {
        return html;
    }

    public int pattern() {
        return pattern;
    }

    public int sequence() {
        return sequence;
    }

    public String mergedFumen() {
        return mergedFumen;
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

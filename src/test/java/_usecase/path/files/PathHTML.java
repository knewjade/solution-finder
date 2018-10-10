package _usecase.path.files;

import java.util.ArrayList;
import java.util.List;

public class PathHTML {
    private final String html;
    private final int pattern;
    private final int sequence;
    private final List<String> deletedLineFumens;
    private final String allSolutionFumen;
    private final List<String> noDeletedLineFumens;

    PathHTML(String html, int pattern, int sequence, String allSolutionFumen, List<String> noDeletedLineFumens, List<String> deletedLineFumens) {
        this.html = html;
        this.pattern = pattern;
        this.sequence = sequence;
        this.allSolutionFumen = allSolutionFumen;
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

    public String allSolutionFumen() {
        return allSolutionFumen;
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

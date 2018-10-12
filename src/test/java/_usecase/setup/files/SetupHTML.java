package _usecase.setup.files;

import java.util.List;

public class SetupHTML {
    private final String html;
    private final String mergedFumen;
    private final List<String> fumens;

    SetupHTML(String html, String mergedFumen, List<String> fumens) {
        this.html = html;
        this.mergedFumen = mergedFumen;
        this.fumens = fumens;
    }

    public String getMergedFumen() {
        return mergedFumen;
    }

    public String getHtml() {
        return html;
    }

    public List<String> getFumens() {
        return fumens;
    }
}

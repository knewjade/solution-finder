package _usecase.setup.files;

import java.util.List;

public class SetupHTML {
    private final String html;
    private final List<String> fumens;

    SetupHTML(String html, List<String> fumens) {
        this.html = html;
        this.fumens = fumens;
    }

    public String getHtml() {
        return html;
    }

    public List<String> getFumens() {
        return fumens;
    }
}

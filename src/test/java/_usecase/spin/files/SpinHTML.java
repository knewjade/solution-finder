package _usecase.spin.files;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpinHTML {
    private final String html;
    private final String mergedFumen;
    private final Map<TSpinType, List<String>> fumensEachSpin;

    SpinHTML(String html, String mergedFumen, Map<TSpinType, List<String>> fumensEachSpin) {
        this.html = html;
        this.mergedFumen = mergedFumen;
        this.fumensEachSpin = fumensEachSpin;
    }

    public String getMergedFumen() {
        return mergedFumen;
    }

    public String getHtml() {
        return html;
    }

    public List<String> getAllFumens() {
        return fumensEachSpin.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<String> getFumensBySpin(TSpinType spin) {
        return fumensEachSpin.getOrDefault(spin, Collections.emptyList());
    }
}

package output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

// TODO: write unittest
public class HTMLBuilder<T extends HTMLColumn> {
    private final List<String> header = new ArrayList<>();
    private final HashMap<T, List<String>> maps = new HashMap<>();
    private final String pageTitle;

    public HTMLBuilder(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public synchronized void addHeader(String line) {
        header.add(line);
    }

    public synchronized void addColumn(T column, String line) {
        List<String> list = maps.computeIfAbsent(column, t -> new ArrayList<>());
        list.add(line);
    }

    public List<String> toList(List<T> columnPriorityList, boolean isNavigator) {
        List<String> allLines = new ArrayList<>();

        allLines.addAll(createHTMLHeader(pageTitle));

        if (!header.isEmpty()) {
            ArrayList<String> lines = createSectionHeader();
            allLines.addAll(lines);
        }

        if (isNavigator) {
            List<String> lines = createIndex(columnPriorityList);
            allLines.addAll(lines);
        }

        boolean isFirst = true;
        for (T column : columnPriorityList) {
            if (maps.containsKey(column)) {
                List<String> lines = createColumnSection(column, isFirst);
                allLines.addAll(lines);
                isFirst = false;
            }
        }

        allLines.addAll(createHTMLFooter());

        return allLines;
    }

    private List<String> createHTMLHeader(String pageTitle) {
        List<String> header = new ArrayList<>();
        header.add("<!DOCTYPE html>");
        header.add(String.format("<html lang=ja><head><meta charset=\"utf-8\"><title>%s</title></head><body>", pageTitle));
        return header;
    }

    private ArrayList<String> createSectionHeader() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("<header>");
        lines.addAll(header);
        lines.add("</header>");
        return lines;
    }

    private List<String> createIndex(List<T> columnPriorityList) {
        List<String> lines = new ArrayList<>();
        lines.add("<nav>");
        lines.add("<h2>Index</h2>");
        lines.add("<ul>");

        for (T column : columnPriorityList)
            lines.add(String.format("<li><a href='#%s'>%s</a></li>", column.getId(), column.getTitle()));

        lines.add("</ul>");
        lines.add("</nav>");
        lines.add("<hr><hr>");
        return lines;
    }

    private List<String> createColumnSection(T column, boolean isFirst) {
        List<String> lines = new ArrayList<>();

        if (!isFirst)
            lines.add("<hr>");

        lines.add(String.format("<section id='%s'>", column.getId()));
        lines.add(String.format("<h2>%s</h2>", column.getTitle()));
        column.getDescription().ifPresent(description -> {
            lines.add(String.format("<p>%s</p>", description));
        });
        lines.addAll(maps.get(column));
        lines.add("</section>");
        return lines;
    }

    private List<String> createHTMLFooter() {
        List<String> footer = new ArrayList<>();
        footer.add("</body>");
        footer.add("</html>");
        return footer;
    }

    public Set<T> getRegisteredColumns() {
        return maps.keySet();
    }
}

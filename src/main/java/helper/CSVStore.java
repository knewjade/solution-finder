package helper;

import java.util.*;

public class CSVStore {
    private static final List<String> EMPTY_COLUMNS = Collections.emptyList();

    private final List<String> columnNames;
    private final HashMap<String, List<String>> map = new HashMap<>();
    private int size = 0;

    public CSVStore(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public void load(String line) {
        String[] split = line.split(",");
        for (int index = 0; index < columnNames.size(); index++) {
            String name = columnNames.get(index);
            String value = index < split.length ? split[index] : "";
            List<String> list = map.computeIfAbsent(name, k -> new ArrayList<>());
            list.add(value);
        }
        size += 1;
    }

    public int size() {
        return size;
    }

    public List<String> columns(String name) {
        return map.getOrDefault(name, EMPTY_COLUMNS);
    }

    public Map<String, String> row(String name, String value) {
        List<String> list = map.getOrDefault(name, EMPTY_COLUMNS);
        int index = list.indexOf(value);
        if (index < 0)
            return Collections.emptyMap();

        HashMap<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String foundName = entry.getKey();
            String foundValue = entry.getValue().get(index);
            result.put(foundName, foundValue);
        }
        return result;
    }

    public Set<String> keySet() {
        return map.keySet();
    }
}

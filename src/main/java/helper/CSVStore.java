package helper;

import java.util.*;

public class CSVStore {
    private static final List<String> EMPTY_COLUMNS = Collections.emptyList();

    private final List<String> columnNames;
    private final Map<String, List<String>> map;
    private int size;

    public CSVStore(List<String> columnNames) {
        this(columnNames, new HashMap<>(), 0);
    }

    private CSVStore(List<String> columnNames, Map<String, List<String>> map, int size) {
        this.columnNames = columnNames;
        this.map = map;
        this.size = size;
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

    public Map<String, String> findRow(String name, String value) {
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

    public CSVStore filter(String name, String value) {
        List<String> columns = map.getOrDefault(name, EMPTY_COLUMNS);
        List<Integer> indexes = new ArrayList<>();
        for (int index = 0; index < columns.size(); index++) {
            String s = columns.get(index);
            if (s.equals(value)) {
                indexes.add(index);
            }
        }

        HashMap<String, List<String>> newMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> from = entry.getValue();

            List<String> newColumns = new ArrayList<>();
            for (int index : indexes) {
                newColumns.add(from.get(index));
            }

            newMap.put(entry.getKey(), newColumns);
        }

        return new CSVStore(columnNames, newMap, indexes.size());
    }
}

package helper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;


class CSVStoreTest {
    @Test
    void size() {
        List<String> columnNames = Arrays.asList("id", "name");
        CSVStore csvStore = new CSVStore(columnNames);
        csvStore.load("1,hello");
        csvStore.load("2,world");
        csvStore.load("3,!");
        assertThat(csvStore.size()).isEqualTo(3);
    }

    @Test
    void columns() {
        List<String> columnNames = Arrays.asList("id", "name", "number");
        CSVStore csvStore = new CSVStore(columnNames);
        csvStore.load("id1,hello,one");
        csvStore.load("id2,,two");
        csvStore.load("id3,world,three");
        assertThat(csvStore.columns("name")).isEqualTo(Arrays.asList("hello", "", "world"));
    }

    @Test
    void row() {
        List<String> columnNames = Arrays.asList("id", "name", "number");
        CSVStore csvStore = new CSVStore(columnNames);
        csvStore.load("id1,hello,one");
        csvStore.load("id2,,two");
        csvStore.load("id3,world,three");
        assertThat(csvStore.findRow("id", "id3"))
                .contains(entry("id", "id3"))
                .contains(entry("name", "world"))
                .contains(entry("number", "three"));
    }

    @Test
    void row2() {
        List<String> columnNames = Arrays.asList("c1", "c2", "c3", "c4", "c5");
        CSVStore csvStore = new CSVStore(columnNames);
        csvStore.load("a,b,c,,");
        assertThat(csvStore.findRow("c1", "a"))
                .contains(entry("c1", "a"))
                .contains(entry("c2", "b"))
                .contains(entry("c3", "c"))
                .contains(entry("c4", ""))
                .contains(entry("c5", ""));
    }
}
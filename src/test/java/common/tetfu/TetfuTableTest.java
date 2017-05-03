package common.tetfu;

import common.datastore.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TetfuTableTest {
    @Test
    public void escapeChar() throws Exception {
        ArrayList<Pair<Character, String>> testCase = new ArrayList<Pair<Character, String>>() {
            {
                add(new Pair<>('a', "a"));
                add(new Pair<>('b', "b"));
                add(new Pair<>('Z', "Z"));
                add(new Pair<>(' ', " "));
                add(new Pair<>('@', "@"));
                add(new Pair<>('_', "_"));
                add(new Pair<>('*', "*"));
                add(new Pair<>('+', "+"));
                add(new Pair<>('-', "-"));
                add(new Pair<>('.', "."));
                add(new Pair<>('/', "/"));
                add(new Pair<>('~', "%7E"));
                add(new Pair<>('^', "%5E"));
                add(new Pair<>('[', "%5B"));
                add(new Pair<>(']', "%5D"));
                add(new Pair<>(',', "%2C"));
                add(new Pair<>('(', "%28"));
                add(new Pair<>(')', "%29"));
                add(new Pair<>('あ', "%u3042"));
                add(new Pair<>('ア', "%u30A2"));
                add(new Pair<>('漢', "%u6F22"));
            }
        };

        for (Pair<Character, String> pair : testCase)
            assertThat(TetfuTable.escape(pair.getKey()), is(pair.getValue()));
    }

    @Test
    public void escapeString() throws Exception {
        ArrayList<Pair<String, String>> testCase = new ArrayList<Pair<String, String>>() {
            {
                add(new Pair<>("こんにちは", "%u3053%u3093%u306B%u3061%u306F"));
                add(new Pair<>("ユニットテスト！", "%u30E6%u30CB%u30C3%u30C8%u30C6%u30B9%u30C8%uFF01"));
                add(new Pair<>("単体試験", "%u5358%u4F53%u8A66%u9A13"));
            }
        };

        for (Pair<String, String> pair : testCase)
            assertThat(TetfuTable.escape(pair.getKey()), is(pair.getValue()));
    }

    @Test
    public void encodeInteger() throws Exception {
        ArrayList<Pair<Integer, String>> testCase = new ArrayList<Pair<Integer, String>>() {
            {
                add(new Pair<>(0, "A"));
                add(new Pair<>(25, "Z"));
                add(new Pair<>(26, "a"));
                add(new Pair<>(51, "z"));
                add(new Pair<>(52, "0"));
                add(new Pair<>(61, "9"));
                add(new Pair<>(62, "+"));
                add(new Pair<>(63, "/"));
            }
        };

        for (Pair<Integer, String> pair : testCase)
            assertThat(TetfuTable.encodeData(pair.getKey()), is(pair.getValue()));
    }

    @Test
    public void encodeAndDecode() throws Exception {
        List<String> testCase = Arrays.asList("a", "b", "c", "z", "A", "B", "Z", "0", "9", "+", "/");

        for (String str : testCase) {
            char c = str.toCharArray()[0];
            assertThat(TetfuTable.encodeData(TetfuTable.decodeData(c)), is(str));
        }
    }
}
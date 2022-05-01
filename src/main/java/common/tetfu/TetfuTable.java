package common.tetfu;

import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TetfuTable {
    private static final List<Character> ASCII_CHARACTERS = parseStringToCharsList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz 0123456789 @*_+-./");
    private static final List<Character> ENCODE_TABLE = parseStringToCharsList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
    private static final List<Character> COMMENT_TABLE = parseStringToCharsList(" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~");

    public static final int ENCODE_TABLE_SIZE = ENCODE_TABLE.size();
    public static final int COMMENT_TABLE_SIZE = COMMENT_TABLE.size() + 1;  // テト譜面の仕様

    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();

    private static List<Character> parseStringToCharsList(String str) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char c : str.toCharArray())
            chars.add(c);
        return chars;
    }

    public static String escape(String str) {
        return str.chars().boxed()
                .map(c -> escape((char) c.intValue()))
                .collect(Collectors.joining());
    }

    static String escape(char c) {
        if (ASCII_CHARACTERS.contains(c))
            return String.valueOf(c);
        else if (c < 256)
            return String.format("%%%x", (int) c).toUpperCase();
        return "%u" + String.format("%x", (int) c).toUpperCase();
    }

    static String encodeData(int value) {
        return String.valueOf(ENCODE_TABLE.get(value));
    }

    static int decodeData(char c) {
        return ENCODE_TABLE.indexOf(c);
    }

    public static int encodeCommentChar(char c) {
        return COMMENT_TABLE.indexOf(c);
    }

    public static char decodeCommentChar(int value) {
        assert 0 <= value && value < COMMENT_TABLE.size() : value;
        return COMMENT_TABLE.get(value);
    }

    public static ColorType parseColorType(int number) {
        return COLOR_CONVERTER.parseToColorType(number);
    }

    public static String unescape(String str) {
        LinkedList<Character> chars = new LinkedList<>();
        for (char character : str.toCharArray())
            chars.add(character);

        StringBuilder builder = new StringBuilder();
        while (!chars.isEmpty()) {
            Character c = chars.pollFirst();
            if (ASCII_CHARACTERS.contains(c)) {
                builder.append(c);
            } else if (c == '%') {
                Character c2 = chars.pollFirst();
                assert c2 != null;
                if (c2 != 'u') {
                    Character c3 = chars.pollFirst();
                    String value = c2 + String.valueOf(c3);
                    char newChar = (char) Integer.valueOf(value, 16).intValue();
                    builder.append(newChar);
                } else {
                    StringBuilder value = new StringBuilder();
                    for (int count = 0; count < 4; count++)
                        value.append(chars.pollFirst());
                    char newChar = (char) Integer.valueOf(value.toString(), 16).intValue();
                    builder.append(newChar);
                }
            } else {
                throw new IllegalStateException("Unknown character");
            }
        }
        return builder.toString();
    }
}

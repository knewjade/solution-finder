package entry.common.kicks;

import common.parser.StringEnumTransform;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KickPatternInterpreter {
    static class XYMark {
        private final int x;
        private final int y;
        private final boolean mark;

        XYMark(int x, int y, boolean mark) {
            this.x = x;
            this.y = y;
            this.mark = mark;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        boolean isMark() {
            return mark;
        }
    }

    public static KickPattern create(String key, String value) {
        String trimmedKey = key.trim();
        String trimmedValue = value.replaceAll(" ", "");

        KickType kickType = parseToKickType(trimmedKey)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected key: key=" + trimmedKey));

        if (trimmedValue.startsWith("&")) {
            // reference
            return parseToKickType(trimmedValue.substring(1))
                    .map(referenceKickType -> new ReferencedKickPattern(kickType, referenceKickType))
                    .orElseThrow(() -> new IllegalArgumentException("Unexpected key: key=" + trimmedKey));
        } else {
            // fixed
            core.srs.Pattern offset = parseToPattern(trimmedValue);
            return new FixedKickPattern(kickType, offset);
        }
    }

    private static Optional<KickType> parseToKickType(String str) {
        str = str.trim().toUpperCase();

        if (!str.matches("[TIOLJSZ]\\.[NEWSLR02]{2}")) {
            return Optional.empty();
        }

        try {
            Piece piece = StringEnumTransform.toPiece(str.charAt(0));
            Rotate rotateFrom = StringEnumTransform.toNEWSRotate(str.charAt(2));
            Rotate rotateTo = StringEnumTransform.toNEWSRotate(str.charAt(3));
            return Optional.of(new KickType(piece, rotateFrom, rotateTo));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static core.srs.Pattern parseToPattern(String str) {
        validate(str);

        List<String> brackets = detectBrackets(str);

        if (brackets.isEmpty()) {
            throw new IllegalArgumentException("Invalid value: value=" + str);
        }

        List<XYMark> XYMarks = detectXYs(str, brackets);

        assert brackets.size() == XYMarks.size();

        return createPattern(XYMarks);
    }

    private static void validate(String str) {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("value is empty: value=" + str);
        }

        long count1 = str.chars().filter(it -> it == '(').count();
        long count2 = str.chars().filter(it -> it == ')').count();

        if (count1 != count2) {
            throw new IllegalArgumentException("brackets don't match: value=" + str);
        }

        if (count1 == 0) {
            throw new IllegalArgumentException("no bracket: value=" + str);
        }
    }

    private static List<String> detectBrackets(String str) {
        List<String> lines = new ArrayList<>();

        java.util.regex.Pattern patter = java.util.regex.Pattern.compile("\\((.*?)\\)");
        Matcher matcher = patter.matcher(str);
        while (matcher.find()) {
            assert matcher.groupCount() == 1;
            String found = matcher.group(1);
            if (found.contains("(")) {
                throw new IllegalArgumentException("brackets pair is unexpected: value=" + str);
            }
            lines.add(found);
        }

        return lines;
    }

    private static List<XYMark> detectXYs(String str, List<String> brackets) {
        Pattern pattern = Pattern.compile("^(@?)([-+]?\\d+),([-+]?\\d+)$");

        return brackets.stream()
                .map(String::trim)
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);

                    if (!matcher.find()) {
                        throw new IllegalArgumentException("Unexpected value format: value=" + str);
                    }

                    assert matcher.groupCount() == 3;

                    String mark = matcher.group(1);
                    String x = matcher.group(2);
                    String y = matcher.group(3);
                    return new XYMark(Integer.parseInt(x), Integer.parseInt(y), !mark.isEmpty());
                })
                .collect(Collectors.toList());
    }

    private static core.srs.Pattern createPattern(List<XYMark> xyMarks) {
        int size = xyMarks.size();
        int[][] offsets = new int[size][2];
        boolean[] privilegeSpins = new boolean[size];
        for (int index = 0; index < size; index++) {
            XYMark xyMark = xyMarks.get(index);
            offsets[index] = new int[]{xyMark.getX(), xyMark.getY()};
            privilegeSpins[index] = xyMark.isMark();
        }

        return new core.srs.Pattern(offsets, privilegeSpins);
    }

    private KickPatternInterpreter() {
    }
}

package searcher.spins.wall;

import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SpinMaskFields {
    private static final List<Integer> DIFF = Arrays.asList(-1, 1);

    private final HashMap<Integer, List<MaskField>> maskFields;

    SpinMaskFields(int maxTargetHeight) {
        this.maskFields = getTSpinMaskFields(maxTargetHeight);
    }

    // Tスピンとして判定されるのに必要なブロックを取得
    private HashMap<Integer, List<MaskField>> getTSpinMaskFields(int maxHeight) {
        int fieldHeight = maxHeight + 1;

        HashMap<Integer, List<MaskField>> maps = new HashMap<>();

        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < 10; x++) {
                List<MaskField> fields = createMaskFields(x, y, fieldHeight);
                int key = toKey(x, y);
                maps.put(key, fields);
            }
        }

        return maps;
    }

    private List<MaskField> createMaskFields(int x, int y, int fieldHeight) {
        // 回転軸のまわり4隅を埋める
        Field field = FieldFactory.createField(fieldHeight);
        for (Integer dx : DIFF) {
            for (Integer dy : DIFF) {
                int fx = x + dx;
                int fy = y + dy;
                if (isInField(fx, fy)) {
                    field.setBlock(fx, fy);
                }
            }
        }

        return Stream.of(
                createMaskField(field, x - 1, y - 1, fieldHeight),
                createMaskField(field, x + 1, y - 1, fieldHeight),
                createMaskField(field, x - 1, y + 1, fieldHeight),
                createMaskField(field, x + 1, y + 1, fieldHeight),
                createMaskField(field, fieldHeight)
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static boolean isInField(int x, int y) {
        return 0 <= x && x < 10 && 0 <= y;
    }

    private MaskField createMaskField(Field field, int x, int y, int fieldHeight) {
        if (!isInField(x, y)) {
            return null;
        }

        Field freezeNeed = field.freeze();
        freezeNeed.removeBlock(x, y);

        Field freezeNotAllowed = FieldFactory.createField(fieldHeight);
        freezeNotAllowed.setBlock(x, y);

        return new MaskField(freezeNeed, freezeNotAllowed);
    }

    private MaskField createMaskField(Field field, int fieldHeight) {
        Field freezeNeed = field.freeze();
        Field freezeNotAllowed = FieldFactory.createField(fieldHeight);
        return new MaskField(freezeNeed, freezeNotAllowed);
    }

    Stream<MaskField> get(int x, int y) {
        int key = toKey(x, y);
        return maskFields.get(key).stream();
    }

    Stream<MaskField> get(int x, int y, long deletedKey) {
        int slideY = Long.bitCount(deletedKey & KeyOperators.getMaskForKeyBelowY(y));
        int key = toKey(x, y - slideY);
        return maskFields.get(key).stream()
                .map(maskField -> {
                    Field freezeNeed = maskField.getRemain().freeze();
                    freezeNeed.insertWhiteLineWithKey(deletedKey);

                    Field freezeNotAllowed = maskField.getNotAllowed().freeze();
                    freezeNotAllowed.insertWhiteLineWithKey(deletedKey);

                    return new MaskField(freezeNeed, freezeNotAllowed);
                });
    }

    private int toKey(int x, int y) {
        return x + y * 10;
    }
}

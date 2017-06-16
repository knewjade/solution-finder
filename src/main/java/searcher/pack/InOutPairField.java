package searcher.pack;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;

import java.util.ArrayList;
import java.util.List;

public class InOutPairField {
    public static List<InOutPairField> createInOutPairFields(int width, int height, Field initField) {
        return createInOutPairFields(width, height, initField, 9 / width);
    }

    private static List<InOutPairField> createInOutPairFields(int width, int height, Field initField, int max) {
        ArrayList<InOutPairField> pairs = new ArrayList<>();

        Field field = initField.freeze(height);
        for (int count = 0; count < max - 1; count++) {
            InOutPairField pairField = parse(field, width, height);
            pairs.add(pairField);
            field.slideLeft(width);
        }

        for (int y = 0; y < height; y++)
            for (int x = (10 - width * (max - 1)); x < width + 3; x++)
                field.setBlock(x, y);

        InOutPairField pairField = parseLast(field, width, height);
        pairs.add(pairField);

        return pairs;
    }

    private static InOutPairField parse(Field field, int width, int height) {
        ColumnSmallField innerField = new ColumnSmallField();
        ColumnSmallField outerField = new ColumnSmallField();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!field.isEmpty(x, y))
                    innerField.setBlock(x, y, height);
            }
            for (int x = width; x < width * 2; x++) {
                if (!field.isEmpty(x, y))
                    outerField.setBlock(x, y, height);
            }
        }
        return new InOutPairField(innerField, outerField);
    }

    private static InOutPairField parseLast(Field field, int width, int height) {
        ColumnSmallField innerField = new ColumnSmallField();
        ColumnSmallField outerField = new ColumnSmallField();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!field.isEmpty(x, y))
                    innerField.setBlock(x, y, height);
            }
            for (int x = width; x < width + 3; x++) {
                if (!field.isEmpty(x, y))
                    outerField.setBlock(x, y, height);
            }
        }
        return new InOutPairField(innerField, outerField);
    }

    private final ColumnField innerField;
    private final ColumnField outerField;

    public InOutPairField(ColumnField innerField, ColumnField outerField) {
        this.innerField = innerField;
        this.outerField = outerField;
    }

    public ColumnField getOuterField() {
        return outerField;
    }

    public ColumnField getInnerField() {
        return innerField;
    }
}

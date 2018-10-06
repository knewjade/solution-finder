package entry.common.field;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import entry.CommandLineWrapper;

import java.util.Optional;

public class FieldData {
    private final ColoredField coloredField;
    private final CommandLineWrapper wrapper;

    FieldData(ColoredField coloredField) {
        this(coloredField, null);
    }

    FieldData(ColoredField coloredField, CommandLineWrapper wrapper) {
        this.coloredField = coloredField;
        this.wrapper = wrapper;
    }

    public Field toField(int maxHeight) {
        int usingHeight = coloredField.getUsingHeight();
        int height = usingHeight < maxHeight ? maxHeight : usingHeight;
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        return field;
    }

    public Optional<CommandLineWrapper> getCommandLineWrapper() {
        return Optional.ofNullable(wrapper);
    }

    public ColoredField toColoredField() {
        return this.coloredField;
    }
}

package entry.path.output;

import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.Operations;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SequenceFumenParser implements FumenParser {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public SequenceFumenParser(MinoFactory minoFactory, ColorConverter colorConverter) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
    }

    @Override
    public String parse(List<MinoOperationWithKey> operations, Field field, int maxClearLine) {
        Operations operations2 = OperationTransform.parseToOperations(field, operations, maxClearLine);
        List<? extends Operation> operationsList = operations2.getOperations();

        // ブロック順に変換
        List<Block> blockList = operationsList.stream()
                .map(Operation::getBlock)
                .collect(Collectors.toList());

        // テト譜を作成
        String quiz = Tetfu.encodeForQuiz(blockList);
        ArrayList<TetfuElement> tetfuElements = new ArrayList<>();

        // 最初のelement
        Operation firstKey = operationsList.get(0);
        ColorType colorType1 = colorConverter.parseToColorType(firstKey.getBlock());
        ColoredField coloredField = createInitColoredField(field, maxClearLine);
        TetfuElement firstElement = new TetfuElement(coloredField, colorType1, firstKey.getRotate(), firstKey.getX(), firstKey.getY(), quiz);
        tetfuElements.add(firstElement);

        // 2番目以降のelement
        if (1 < operationsList.size()) {
            operationsList.subList(1, operationsList.size()).stream()
                    .map(operation -> {
                        ColorType colorType = colorConverter.parseToColorType(operation.getBlock());
                        return new TetfuElement(colorType, operation.getRotate(), operation.getX(), operation.getY(), quiz);
                    })
                    .forEach(tetfuElements::add);
        }

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        return tetfu.encode(tetfuElements);
    }

    private ColoredField createInitColoredField(Field initField, int maxClearLine) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField, maxClearLine);
        return coloredField;
    }

    private void fillInField(ColoredField coloredField, ColorType colorType, Field target, int maxClearLine) {
        for (int y = 0; y < maxClearLine; y++)
            for (int x = 0; x < 10; x++)
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
    }
}

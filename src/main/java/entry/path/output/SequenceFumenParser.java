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
import core.mino.MinoFactory;
import core.mino.Piece;

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
    public String parse(List<MinoOperationWithKey> operationsWithKey, Field field, int maxClearLine) {
        Operations operations = OperationTransform.parseToOperationsBeforeNoClearLine(field, operationsWithKey, maxClearLine);
        return parse(operations, field, maxClearLine);
    }

    public String parse(Operations operations, Field field, int maxClearLine) {
        List<? extends Operation> operationsList = operations.getOperations();

        // ブロック順に変換
        List<Piece> pieceList = operationsList.stream()
                .map(Operation::getPiece)
                .collect(Collectors.toList());

        // テト譜を作成
        String quiz = Tetfu.encodeForQuiz(pieceList);
        ArrayList<TetfuElement> tetfuElements = new ArrayList<>();

        // 最初のelement
        Operation firstKey = operationsList.get(0);
        ColorType colorType1 = colorConverter.parseToColorType(firstKey.getPiece());
        ColoredField coloredField = createInitColoredField(field, maxClearLine);
        TetfuElement firstElement = new TetfuElement(coloredField, colorType1, firstKey.getRotate(), firstKey.getX(), firstKey.getY(), quiz);
        tetfuElements.add(firstElement);

        // 2番目以降のelement
        if (1 < operationsList.size()) {
            operationsList.subList(1, operationsList.size()).stream()
                    .map(operation -> {
                        ColorType colorType = colorConverter.parseToColorType(operation.getPiece());
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

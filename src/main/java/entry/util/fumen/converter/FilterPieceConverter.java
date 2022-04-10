package entry.util.fumen.converter;

import common.datastore.*;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterPieceConverter implements FumenConverter {
    private static final int HEIGHT = 24;

    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final Piece filteredPiece;

    public FilterPieceConverter(MinoFactory minoFactory, ColorConverter colorConverter, Piece filteredPiece) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.filteredPiece = filteredPiece;
    }

    @Override
    public String parse(String data) throws FinderParseException {
        List<TetfuPage> filtered = new ArrayList<>();

        {
            Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
            List<TetfuPage> pages = tetfu.decode(data);

            for (TetfuPage page : pages) {
                if (page.isPutMino()) {
                    filtered.add(page);

                    if (page.isBlockUp() || page.isMirror()) {
                        break;
                    }
                }
            }
        }

        List<TetfuElement> elements = filterAndCreateElements(filtered);

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String encode = tetfu.encode(elements);
        return String.format("v115@%s", encode);
    }

    private List<TetfuElement> filterAndCreateElements(List<TetfuPage> pages) {
        if (pages.isEmpty()) {
            return Collections.emptyList();
        }

        Field headField = toField(pages.get(0).getField());

        List<Pair<TetfuPage, MinoOperationWithKey>> filteredPairs = createFilteredPairs(pages, headField);
        if (filteredPairs.isEmpty()) {
            return Collections.emptyList();
        }

        // Filtered operations with key -> operations
        List<? extends Operation> filteredOperations = OperationTransform.parseToOperations(
                headField, filteredPairs.stream().map(Pair::getValue).collect(Collectors.toList()), HEIGHT
        ).getOperations();

        assert filteredPairs.size() == filteredOperations.size();

        // operations and pages -> elements
        List<TetfuPage> filteredPages = filteredPairs.stream().map(Pair::getKey).collect(Collectors.toList());
        return buildElements(filteredPages, filteredOperations, pages.get(0));
    }

    private List<Pair<TetfuPage, MinoOperationWithKey>> createFilteredPairs(List<TetfuPage> pages, Field headField) {
        // Page -> Operations
        List<SimpleMinoOperation> operationList = pages.stream()
                .map(page -> {
                    Piece piece = colorConverter.parseToBlock(page.getColorType());
                    Mino mino = minoFactory.create(piece, page.getRotate());
                    return new SimpleMinoOperation(mino, page.getX(), page.getY());
                })
                .collect(Collectors.toList());

        // Operations -> Operations with Key
        List<MinoOperationWithKey> operationsWithKey = OperationTransform.parseToOperationWithKeys(
                headField, new Operations(operationList), minoFactory, HEIGHT
        );

        assert pages.size() == operationsWithKey.size();

        // Mapping page and operation with key
        // Filter pages that use specified piece
        List<Pair<TetfuPage, MinoOperationWithKey>> pairs = new ArrayList<>();
        for (int index = 0; index < operationsWithKey.size(); index++) {
            MinoOperationWithKey operation = operationsWithKey.get(index);
            if (operation.getPiece() == filteredPiece) {
                continue;
            }

            pairs.add(new Pair<>(pages.get(index), operationsWithKey.get(index)));
        }

        return pairs;
    }

    private List<TetfuElement> buildElements(
            List<TetfuPage> pages, List<? extends Operation> operations, TetfuPage headPage
    ) {
        assert pages.size() == operations.size();
        assert !pages.isEmpty();
        assert headPage != null;

        List<TetfuElement> elements = new ArrayList<>();
        for (int index = 0; index < operations.size(); index++) {
            Operation operation = operations.get(index);
            TetfuPage page = pages.get(index);
            ColoredField field = index == 0 ? headPage.getField() : null;
            elements.add(new TetfuElement(
                    field,
                    colorConverter.parseToColorType(operation.getPiece()),
                    operation.getRotate(),
                    operation.getX(),
                    operation.getY(),
                    page.getComment(),
                    page.isLock(),
                    page.isMirror(),
                    page.isBlockUp(),
                    page.getBlockUpList()
            ));
        }

        return elements;
    }

    private Field toField(ColoredField coloredField) {
        Field field = FieldFactory.createField(HEIGHT);
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        return field;
    }
}

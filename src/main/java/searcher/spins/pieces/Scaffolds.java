package searcher.spins.pieces;

import core.field.Field;
import core.field.FieldFactory;
import core.neighbor.SimpleOriginalPiece;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scaffolds {
    public static Scaffolds create(MinimalSimpleOriginalPieces originalPieces) {
        return new Scaffolds(originalPieces.getOriginalPieces());
    }

    private static final Field EMPTY_FIELD = FieldFactory.createField(24);

    private final List<SimpleOriginalPiece> originalPieces;
    private final Map<Long, List<SimpleOriginalPiece>> keyToScaffolds = new ConcurrentHashMap<>();

    private Scaffolds(List<SimpleOriginalPiece> originalPieces) {
        this.originalPieces = originalPieces;
    }

    public Stream<SimpleOriginalPiece> get(SimpleOriginalPiece operation) {
        long key = operation.toUniqueKey();
        List<SimpleOriginalPiece> pieces = keyToScaffolds.computeIfAbsent(key, (it) -> create(operation));
        return pieces.stream();
    }

    private List<SimpleOriginalPiece> create(SimpleOriginalPiece operation) {
        if (EMPTY_FIELD.isOnGround(operation.getMino(), operation.getX(), operation.getY())) {
            return Collections.emptyList();
        }

        long needDeletedKey = operation.getNeedDeletedKey();
        long usingKey = operation.getUsingKey();

        Field minoField = operation.getMinoField();

        // 足場になるミノだけを抽出
        return originalPieces.stream()
                // 足場に必要な消去ライン中、ミノが含まれないこと
                .filter(scaffold -> (scaffold.getNeedDeletedKey() & usingKey) == 0L)
                .filter(scaffold -> minoField.canMerge(scaffold.getMinoField()))
                .filter(scaffold -> {
                    Field scaffoldField = scaffold.getMinoField().freeze();
                    scaffoldField.deleteLineWithKey(needDeletedKey);
                    return scaffoldField.isOnGround(operation.getMino(), operation.getX(), operation.getY());
                })
                .collect(Collectors.toList());
    }
}

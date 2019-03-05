package searcher.spins.roof;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;

import java.util.List;
import java.util.stream.Stream;

public class Roofs {
    private final List<SimpleOriginalPiece> originalPieces;

    public Roofs(MinimalSimpleOriginalPieces minimalSimpleOriginalPieces) {
        this.originalPieces = minimalSimpleOriginalPieces.getOriginalPieces();
    }

    public Stream<SimpleOriginalPiece> get(Field allMergedWithoutT, Field notAllowedWithT, long deletedKeyWithoutT) {
        Field freezeNotAllowed = allMergedWithoutT.freeze();
        freezeNotAllowed.merge(notAllowedWithT);

        return originalPieces.stream()
                .filter(it -> {
                    long needDeletedKey = it.getNeedDeletedKey();
                    return (deletedKeyWithoutT & needDeletedKey) == needDeletedKey;
                })
                .filter(it -> freezeNotAllowed.canMerge(it.getMinoField()))
                .filter(it -> {
                    Field field = allMergedWithoutT.freeze();
                    field.deleteLineWithKey(it.getNeedDeletedKey());
                    return field.isOnGround(it.getMino(), it.getX(), it.getY());
                });
    }
}

package searcher.spins.pieces.bits;

import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.MiddleField;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MiddleBitBlocks implements BitBlocks {
    private final Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>> maps;

    public static BitBlocks create(MinimalSimpleOriginalPieces minimalSimpleOriginalPieces) {
        List<SimpleOriginalPiece> originalPieces = minimalSimpleOriginalPieces.getOriginalPieces();
        int maxHeight = minimalSimpleOriginalPieces.getMaxHeight();

        assert originalPieces.stream().allMatch(it -> Field.isIn(it.getMino(), it.getX(), it.getY()));

        Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>> maps = new HashMap<>();

        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < 10; x++) {
                // 1ブロック埋める
                Field field = FieldFactory.createField(maxHeight);
                field.setBlock(x, y);

                // そのブロックと重なるものだけ抽出
                List<SimpleOriginalPiece> keyOriginalPieces = originalPieces.stream()
                        .filter(originalPiece -> !field.canMerge(originalPiece.getMinoField()))
                        .collect(Collectors.toList());

                // mapを取得
                long firstKey = field.getBoard(0);
                long secondKey = field.getBoard(1);

                Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>> firstMap = maps.computeIfAbsent(firstKey, (ignored) -> new HashMap<>());
                EnumMap<Piece, List<SimpleOriginalPiece>> secondMap = firstMap.computeIfAbsent(secondKey, (ignored) -> new EnumMap<>(Piece.class));

                // Pieceごとに追加
                for (Piece piece : Piece.values()) {
                    List<SimpleOriginalPiece> eachPiece = keyOriginalPieces.stream()
                            .filter(originalPiece -> originalPiece.getPiece() == piece)
                            .collect(Collectors.toList());
                    secondMap.put(piece, eachPiece);
                }
            }
        }

        return new MiddleBitBlocks(maps);
    }

    private MiddleBitBlocks(Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>> maps) {
        this.maps = maps;
    }

    @Override
    public FilteredBitBlocks filter(Field rest) {
        return new FilteredBitBlocks(getNextOriginPiecesMap(rest));
    }

    @Override
    public EnumMap<Piece, List<SimpleOriginalPiece>> getNextOriginPiecesMap(Field rest) {
        assert !rest.isPerfect();
        MiddleField field = getBit(rest);
        return get(field);
    }

    private EnumMap<Piece, List<SimpleOriginalPiece>> get(MiddleField field) {
        long firstKey = field.getBoard(0);
        long secondKey = field.getBoard(1);
        Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>> firstResponse = maps.get(firstKey);
        assert firstResponse != null : FieldView.toString(field);
        return firstResponse.get(secondKey);
    }

    private MiddleField getBit(Field field) {
        long firstBoard = field.getBoard(0);
        if (firstBoard != 0L) {
            long board = firstBoard & (-firstBoard);
            return new MiddleField(board, 0L);
        }

        long secondBoard = field.getBoard(1);
        long board = secondBoard & (-secondBoard);
        return new MiddleField(0L, board);
    }
}

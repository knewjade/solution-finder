package searcher.spins.pieces.bits;

import core.field.Field;
import core.field.FieldFactory;
import core.field.LargeField;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LargeBitBlocks implements BitBlocks {
    private final Map<Long, Map<Long, Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>>>> maps;

    public static LargeBitBlocks create(MinimalSimpleOriginalPieces minimalSimpleOriginalPieces) {
        List<SimpleOriginalPiece> originalPieces = minimalSimpleOriginalPieces.getOriginalPieces();
        int maxHeight = minimalSimpleOriginalPieces.getMaxHeight();

        assert originalPieces.stream().allMatch(it -> Field.isIn(it.getMino(), it.getX(), it.getY()));

        Map<Long, Map<Long, Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>>>> maps = new HashMap<>();

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
                long thirdKey = field.getBoard(2);
                long fourthKey = field.getBoard(3);

                Map<Long, Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>>> firstMap = maps.computeIfAbsent(firstKey, (ignored) -> new HashMap<>());
                Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>> secondMap = firstMap.computeIfAbsent(secondKey, (ignored) -> new HashMap<>());
                Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>> thirdMap = secondMap.computeIfAbsent(thirdKey, (ignored) -> new HashMap<>());
                EnumMap<Piece, List<SimpleOriginalPiece>> fourthMap = thirdMap.computeIfAbsent(fourthKey, (ignored) -> new EnumMap<>(Piece.class));

                // Pieceごとに追加
                for (Piece piece : Piece.values()) {
                    List<SimpleOriginalPiece> eachPiece = keyOriginalPieces.stream()
                            .filter(originalPiece -> originalPiece.getPiece() == piece)
                            .collect(Collectors.toList());
                    fourthMap.put(piece, eachPiece);
                }
            }
        }

        return new LargeBitBlocks(maps);
    }

    private LargeBitBlocks(Map<Long, Map<Long, Map<Long, Map<Long, EnumMap<Piece, List<SimpleOriginalPiece>>>>>> maps) {
        this.maps = maps;
    }

    @Override
    public FilteredBitBlocks filter(Field rest) {
        return new FilteredBitBlocks(getNextOriginPiecesMap(rest));
    }

    @Override
    public EnumMap<Piece, List<SimpleOriginalPiece>> getNextOriginPiecesMap(Field rest) {
        assert !rest.isPerfect();
        LargeField field = getBit(rest);
        return get(field);
    }

    private EnumMap<Piece, List<SimpleOriginalPiece>> get(LargeField field) {
        long firstKey = field.getBoard(0);
        long secondKey = field.getBoard(1);
        long thirdKey = field.getBoard(2);
        long fourthKey = field.getBoard(3);
        return maps.get(firstKey).get(secondKey).get(thirdKey).get(fourthKey);
    }

    private LargeField getBit(Field field) {
        long firstBoard = field.getBoard(0);
        if (firstBoard != 0L) {
            long board = firstBoard & (-firstBoard);
            return new LargeField(board, 0L, 0L, 0L);
        }

        long secondBoard = field.getBoard(1);
        if (secondBoard != 0L) {
            long board = secondBoard & (-secondBoard);
            return new LargeField(0L, board, 0L, 0L);
        }

        long thirdBoard = field.getBoard(2);
        if (thirdBoard != 0L) {
            long board = thirdBoard & (-thirdBoard);
            return new LargeField(0L, 0L, board, 0L);
        }

        long fourthBoard = field.getBoard(3);
        long board = fourthBoard & (-fourthBoard);
        return new LargeField(0L, 0L, 0L, board);
    }
}
package entry.util.seq;

import common.datastore.blocks.Pieces;
import core.mino.Piece;

import java.util.HashSet;
import java.util.stream.Collectors;

interface PieceOutput {
    void output(Pieces pieces);

    default String parseToString(Pieces pieces) {
        return pieces.blockStream()
                .map(Piece::getName)
                .collect(Collectors.joining());
    }
}

class SimplePieceOutput implements PieceOutput {
    @Override
    public void output(Pieces pieces) {
        String str = parseToString(pieces);
        System.out.println(str);
    }
}

class DistinctPieceOutput implements PieceOutput {
    private final HashSet<Pieces> map = new HashSet<>();

    @Override
    public void output(Pieces pieces) {
        boolean success = map.add(pieces);
        if (success) {
            String str = parseToString(pieces);
            System.out.println(str);
        }
    }
}

package entry.searching_pieces;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface EnumeratePiecesCore {
    Set<LongPieces> enumerate() throws IOException;

    // もとの重複を含む組み合わせ個数を返却
    int getCounter();
}

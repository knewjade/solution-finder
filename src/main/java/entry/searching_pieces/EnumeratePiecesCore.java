package entry.searching_pieces;

import common.datastore.blocks.LongPieces;

import java.io.IOException;
import java.util.Set;

public interface EnumeratePiecesCore {
    Set<LongPieces> enumerate() throws IOException;

    // もとの重複を含む組み合わせ個数を返却
    int getCounter();
}

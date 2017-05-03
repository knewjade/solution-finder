package entry.searching_pieces;

import core.mino.Block;

import java.io.IOException;
import java.util.List;

public interface EnumeratePiecesCore {
    List<List<Block>> enumerate() throws IOException;

    // もとの重複を含む組み合わせ個数を返却
    int getCounter();
}

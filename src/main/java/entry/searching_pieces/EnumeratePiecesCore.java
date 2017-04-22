package entry.searching_pieces;

import core.mino.Block;

import java.io.IOException;
import java.util.List;

public interface EnumeratePiecesCore {
    List<List<Block>> enumerate() throws IOException;
}

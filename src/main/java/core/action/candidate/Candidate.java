package core.action.candidate;

import core.field.Field;
import core.mino.Block;

import java.util.Set;

public interface Candidate<T> {
    // TODO: To Stream
    Set<T> search(Field field, Block block, int appearY);
}

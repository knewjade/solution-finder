package action.candidate;

import core.field.Field;
import core.mino.Block;

import java.util.Set;

public interface Candidate<T> {
    Set<T> search(Field field, Block block, int appearY);
}

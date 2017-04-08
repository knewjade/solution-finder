package action.candidate;

import core.mino.Block;
import core.field.Field;

import java.util.Set;

public interface Candidate<T> {
    Set<T> search(Field field, Block block, int appearY);
}

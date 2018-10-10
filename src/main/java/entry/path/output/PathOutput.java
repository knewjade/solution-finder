package entry.path.output;

import core.field.Field;
import entry.path.PathPairs;
import exceptions.FinderExecuteException;
import searcher.pack.SizedBit;

public interface PathOutput {
    void output(PathPairs pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException;
}

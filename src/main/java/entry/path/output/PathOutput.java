package entry.path.output;

import core.field.Field;
import entry.path.PathPair;
import exceptions.FinderExecuteException;
import searcher.pack.SizedBit;

import java.util.List;

public interface PathOutput {
    void output(List<PathPair> pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException;
}

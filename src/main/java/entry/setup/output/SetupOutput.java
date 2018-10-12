package entry.setup.output;

import core.field.Field;
import entry.setup.SetupResults;
import exceptions.FinderExecuteException;
import searcher.pack.SizedBit;

public interface SetupOutput {
    void output(SetupResults setupResults, Field field, SizedBit sizedBit) throws FinderExecuteException;
}

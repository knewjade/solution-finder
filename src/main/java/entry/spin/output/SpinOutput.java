package entry.spin.output;

import core.field.Field;
import entry.path.output.MyFile;
import entry.spin.OutputCandidate;
import exceptions.FinderExecuteException;

import java.util.List;

public interface SpinOutput {
    void output(MyFile myFile, List<OutputCandidate> results, Field initField, int fieldHeight) throws FinderExecuteException;
}

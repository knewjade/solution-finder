package entry.spin.output;

import core.field.Field;
import entry.path.output.MyFile;
import exceptions.FinderExecuteException;
import searcher.spins.candidates.Candidate;

import java.util.List;

public interface SpinOutput {
    int output(MyFile myFile, List<Candidate> results, Field initField, int fieldHeight) throws FinderExecuteException;
}

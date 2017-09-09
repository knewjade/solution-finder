package entry;

import exceptions.FinderException;
import exceptions.FinderTerminateException;
import exceptions.FinderExecuteException;

public interface EntryPoint {
    void run() throws FinderException;

    void close() throws FinderTerminateException;
}

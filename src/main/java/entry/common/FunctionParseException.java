package entry.common;

import exceptions.FinderParseException;

public interface FunctionParseException<T, R> {
    R apply(T t) throws FinderParseException;
}

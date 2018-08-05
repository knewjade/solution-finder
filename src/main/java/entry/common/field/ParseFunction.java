package entry.common.field;

import exceptions.FinderParseException;

public interface ParseFunction<T, R> {
    R apply(T t) throws FinderParseException;
}

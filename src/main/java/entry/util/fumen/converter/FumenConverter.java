package entry.util.fumen.converter;

import exceptions.FinderParseException;

public interface FumenConverter {
    String parse(String data) throws FinderParseException;
}

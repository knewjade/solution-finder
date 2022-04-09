package entry.util.fumen.converter;

import exceptions.FinderParseException;

import java.util.List;

public interface FumenConverter {

    List<String> parse(List<String> fumens) throws FinderParseException;
}

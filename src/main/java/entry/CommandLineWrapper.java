package entry;

import exceptions.FinderParseException;

import java.util.List;
import java.util.Optional;

public interface CommandLineWrapper {
    boolean hasOption(String name);

    Optional<Boolean> getBoolOption(String name) throws FinderParseException;

    Optional<String> getStringOption(String name);

    Optional<Double> getDoubleOption(String name) throws FinderParseException;

    Optional<Integer> getIntegerOption(String name) throws FinderParseException;

    List<String> getStringOptions(String name);
}

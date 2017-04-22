package entry;

import java.util.Optional;

public interface CommandLineWrapper {
    boolean hasOption(String name);

    Optional<Boolean> getBoolOption(String name);

    Optional<String> getStringOption(String name);

    Optional<Integer> getIntegerOption(String name);
}

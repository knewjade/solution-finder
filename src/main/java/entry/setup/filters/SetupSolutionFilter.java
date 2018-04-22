package entry.setup.filters;

import java.util.Objects;
import java.util.function.Predicate;

public interface SetupSolutionFilter extends Predicate<SetupResult> {
    default SetupSolutionFilter and(Predicate<? super SetupResult> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }
}
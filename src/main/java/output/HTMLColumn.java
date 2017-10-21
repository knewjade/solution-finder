package output;

import java.util.Optional;

public interface HTMLColumn {
    String getTitle();

    String getId();

    Optional<String> getDescription();
}

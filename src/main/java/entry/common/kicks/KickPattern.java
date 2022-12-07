package entry.common.kicks;

import core.srs.Pattern;

import java.util.Collections;
import java.util.Map;

public interface KickPattern {
    KickType getKickType();

    default Pattern getPattern() {
        return getPattern(Collections.emptyMap());
    }

    Pattern getPattern(Map<KickType, KickPattern> patterns);
}

package entry.common.kicks;

import core.srs.Pattern;

import java.util.Map;

class FixedKickPattern implements KickPattern {
    private final KickType kickType;
    private final Pattern pattern;

    FixedKickPattern(KickType kickType, Pattern pattern) {
        this.kickType = kickType;
        this.pattern = pattern;
    }

    @Override
    public KickType getKickType() {
        return kickType;
    }

    @Override
    public Pattern getPattern(Map<KickType, KickPattern> fallback) {
        return pattern;
    }
}

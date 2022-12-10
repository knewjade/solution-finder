package entry.common.kicks;

import core.srs.Pattern;

import java.util.Map;

class ReferencedKickPattern implements KickPattern {
    private final KickType kickType;
    private final KickType referenceKickType;

    ReferencedKickPattern(KickType kickType, KickType referenceKickType) {
        if (kickType.equals(referenceKickType)) {
            throw new IllegalArgumentException("Cannot refer to itself");
        }
        this.kickType = kickType;
        this.referenceKickType = referenceKickType;
    }

    @Override
    public KickType getKickType() {
        return kickType;
    }

    @Override
    public Pattern getPattern(Map<KickType, KickPattern> fallback) {
        KickPattern kickPattern = fallback.get(referenceKickType);
        if (kickPattern == null) {
            return null;
        }
        return kickPattern.getPattern(fallback);
    }
}

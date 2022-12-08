package entry.common.kicks;

import core.mino.Piece;
import core.srs.Pattern;
import core.srs.Rotate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KickPatterns {
    public static KickPatterns create(List<KickPattern> kickPatterns) {
        Map<KickType, KickPattern> map = kickPatterns.stream()
                .collect(Collectors.toMap(KickPattern::getKickType, Function.identity()));
        return new KickPatterns(map);
    }

    private final Map<KickType, KickPattern> kickPatterns;

    public KickPatterns(Map<KickType, KickPattern> kickPatterns) {
        this.kickPatterns = kickPatterns;
    }

    public Optional<Pattern> getPattern(
            Piece piece, Rotate rotateFrom, Rotate rotateTo
    ) {
        KickType kickType = new KickType(piece, rotateFrom, rotateTo);
        KickPattern kickPattern = kickPatterns.get(kickType);
        if (kickPattern == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(kickPattern.getPattern(kickPatterns));
    }

    public int size() {
        return kickPatterns.size();
    }
}

package entry.common.kicks.factory;

import core.mino.Piece;
import core.srs.*;
import entry.common.kicks.KickPatterns;
import entry.common.kicks.KickType;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class MinoRotationSupplier implements Supplier<MinoRotation> {

    public static final int SIZE_90_ONLY = 28 * 2;
    public static final int SIZE_WITH_180 = 28 * 3;

    private static class MapResult {
        static MapResult success(EnumMap<Piece, EnumMap<Rotate, Pattern>> result) {
            return new MapResult(result, null);
        }

        static MapResult failed(Piece piece, Rotate from, Rotate to) {
            return new MapResult(null, new KickType(piece, from, to));
        }

        private final EnumMap<Piece, EnumMap<Rotate, Pattern>> result;
        private final KickType failedKickType;

        MapResult(EnumMap<Piece, EnumMap<Rotate, Pattern>> result, KickType failedKickType) {
            this.result = result;
            this.failedKickType = failedKickType;
        }

        boolean isSuccess() {
            return result != null;
        }

        boolean isFailed() {
            return !isSuccess();
        }
    }

    private final KickPatterns kickPatterns;

    MinoRotationSupplier(KickPatterns kickPatterns) {
        this.kickPatterns = kickPatterns;
        validate();
    }

    private void validate() {
        int size = kickPatterns.size();
        if (size != SIZE_90_ONLY && size != SIZE_WITH_180) {
            throw new IllegalArgumentException(String.format("Unexpected number of kicks: size=%d", size));
        }

        {
            // right
            MapResult result = createRightMap();
            if (result.isFailed()) {
                KickType kickType = result.failedKickType;
                throw new IllegalArgumentException(String.format(
                        "invalid kicks: piece=%s, from=%s, to=%s",
                        kickType.getPiece(), kickType.getRotateFrom(), kickType.getRotateTo()
                ));
            }
        }
        {
            // left
            MapResult result = createLeftMap();
            if (result.isFailed()) {
                KickType kickType = result.failedKickType;
                throw new IllegalArgumentException(String.format(
                        "invalid kicks: piece=%s, from=%s, to=%s",
                        kickType.getPiece(), kickType.getRotateFrom(), kickType.getRotateTo()
                ));
            }
        }
        if (supports180()) {
            // 180
            MapResult result = createRotate180Map();
            if (result.isFailed()) {
                KickType kickType = result.failedKickType;
                throw new IllegalArgumentException(String.format(
                        "invalid kicks: piece=%s, from=%s, to=%s",
                        kickType.getPiece(), kickType.getRotateFrom(), kickType.getRotateTo()
                ));
            }
        }
    }

    @Override
    public MinoRotation get() {
        return createMinoRotation();
    }

    private MinoRotation createMinoRotation() {
        MapResult rightMap = createRightMap();
        assert rightMap.isSuccess();

        MapResult leftMap = createLeftMap();
        assert leftMap.isSuccess();

        if (supports180()) {
            MapResult rotate180Map = createRotate180Map();
            assert rotate180Map.isSuccess();

            return new MinoRotationImpl(rightMap.result, leftMap.result, rotate180Map.result);
        } else {
            return new MinoRotationNo180Impl(rightMap.result, leftMap.result);
        }
    }

    private boolean supports180() {
        return kickPatterns.size() == SIZE_WITH_180;
    }

    private MapResult createRightMap() {
        return createMap(Rotate::getRightRotate);
    }

    private MapResult createLeftMap() {
        return createMap(Rotate::getLeftRotate);
    }

    private MapResult createRotate180Map() {
        return createMap(Rotate::get180Rotate);
    }

    private MapResult createMap(Function<Rotate, Rotate> rotator) {
        EnumMap<Piece, EnumMap<Rotate, Pattern>> pieceMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate from : Rotate.values()) {
                Rotate to = rotator.apply(from);
                Optional<Pattern> pattern = kickPatterns.getPattern(piece, from, to);
                if (!pattern.isPresent()) {
                    return MapResult.failed(piece, from, to);
                }
                rotateMap.put(from, pattern.get());
            }
            pieceMap.put(piece, rotateMap);
        }
        return MapResult.success(pieceMap);
    }
}

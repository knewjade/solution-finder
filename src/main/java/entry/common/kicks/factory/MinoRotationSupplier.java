package entry.common.kicks.factory;

import core.mino.Piece;
import core.srs.*;
import entry.common.kicks.KickPatterns;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class MinoRotationSupplier implements Supplier<MinoRotation> {
    private final KickPatterns kickPatterns;

    MinoRotationSupplier(KickPatterns kickPatterns) {
        this.kickPatterns = kickPatterns;
        validate();
    }

    private void validate() {
        createRightMap().orElseThrow(IllegalArgumentException::new);
        createLeftMap().orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public MinoRotation get() {
        return createMinoRotation();
    }

    private MinoRotation createMinoRotation() {
        Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> rightMapOptional = createRightMap();
        Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> leftMapOptional = createLeftMap();
        Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> rotate180MapOptional = createRotate180Map();

        EnumMap<Piece, EnumMap<Rotate, Pattern>> rightMap = rightMapOptional.orElseThrow(IllegalStateException::new);
        EnumMap<Piece, EnumMap<Rotate, Pattern>> leftMap = leftMapOptional.orElseThrow(IllegalStateException::new);

        if (rotate180MapOptional.isPresent()) {
            return new MinoRotationImpl(rightMap, leftMap, rotate180MapOptional.get());
        } else {
            return new MinoRotationNo180Impl(rightMap, leftMap);
        }
    }

    private Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> createRightMap() {
        return createMap(Rotate::getRightRotate);
    }

    private Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> createLeftMap() {
        return createMap(Rotate::getLeftRotate);
    }

    private Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> createRotate180Map() {
        return createMap(Rotate::get180Rotate);
    }

    private Optional<EnumMap<Piece, EnumMap<Rotate, Pattern>>> createMap(Function<Rotate, Rotate> rotator) {
        EnumMap<Piece, EnumMap<Rotate, Pattern>> pieceMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate from : Rotate.values()) {
                Rotate to = rotator.apply(from);
                Optional<Pattern> pattern = kickPatterns.getPattern(piece, from, to);
                if (!pattern.isPresent()) {
                    return Optional.empty();
                }
                rotateMap.put(from, pattern.get());
            }
            pieceMap.put(piece, rotateMap);
        }
        return Optional.of(pieceMap);
    }
}

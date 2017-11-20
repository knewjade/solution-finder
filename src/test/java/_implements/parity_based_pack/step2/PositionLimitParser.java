package _implements.parity_based_pack.step2;

import core.mino.Piece;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import _implements.parity_based_pack.step1.DeltaLimit;
import _implements.parity_based_pack.step1.DeltaLimitedMino;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class PositionLimitParser {
    private final MinoFactory minoFactory;
    private final DeleteKeyParser deleteKeyParser;
    private final EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps;

    public PositionLimitParser(MinoFactory minoFactory, int maxClearLine) {
        this.minoFactory = minoFactory;
        this.deleteKeyParser = new DeleteKeyParser(minoFactory, maxClearLine);
        this.maps = initialMap();
    }

    private EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> initialMap() {
        EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps = new EnumMap<>(Piece.class);
        initialMapSZ(maps);
        initialMapO(maps);
        initialMapLJ(maps);
        initialMapT(maps);
        initialMapI(maps);
        return maps;
    }

    private void initialMapSZ(EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (Piece piece : Arrays.asList(Piece.S, Piece.Z)) {
            Rotate side = piece == Piece.S ? Rotate.Left : Rotate.Right;
            List<PositionLimitedMino> minos = Arrays.asList(
                    PositionLimitedMino.create(minoFactory.create(piece, Rotate.Spawn), PositionLimit.OddX),
                    PositionLimitedMino.create(minoFactory.create(piece, Rotate.Spawn), PositionLimit.EvenX),
                    PositionLimitedMino.create(minoFactory.create(piece, side), PositionLimit.OddX),
                    PositionLimitedMino.create(minoFactory.create(piece, side), PositionLimit.EvenX)
            );
            registerToMap(maps, piece, minos, DeltaLimit.Flat);
        }
    }

    private void registerToMap(EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps, Piece piece, List<PositionLimitedMino> minos, DeltaLimit deltaLimit) {
        List<FullLimitedMino> limitedMinos = minos.stream()
                .flatMap(positionLimitedMino -> {
                    Mino mino = positionLimitedMino.getMino();
                    PositionLimit positionLimit = positionLimitedMino.getPositionLimit();
                    List<DeleteKey> deleteKeys = deleteKeyParser.parse(mino);
                    return deleteKeys.stream()
                            .map(key -> FullLimitedMino.create(mino, positionLimit, key));
                })
                .collect(Collectors.toList());

        EnumMap<DeltaLimit, List<FullLimitedMino>> deltaMap = maps.computeIfAbsent(piece, blk -> new EnumMap<>(DeltaLimit.class));
        deltaMap.put(deltaLimit, limitedMinos);
    }

    private void initialMapO(EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        List<PositionLimitedMino> minos = Arrays.asList(
                PositionLimitedMino.create(minoFactory.create(Piece.O, Rotate.Spawn), PositionLimit.OddX),
                PositionLimitedMino.create(minoFactory.create(Piece.O, Rotate.Spawn), PositionLimit.EvenX)
        );
        registerToMap(maps, Piece.O, minos, DeltaLimit.Flat);

    }

    private void initialMapLJ(EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (Piece piece : Arrays.asList(Piece.L, Piece.J)) {
            for (DeltaLimit deltaLimit : Arrays.asList(DeltaLimit.EvenUp, DeltaLimit.OddUp)) {
                List<PositionLimitedMino> minos = createLJ(piece, deltaLimit);
                registerToMap(maps, piece, minos, deltaLimit);
            }
        }
    }

    private List<PositionLimitedMino> createLJ(Piece piece, DeltaLimit delta) {
        Mino spawn = minoFactory.create(piece, Rotate.Spawn);
        Mino reverse = minoFactory.create(piece, Rotate.Reverse);
        Mino left = minoFactory.create(piece, Rotate.Left);
        Mino right = minoFactory.create(piece, Rotate.Right);

        switch (delta) {
            case OddUp:
                return Arrays.asList(
                        PositionLimitedMino.create(spawn, PositionLimit.EvenX),
                        PositionLimitedMino.create(reverse, PositionLimit.EvenX),
                        PositionLimitedMino.create(left, PositionLimit.OddX),
                        PositionLimitedMino.create(right, PositionLimit.OddX)
                );
            case EvenUp:
                return Arrays.asList(
                        PositionLimitedMino.create(spawn, PositionLimit.OddX),
                        PositionLimitedMino.create(reverse, PositionLimit.OddX),
                        PositionLimitedMino.create(left, PositionLimit.EvenX),
                        PositionLimitedMino.create(right, PositionLimit.EvenX)
                );
        }
        throw new IllegalStateException("No reachable");
    }

    private void initialMapT(EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (DeltaLimit deltaLimit : Arrays.asList(DeltaLimit.EvenUp, DeltaLimit.OddUp, DeltaLimit.Flat)) {
            List<PositionLimitedMino> minos = createT(deltaLimit);
            registerToMap(maps, Piece.T, minos, deltaLimit);
        }
    }

    private List<PositionLimitedMino> createT(DeltaLimit delta) {
        Mino spawn = minoFactory.create(Piece.T, Rotate.Spawn);
        Mino reverse = minoFactory.create(Piece.T, Rotate.Reverse);
        Mino left = minoFactory.create(Piece.T, Rotate.Left);
        Mino right = minoFactory.create(Piece.T, Rotate.Right);

        switch (delta) {
            case OddUp:
                return Arrays.asList(
                        PositionLimitedMino.create(left, PositionLimit.OddX),
                        PositionLimitedMino.create(right, PositionLimit.OddX)
                );
            case EvenUp:
                return Arrays.asList(
                        PositionLimitedMino.create(left, PositionLimit.EvenX),
                        PositionLimitedMino.create(right, PositionLimit.EvenX)
                );
            case Flat:
                return Arrays.asList(
                        PositionLimitedMino.create(spawn, PositionLimit.OddX),
                        PositionLimitedMino.create(spawn, PositionLimit.EvenX),
                        PositionLimitedMino.create(reverse, PositionLimit.OddX),
                        PositionLimitedMino.create(reverse, PositionLimit.EvenX)
                );
        }
        throw new IllegalStateException("No reachable");
    }

    private void initialMapI(EnumMap<Piece, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (DeltaLimit deltaLimit : Arrays.asList(DeltaLimit.EvenUp, DeltaLimit.OddUp, DeltaLimit.Flat)) {
            List<PositionLimitedMino> minos = createI(deltaLimit);
            registerToMap(maps, Piece.I, minos, deltaLimit);
        }
    }

    private List<PositionLimitedMino> createI(DeltaLimit delta) {
        Mino spawn = minoFactory.create(Piece.I, Rotate.Spawn);
        Mino left = minoFactory.create(Piece.I, Rotate.Left);

        switch (delta) {
            case OddUp:
                return Collections.singletonList(PositionLimitedMino.create(left, PositionLimit.OddX));
            case EvenUp:
                return Collections.singletonList(PositionLimitedMino.create(left, PositionLimit.EvenX));
            case Flat:
                return Arrays.asList(
                        PositionLimitedMino.create(spawn, PositionLimit.OddX),
                        PositionLimitedMino.create(spawn, PositionLimit.EvenX)
                );
        }

        throw new IllegalStateException("No reachable");
    }

    public List<FullLimitedMino> parse(DeltaLimitedMino deltaLimitedMino) {
        return maps.get(deltaLimitedMino.getPiece()).get(deltaLimitedMino.getDeltaLimit());
    }
}

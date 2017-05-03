package _experimental.newfield.step2;

import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import _experimental.newfield.step1.DeltaLimit;
import _experimental.newfield.step1.DeltaLimitedMino;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class PositionLimitParser {
    private final MinoFactory minoFactory;
    private final DeleteKeyParser deleteKeyParser;
    private final EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps;

    public PositionLimitParser(MinoFactory minoFactory, int maxClearLine) {
        this.minoFactory = minoFactory;
        this.deleteKeyParser = new DeleteKeyParser(minoFactory, maxClearLine);
        this.maps = initialMap();
    }

    private EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> initialMap() {
        EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps = new EnumMap<>(Block.class);
        initialMapSZ(maps);
        initialMapO(maps);
        initialMapLJ(maps);
        initialMapT(maps);
        initialMapI(maps);
        return maps;
    }

    private void initialMapSZ(EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (Block block : Arrays.asList(Block.S, Block.Z)) {
            List<PositionLimitedMino> minos = Arrays.asList(
                    PositionLimitedMino.create(minoFactory.create(block, Rotate.Spawn), PositionLimit.OddX),
                    PositionLimitedMino.create(minoFactory.create(block, Rotate.Spawn), PositionLimit.EvenX),
                    PositionLimitedMino.create(minoFactory.create(block, Rotate.Left), PositionLimit.OddX),
                    PositionLimitedMino.create(minoFactory.create(block, Rotate.Left), PositionLimit.EvenX)
            );
            registerToMap(maps, block, minos, DeltaLimit.Flat);
        }
    }

    private void registerToMap(EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps, Block block, List<PositionLimitedMino> minos, DeltaLimit deltaLimit) {
        List<FullLimitedMino> limitedMinos = minos.stream()
                .flatMap(positionLimitedMino -> {
                    Mino mino = positionLimitedMino.getMino();
                    PositionLimit positionLimit = positionLimitedMino.getPositionLimit();
                    List<DeleteKey> deleteKeys = deleteKeyParser.parse(mino);
                    return deleteKeys.stream()
                            .map(key -> FullLimitedMino.create(mino, positionLimit, key));
                })
                .collect(Collectors.toList());

        EnumMap<DeltaLimit, List<FullLimitedMino>> deltaMap = maps.computeIfAbsent(block, blk -> new EnumMap<>(DeltaLimit.class));
        deltaMap.put(deltaLimit, limitedMinos);
    }

    private void initialMapO(EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        List<PositionLimitedMino> minos = Arrays.asList(
                PositionLimitedMino.create(minoFactory.create(Block.O, Rotate.Spawn), PositionLimit.OddX),
                PositionLimitedMino.create(minoFactory.create(Block.O, Rotate.Spawn), PositionLimit.EvenX)
        );
        registerToMap(maps, Block.O, minos, DeltaLimit.Flat);

    }

    private void initialMapLJ(EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (Block block : Arrays.asList(Block.L, Block.J)) {
            for (DeltaLimit deltaLimit : Arrays.asList(DeltaLimit.EvenUp, DeltaLimit.OddUp)) {
                List<PositionLimitedMino> minos = createLJ(block, deltaLimit);
                registerToMap(maps, block, minos, deltaLimit);
            }
        }
    }

    private List<PositionLimitedMino> createLJ(Block block, DeltaLimit delta) {
        Mino spawn = minoFactory.create(block, Rotate.Spawn);
        Mino reverse = minoFactory.create(block, Rotate.Reverse);
        Mino left = minoFactory.create(block, Rotate.Left);
        Mino right = minoFactory.create(block, Rotate.Right);

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

    private void initialMapT(EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (DeltaLimit deltaLimit : Arrays.asList(DeltaLimit.EvenUp, DeltaLimit.OddUp, DeltaLimit.Flat)) {
            List<PositionLimitedMino> minos = createT(deltaLimit);
            registerToMap(maps, Block.T, minos, deltaLimit);
        }
    }

    private List<PositionLimitedMino> createT(DeltaLimit delta) {
        Mino spawn = minoFactory.create(Block.T, Rotate.Spawn);
        Mino reverse = minoFactory.create(Block.T, Rotate.Reverse);
        Mino left = minoFactory.create(Block.T, Rotate.Left);
        Mino right = minoFactory.create(Block.T, Rotate.Right);

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

    private void initialMapI(EnumMap<Block, EnumMap<DeltaLimit, List<FullLimitedMino>>> maps) {
        for (DeltaLimit deltaLimit : Arrays.asList(DeltaLimit.EvenUp, DeltaLimit.OddUp, DeltaLimit.Flat)) {
            List<PositionLimitedMino> minos = createI(deltaLimit);
            registerToMap(maps, Block.I, minos, deltaLimit);
        }
    }

    private List<PositionLimitedMino> createI(DeltaLimit delta) {
        Mino spawn = minoFactory.create(Block.I, Rotate.Spawn);
        Mino left = minoFactory.create(Block.I, Rotate.Left);

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
        return maps.get(deltaLimitedMino.getBlock()).get(deltaLimitedMino.getDeltaLimit());
    }
}

package _implements.parity_based_pack;

import _implements.parity_based_pack.step1.ColumnParityLimitation;
import _implements.parity_based_pack.step1.DeltaLimitedMino;
import _implements.parity_based_pack.step1.EstimateBuilder;
import _implements.parity_based_pack.step2.FullLimitedMino;
import _implements.parity_based_pack.step2.PositionLimitParser;
import _implements.parity_based_pack.step3.CrossBuilder;
import common.buildup.BuildUp;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import concurrent.LockedReachableThreadLocal;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParityBasedPackSearcher {
    private final Field field;
    private final Field verifyField;
    private final int maxClearLine;

    public ParityBasedPackSearcher(Field field, int maxClearLine) {
        this(field, field.freeze(maxClearLine), maxClearLine);
    }

    public ParityBasedPackSearcher(Field field, Field verifyField, int maxClearLine) {
        this.field = field;
        this.verifyField = verifyField;
        this.maxClearLine = maxClearLine;
    }

    public Stream<List<OperationWithKey>> search(List<Block> usingBlocks) {
        // 準備
        MinoFactory minoFactory = new MinoFactory();
        PositionLimitParser positionLimitParser = new PositionLimitParser(minoFactory, maxClearLine);
        LockedReachableThreadLocal threadLocal = new LockedReachableThreadLocal(maxClearLine);

        ParityField parityField = new ParityField(field);
        BlockCounter blockCounter = new BlockCounter(usingBlocks);
        ColumnParityLimitation limitation = new ColumnParityLimitation(blockCounter, parityField, maxClearLine);

        return limitation.enumerate().parallelStream()
                .map(EstimateBuilder::create)
                .flatMap(Collection::stream)
                .flatMap(deltaLimitedMinos -> parseToSortedFullLimitedMinoStream(positionLimitParser, deltaLimitedMinos))
                .limit(Long.MAX_VALUE)  // parallelでの並列数をリセットする（同時実行数を増やす）
                .flatMap(sets -> new CrossBuilder(sets, field, maxClearLine).create().stream())
                .filter(operationWithKeys -> BuildUp.existsValidBuildPattern(verifyField, operationWithKeys, maxClearLine, threadLocal.get()));
    }

    private Stream<? extends List<List<FullLimitedMino>>> parseToSortedFullLimitedMinoStream(PositionLimitParser positionLimitParser, List<DeltaLimitedMino> deltaLimitedMinos) {
        // Parse DeltaLimitedMinos to FullLimitedMino
        List<List<FullLimitedMino>> collect = deltaLimitedMinos.stream()
                .map(positionLimitParser::parse)
                .collect(Collectors.toList());

        // 候補数が小さい順  // 同種のブロックを固めるため
        List<Block> priority = collect.stream()
                .sorted(Comparator.comparingInt(List::size))
                .map(fullLimitedMinos -> fullLimitedMinos.get(0).getMino().getBlock())
                .collect(Collectors.toList());

        // ソートする
        collect.sort((o1, o2) -> {
            int compare = Integer.compare(priority.indexOf(o1.get(0).getMino().getBlock()), priority.indexOf(o2.get(0).getMino().getBlock()));
            if (compare != 0)
                return compare;
            return -Integer.compare(o1.size(), o2.size());
        });

        return Stream.of(collect);
    }
}

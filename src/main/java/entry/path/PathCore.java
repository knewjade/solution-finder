package entry.path;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.pattern.BlocksGenerator;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import searcher.pack.SizedBit;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathCore {
    private final List<Result> candidates;
    private final HashSet<LongBlocks> validPieces;

    PathCore(List<String> patterns, PackSearcher searcher, int maxDepth, boolean isUsingHold) throws ExecutionException, InterruptedException {
        this.candidates = searcher.toList();

        // ホールド込みで有効な手順を列挙する
        this.validPieces = getValidPieces(patterns, maxDepth, isUsingHold);
    }

    private HashSet<LongBlocks> getValidPieces(List<String> patterns, int maxDepth, boolean isUsingHold) {
        BlocksGenerator blocksGenerator = new BlocksGenerator(patterns);

        // 必要以上にミノを使っている場合はリストを削減する
        Function<List<Block>, List<Block>> reduceBlocks = Function.identity();
        if (maxDepth < blocksGenerator.getDepth())
            reduceBlocks = blocks -> blocks.subList(0, maxDepth);

        if (isUsingHold) {
            return blocksGenerator.blocksParallelStream()
                    .map(Blocks::getBlocks)
                    .flatMap(blocks -> OrderLookup.forwardBlocks(blocks, maxDepth).stream())
                    .collect(Collectors.toCollection(HashSet::new))
                    .parallelStream()
                    .map(StackOrder::toList)
                    .map(reduceBlocks)
                    .map(LongBlocks::new)
                    .collect(Collectors.toCollection(HashSet::new));
        } else {
            return blocksGenerator.blocksParallelStream()
                    .map(Blocks::getBlocks)
                    .map(reduceBlocks)
                    .map(LongBlocks::new)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }

    List<PathPair> run(Field field, SizedBit sizedBit) {
        // uniqueの作成
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(sizedBit.getHeight());
        return candidates.parallelStream()
                .map(result -> {
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    BuildUpStream buildUpStream = threadLocal.get();
                    HashSet<LongBlocks> pieces = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getMino)
                                    .map(Mino::getBlock)
                                    .collect(Collectors.toList())
                            )
                            .map(LongBlocks::new)
                            .filter(validPieces::contains)
                            .collect(Collectors.toCollection(HashSet::new));

                    if (pieces.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    return new PathPair(result, pieces);
                })
                .filter(pathPair -> pathPair != PathPair.EMPTY_PAIR)
                .collect(Collectors.toList());
    }
}

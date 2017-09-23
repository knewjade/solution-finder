package entry.path;

import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.order.OrderLookup;
import common.order.ReverseOrderLookUp;
import common.order.StackOrder;
import common.pattern.BlocksGenerator;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import entry.path.output.FumenParser;
import searcher.pack.SizedBit;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PathCore {
    private final PackSearcher searcher;
    private final FumenParser fumenParser;
    private final ThreadLocal<BuildUpStream> buildUpStreamTh1readLocal;
    private final boolean isReduced;
    private final boolean isUsingHold;
    private final int maxDepth;
    private final HashSet<LongBlocks> validPieces;
    private final HashSet<LongBlocks> allPieces;

    PathCore(List<String> patterns, PackSearcher searcher, int maxDepth, boolean isUsingHold, FumenParser fumenParser, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal) {
        this.searcher = searcher;
        this.fumenParser = fumenParser;
        this.buildUpStreamTh1readLocal = buildUpStreamThreadLocal;
        BlocksGenerator blocksGenerator = new BlocksGenerator(patterns);
        this.isReduced = isReducedPieces(blocksGenerator, maxDepth, isUsingHold);
        this.isUsingHold = isUsingHold;
        this.maxDepth = maxDepth;
        this.allPieces = getAllPieces(blocksGenerator, maxDepth, isReduced);
        this.validPieces = getValidPieces(blocksGenerator, allPieces, maxDepth, isReduced);
    }

    private boolean isReducedPieces(BlocksGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        return isUsingHold && maxDepth < blocksGenerator.getDepth();
    }

    private HashSet<LongBlocks> getAllPieces(BlocksGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        if (isUsingHold && maxDepth + 1 < blocksGenerator.getDepth()) {
            return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth + 1);
        } else if (!isUsingHold && maxDepth < blocksGenerator.getDepth()) {
            return toReducedHashSetWithoutHold(blocksGenerator.blocksStream(), maxDepth);
        } else {
            return toDirectHashSet(blocksGenerator.blocksStream());
        }
    }

    private HashSet<LongBlocks> getValidPieces(BlocksGenerator blocksGenerator, HashSet<LongBlocks> allPieces, int maxDepth, boolean isUsingHold) {
        if (isReducedPieces(blocksGenerator, maxDepth, isUsingHold)) {
            return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth);
        } else {
            return allPieces;
        }
    }

    private HashSet<LongBlocks> toReducedHashSetWithHold(Stream<? extends Blocks> blocksStream, int maxDepth) {
        return blocksStream.parallel()
                .map(Blocks::getBlocks)
                .flatMap(blocks -> OrderLookup.forwardBlocks(blocks, maxDepth).stream())
                .collect(Collectors.toCollection(HashSet::new))
                .parallelStream()
                .map(StackOrder::toList)
                .map(blocks -> blocks.subList(0, maxDepth))
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongBlocks> toReducedHashSetWithoutHold(Stream<? extends Blocks> blocksStream, int maxDepth) {
        return blocksStream.parallel()
                .map(Blocks::getBlocks)
                .map(blocks -> blocks.subList(0, maxDepth))
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongBlocks> toDirectHashSet(Stream<? extends Blocks> blocksStream) {
        return blocksStream.parallel()
                .map(Blocks::getBlocks)
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    List<PathPair> run(Field field, SizedBit sizedBit) throws ExecutionException, InterruptedException {
        List<Result> candidates = searcher.toList();
        int maxClearLine = sizedBit.getHeight();
        return candidates.parallelStream()
                .map(result -> {
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));

                    // 地形の中で組むことができるoperationsを一つ作成
                    BuildUpStream buildUpStream = buildUpStreamTh1readLocal.get();
                    List<OperationWithKey> sampleOperations = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());

                    // 地形の中で組むことができるものがないときはスキップ
                    if (sampleOperations.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 地形の中で組むことができるSetを作成
                    HashSet<LongBlocks> piecesSolution = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getMino)
                                    .map(Mino::getBlock)
                                    .collect(Collectors.toList())
                            )
                            .map(LongBlocks::new)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    HashSet<LongBlocks> piecesPattern = getPiecesPattern(piecesSolution);

                    // 探索シーケンスの中で組むことができるものがないときはスキップ
                    if (piecesPattern.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 譜面の作成
                    String fumen = fumenParser.parse(sampleOperations, field, maxClearLine);

                    return new PathPair(result, piecesSolution, piecesPattern, fumen, new ArrayList<>(sampleOperations));
                })
                .filter(pathPair -> pathPair != PathPair.EMPTY_PAIR)
                .collect(Collectors.toList());
    }

    List<PathPair> run(Field field, SizedBit sizedBit, BlockField blockField) throws ExecutionException, InterruptedException {
        int maxClearLine = sizedBit.getHeight();

        List<Result> candidates = searcher.stream(resultStream -> {
            return resultStream
                    .filter(result -> {
                        LinkedList<OperationWithKey> operations = result.getMemento()
                                .getOperationsStream(sizedBit.getWidth())
                                .collect(Collectors.toCollection(LinkedList::new));

                        BlockField blockField2 = new BlockField(maxClearLine);
                        operations.forEach(operation -> {
                            Field field1 = createField(operation, maxClearLine);
                            blockField2.merge(field1, operation.getMino().getBlock());
                        });

                        return blockField2.containsAll(blockField);
                    })
                    .collect(Collectors.toList());
        });

        return candidates.parallelStream()
                .map(result -> {
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));

                    // 地形の中で組むことができるoperationsを一つ作成
                    BuildUpStream buildUpStream = buildUpStreamTh1readLocal.get();
                    List<OperationWithKey> sampleOperations = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());

                    // 地形の中で組むことができるものがないときはスキップ
                    if (sampleOperations.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 地形の中で組むことができるSetを作成
                    HashSet<LongBlocks> piecesSolution = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getMino)
                                    .map(Mino::getBlock)
                                    .collect(Collectors.toList())
                            )
                            .map(LongBlocks::new)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    HashSet<LongBlocks> piecesPattern = getPiecesPattern(piecesSolution);

                    // 探索シーケンスの中で組むことができるものがないときはスキップ
                    if (piecesPattern.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 譜面の作成
                    String fumen = fumenParser.parse(sampleOperations, field, maxClearLine);

                    return new PathPair(result, piecesSolution, piecesPattern, fumen, new ArrayList<>(sampleOperations));
                })
                .filter(pathPair -> pathPair != PathPair.EMPTY_PAIR)
                .collect(Collectors.toList());
    }

    private Field createField(OperationWithKey key, int maxClearLine) {
        Mino mino = key.getMino();
        Field test = FieldFactory.createField(maxClearLine);
        test.put(mino, key.getX(), key.getY());
        test.insertWhiteLineWithKey(key.getNeedDeletedKey());
        return test;
    }

    private HashSet<LongBlocks> getPiecesPattern(HashSet<LongBlocks> piecesSolution) {
        if (isReduced) {
            // allとvalidが異なる
            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(maxDepth, maxDepth + 1);

            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .flatMap(blocks -> {
                        return reverseOrderLookUp.parse(blocks.getBlocks())
                                .map(stream -> stream.collect(Collectors.toCollection(ArrayList::new)))
                                .flatMap(blocksWithHold -> {
                                    int nullIndex = blocksWithHold.indexOf(null);
                                    if (nullIndex < 0)
                                        return Stream.of(new LongBlocks(blocksWithHold));

                                    Stream.Builder<LongBlocks> builder = Stream.builder();
                                    for (Block block : Block.values()) {
                                        blocksWithHold.set(nullIndex, block);
                                        builder.accept(new LongBlocks(blocksWithHold));
                                    }
                                    return builder.build();
                                });
                    })
                    .filter(allPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));
        } else if (isUsingHold) {
            // allとvalidが同じだが、ホールドが使える
            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(maxDepth, maxDepth);

            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .flatMap(blocks -> {
                        return reverseOrderLookUp.parse(blocks.getBlocks())
                                .map(stream -> stream.collect(Collectors.toCollection(ArrayList::new)))
                                .flatMap(blocksWithHold -> {
                                    int nullIndex = blocksWithHold.indexOf(null);
                                    if (nullIndex < 0)
                                        return Stream.of(new LongBlocks(blocksWithHold));

                                    Stream.Builder<LongBlocks> builder = Stream.builder();
                                    for (Block block : Block.values()) {
                                        blocksWithHold.set(nullIndex, block);
                                        builder.accept(new LongBlocks(blocksWithHold));
                                    }
                                    return builder.build();
                                });
                    })
                    .filter(allPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));

        } else {
            // allとvalidが同じで、ホールドも使えない
            // そのまま絞り込みだけ実施
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }
}


package entry.path;

import common.SyntaxException;
import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.order.OrderLookup;
import common.order.ReverseOrderLookUp;
import common.order.StackOrder;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.Piece;
import entry.path.output.FumenParser;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.task.PerfectPackSearcher;
import searcher.pack.task.Result;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PathCore {
    private final PerfectPackSearcher searcher;
    private final FumenParser fumenParser;
    private final ThreadLocal<BuildUpStream> buildUpStreamThreadLocal;
    private final boolean isHoldReduced;
    private final boolean isUsingHold;
    private final int maxDepth;
    private final HashSet<LongPieces> validPieces;
    private final HashSet<LongPieces> allPieces;

    PathCore(List<String> patterns, PerfectPackSearcher searcher, int maxDepth, boolean isUsingHold, FumenParser fumenParser, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal) throws SyntaxException {
        this.searcher = searcher;
        this.fumenParser = fumenParser;
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
        PatternGenerator blocksGenerator = new LoadedPatternGenerator(patterns);
        this.isHoldReduced = isHoldReducedPieces(blocksGenerator, maxDepth, isUsingHold);
        this.isUsingHold = isUsingHold;
        this.maxDepth = maxDepth;
        this.allPieces = getAllPieces(blocksGenerator, maxDepth, isUsingHold);
        this.validPieces = getValidPieces(blocksGenerator, allPieces, maxDepth, isHoldReduced);
    }

    private boolean isHoldReducedPieces(PatternGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        return isUsingHold && maxDepth < blocksGenerator.getDepth();
    }

    private HashSet<LongPieces> getAllPieces(PatternGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        if (isUsingHold) {
            // ホールドあり
            if (maxDepth < blocksGenerator.getDepth()) {
                // Reduceあり  // isHoldReduceの対象
                return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth + 1);
            } else {
                // 場所の交換のみ
                return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth);
            }
        } else {
            // ホールドなし
            if (maxDepth < blocksGenerator.getDepth()) {
                // Reduceあり
                return toReducedHashSetWithoutHold(blocksGenerator.blocksStream(), maxDepth);
            } else {
                // そのまま
                return toDirectHashSet(blocksGenerator.blocksStream());
            }
        }
    }

    private HashSet<LongPieces> getValidPieces(PatternGenerator blocksGenerator, HashSet<LongPieces> allPieces, int maxDepth, boolean isHoldReduced) {
        if (isHoldReduced) {
            // パフェ時に使用ミノが少なくなるケースのため改めて専用のSetを作る
            return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth);
        } else {
            return allPieces;
        }
    }

    private HashSet<LongPieces> toReducedHashSetWithHold(Stream<? extends Pieces> blocksStream, int maxDepth) {
        return blocksStream.parallel()
                .map(Pieces::getPieces)
                .flatMap(blocks -> OrderLookup.forwardBlocks(blocks, maxDepth).stream())
                .collect(Collectors.toCollection(HashSet::new))
                .parallelStream()
                .map(StackOrder::toList)
                .map(blocks -> blocks.subList(0, maxDepth))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongPieces> toReducedHashSetWithoutHold(Stream<? extends Pieces> blocksStream, int maxDepth) {
        return blocksStream.parallel()
                .map(Pieces::getPieces)
                .map(blocks -> blocks.subList(0, maxDepth))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongPieces> toDirectHashSet(Stream<? extends Pieces> blocksStream) {
        return blocksStream.parallel()
                .map(Pieces::getPieces)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    List<PathPair> run(Field field, SizedBit sizedBit) throws ExecutionException, InterruptedException {
        List<Result> candidates = searcher.toList();
        int maxClearLine = sizedBit.getHeight();
        return getPathPairs(field, sizedBit, maxClearLine, candidates);
    }

    private List<PathPair> getPathPairs(Field field, SizedBit sizedBit, int maxClearLine, List<Result> results) {
        return results.parallelStream()
                .map(result -> {
                    LinkedList<MinoOperationWithKey> operations = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toCollection(LinkedList::new));

                    // 地形の中で組むことができるoperationsを一つ作成
                    BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                    List<MinoOperationWithKey> sampleOperations = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());

                    // 地形の中で組むことができるものがないときはスキップ
                    if (sampleOperations.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 地形の中で組むことができるSetを作成
                    HashSet<LongPieces> piecesSolution = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getPiece)
                                    .collect(Collectors.toList())
                            )
                            .map(LongPieces::new)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    HashSet<LongPieces> piecesPattern = getPiecesPattern(piecesSolution);

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
                        LinkedList<MinoOperationWithKey> operations = result.getMemento()
                                .getSeparableMinoStream(sizedBit.getWidth())
                                .map(SeparableMino::toMinoOperationWithKey)
                                .collect(Collectors.toCollection(LinkedList::new));

                        BlockField mergedField = new BlockField(maxClearLine);
                        operations.forEach(operation -> {
                            Field operationField = createField(operation, maxClearLine);
                            mergedField.merge(operationField, operation.getPiece());
                        });

                        return mergedField.containsAll(blockField);
                    })
                    .collect(Collectors.toList());
        });

        return getPathPairs(field, sizedBit, maxClearLine, candidates);
    }

    private Field createField(MinoOperationWithKey key, int maxClearLine) {
        Mino mino = key.getMino();
        Field test = FieldFactory.createField(maxClearLine);
        test.put(mino, key.getX(), key.getY());
        test.insertWhiteLineWithKey(key.getNeedDeletedKey());
        return test;
    }

    private HashSet<LongPieces> getPiecesPattern(HashSet<LongPieces> piecesSolution) {
        if (isHoldReduced) {
            // allとvalidが異なる
            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(maxDepth, maxDepth + 1);
            return getPiecesPattern(piecesSolution, reverseOrderLookUp);
        } else if (isUsingHold) {
            // allとvalidが同じだが、ホールドが使える
            ReverseOrderLookUp reverseOrderLookUp = new ReverseOrderLookUp(maxDepth, maxDepth);
            return getPiecesPattern(piecesSolution, reverseOrderLookUp);
        } else {
            // allとvalidが同じで、ホールドも使えない
            // そのまま絞り込みだけ実施
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }

    private HashSet<LongPieces> getPiecesPattern(HashSet<LongPieces> piecesSolution, ReverseOrderLookUp reverseOrderLookUp) {
        return piecesSolution.stream()
                .filter(validPieces::contains)
                .flatMap(blocks -> {
                    return reverseOrderLookUp.parse(blocks.getPieces())
                            .map(stream -> stream.collect(Collectors.toCollection(ArrayList::new)))
                            .flatMap(blocksWithHold -> {
                                int nullIndex = blocksWithHold.indexOf(null);
                                if (nullIndex < 0)
                                    return Stream.of(new LongPieces(blocksWithHold));

                                Stream.Builder<LongPieces> builder = Stream.builder();
                                for (Piece piece : Piece.values()) {
                                    blocksWithHold.set(nullIndex, piece);
                                    builder.accept(new LongPieces(blocksWithHold));
                                }
                                return builder.build();
                            });
                })
                .filter(allPieces::contains)
                .collect(Collectors.toCollection(HashSet::new));
    }
}


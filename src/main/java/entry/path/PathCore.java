package entry.path;

import common.ValidPiecesPool;
import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.order.ReverseOrderLookUp;
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
    private final boolean isUsingHold;
    private final ValidPiecesPool piecesPool;
    private final ReverseOrderLookUp reverseOrderLookUpReduceDepth;
    private final ReverseOrderLookUp reverseOrderLookUpSameDepth;

    PathCore(PerfectPackSearcher searcher, int maxDepth, boolean isUsingHold, FumenParser fumenParser, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, ValidPiecesPool piecesPool) {
        this.searcher = searcher;
        this.fumenParser = fumenParser;
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
        this.isUsingHold = isUsingHold;
        this.piecesPool = piecesPool;

        this.reverseOrderLookUpReduceDepth = new ReverseOrderLookUp(maxDepth, maxDepth + 1);
        this.reverseOrderLookUpSameDepth = new ReverseOrderLookUp(maxDepth, maxDepth);
    }

    List<PathPair> run(Field field, SizedBit sizedBit) throws ExecutionException, InterruptedException {
        List<Result> candidates = searcher.toList();
        int maxClearLine = sizedBit.getHeight();
        return candidates.parallelStream()
                .map(result -> {
                    LinkedList<MinoOperationWithKey> operations = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toCollection(LinkedList::new));

                    // 地形の中で組むことができるoperationsをすべてリスト化する
                    BuildUpStream buildUpStream2 = buildUpStreamThreadLocal.get();
                    List<List<MinoOperationWithKey>> validOperaions = buildUpStream2.existsValidBuildPatternDirectly(field, operations)
                            .collect(Collectors.toList());

                    // 地形の中で組むことができるものがないときはスキップ
                    if (validOperaions.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 地形の中で組むことができるSetを作成
                    HashSet<LongPieces> piecesSolution = validOperaions.stream()
                            .map(
                                    operationWithKeys -> operationWithKeys.stream().map(OperationWithKey::getPiece)
                            )
                            .map(LongPieces::new)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    HashSet<LongPieces> piecesPattern = getPiecesPattern(piecesSolution);

                    // 探索シーケンスの中で組むことができるものがないときはスキップ
                    if (piecesPattern.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 探索シーケンスの中でテト譜にするoperationsを選択する
                    HashSet<LongPieces> validPieces = piecesPool.getValidPieces();
                    List<MinoOperationWithKey> operationsToUrl = validOperaions.stream()
                            .filter(o -> {
                                return validPieces.contains(new LongPieces(o.stream().map(Operation::getPiece)));
                            })
                            .findFirst()
                            .orElse(Collections.emptyList());

                    // 譜面の作成
                    String fumen = fumenParser.parse(operationsToUrl, field, maxClearLine);

                    long numOfValidSpecifiedPatterns = getNumOfValidSpecifiedPatterns(piecesSolution);

                    return new PathPair(result, piecesSolution, piecesPattern, fumen, new ArrayList<>(operationsToUrl), validPieces, numOfValidSpecifiedPatterns);
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

        return candidates.stream()
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

                    HashSet<LongPieces> validPieces = piecesPool.getValidPieces();
                    long numOfValidSpecifiedPatterns = getNumOfValidSpecifiedPatterns(piecesSolution);

                    return new PathPair(result, piecesSolution, piecesPattern, fumen, new ArrayList<>(sampleOperations), validPieces, numOfValidSpecifiedPatterns);
                })
                .filter(pathPair -> pathPair != PathPair.EMPTY_PAIR)
                .collect(Collectors.toList());
    }

    private Field createField(MinoOperationWithKey key, int maxClearLine) {
        Mino mino = key.getMino();
        Field test = FieldFactory.createField(maxClearLine);
        test.put(mino, key.getX(), key.getY());
        test.insertWhiteLineWithKey(key.getNeedDeletedKey());
        return test;
    }

    private HashSet<LongPieces> getPiecesPattern(HashSet<LongPieces> piecesSolution) {
        HashSet<LongPieces> validPieces = piecesPool.getValidPieces();
        HashSet<LongPieces> allPieces = piecesPool.getAllPieces();

        if (piecesPool.isHoldReduced()) {
            // allとvalidが異なる
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .flatMap(blocks -> {
                        return reverseOrderLookUpReduceDepth.parse(blocks.getPieces())
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
        } else if (isUsingHold) {
            // allとvalidが同じだが、ホールドが使える
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .flatMap(blocks -> {
                        return reverseOrderLookUpSameDepth.parse(blocks.getPieces())
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

        } else {
            // allとvalidが同じで、ホールドも使えない
            // そのまま絞り込みだけ実施
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }

    private long getNumOfValidSpecifiedPatterns(HashSet<LongPieces> piecesSolution) {
        HashSet<LongPieces> allSpecifiedPieces = piecesPool.getAllSpecifiedPieces();

        if (piecesPool.isHoldReduced()) {
            // ホールドによって1ミノ少ない
            // 1ミノ増やして、入力パターンと同じ数にする
            HashSet<LongPieces> sequencePiecesSolution = piecesSolution.stream()
                    .map(Pieces::getPieces)
                    .flatMap(reverseOrderLookUpReduceDepth::parseAndExpand)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            return allSpecifiedPieces.stream()
                    .filter(sequencePiecesSolution::contains)
                    .count();
        } else if (isUsingHold) {
            // ホールドはできるが、ミノ数は入力パターンと同じ
            // そのため、ミノ順の並び替えのみ
            HashSet<LongPieces> sequencePiecesSolution = piecesSolution.stream()
                    .map(Pieces::getPieces)
                    .flatMap(reverseOrderLookUpSameDepth::parseAndExpand)
                    .map(LongPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));

            return allSpecifiedPieces.stream()
                    .filter(sequencePiecesSolution::contains)
                    .count();
        } else {
            // ホールドはできないため、ミノ数は入力パターンと同じ
            // そのまま絞り込む
            return allSpecifiedPieces.stream()
                    .filter(piecesSolution::contains)
                    .count();
        }
    }
}
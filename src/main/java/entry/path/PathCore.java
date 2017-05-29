package entry.path;

import common.buildup.BuildUpListUp;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.datastore.pieces.NumberPieces;
import common.datastore.pieces.Pieces;
import common.order.ListPieces;
import common.order.OrderLookup;
import common.pattern.PiecesGenerator;
import core.field.Field;
import core.mino.Mino;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

class PathCore {
    private final List<Result> candidates;
    private final HashSet<NumberPieces> validPieces;

    private List<Pair<Result, HashSet<NumberPieces>>> unique = null;
    private LinkedList<Pair<Result, HashSet<NumberPieces>>> minimal = null;

    PathCore(List<String> patterns, PackSearcher searcher, int maxDepth, boolean isUsingHold) throws ExecutionException, InterruptedException {
        this.candidates = searcher.collect(Collectors.toList());

        // ホールド込みで有効な手順を列挙する
        this.validPieces = getCollect(patterns, maxDepth, isUsingHold);
    }

    private HashSet<NumberPieces> getCollect(List<String> patterns, int maxDepth, boolean isUsingHold) {
        if (isUsingHold) {
            return new PiecesGenerator(patterns).stream()
                    .parallel()
                    .map(Pieces::getBlocks)
                    .flatMap(blocks -> OrderLookup.forward(blocks, maxDepth).stream())
                    .collect(Collectors.toCollection(HashSet::new))
                    .parallelStream()
                    .map(ListPieces::getBlocks)
                    .map(NumberPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));
        } else {
            return new PiecesGenerator(patterns).stream()
                    .parallel()
                    .map(Pieces::getBlocks)
                    .map(NumberPieces::new)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }

    void runUnique(Field field, int maxClearLine) {
        // uniqueの作成
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(maxClearLine);
        List<Pair<Result, HashSet<NumberPieces>>> unique = candidates.parallelStream()
                .map(result -> {
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperations();

                    BuildUpListUp buildUpListUp = threadLocal.get();
                    HashSet<NumberPieces> pieces = buildUpListUp.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getMino)
                                    .map(Mino::getBlock)
                                    .collect(Collectors.toList())
                            )
                            .map(NumberPieces::new)
                            .filter(validPieces::contains)
                            .collect(Collectors.toCollection(HashSet::new));
                    return new Pair<>(result, pieces);
                })
                .filter(pair -> !pair.getValue().isEmpty())
                .collect(Collectors.toList());
        this.unique = unique;
    }

    public void runMinimal() {
        // 他のパターンではカバーできないものだけを列挙する
        LinkedList<Pair<Result, HashSet<NumberPieces>>> minimal = new LinkedList<>();
        for (Pair<Result, HashSet<NumberPieces>> pair : unique) {
            HashSet<NumberPieces> canBuildBlocks = pair.getValue();
            boolean isSetNeed = true;
            LinkedList<Pair<Result, HashSet<NumberPieces>>> nextMasters = new LinkedList<>();

            // すでに登録済みのパターンでカバーできるか確認
            while (!minimal.isEmpty()) {
                Pair<Result, HashSet<NumberPieces>> targetPair = minimal.pollFirst();
                Set<NumberPieces> registeredBlocks = targetPair.getValue();

                if (registeredBlocks.size() < canBuildBlocks.size()) {
                    // 新しいパターンの方が多く対応できる  // 新パターンが残る
                    HashSet<NumberPieces> newTarget = new HashSet<>(registeredBlocks);
                    newTarget.removeAll(canBuildBlocks);

                    // 新パターンでも対応できないパターンがあるときは残す
                    if (newTarget.size() != 0)
                        nextMasters.add(targetPair);
                } else if (canBuildBlocks.size() < registeredBlocks.size()) {
                    // 登録済みパターンの方が多く対応できる
                    HashSet<NumberPieces> newSet = new HashSet<>(canBuildBlocks);
                    newSet.removeAll(registeredBlocks);

                    // 登録済みパターンを残す
                    nextMasters.add(targetPair);

                    if (newSet.size() == 0) {
                        // 上位のパターンが存在するので新パターンはいらない
                        // 残りの登録済みパターンは無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(minimal);
                        break;
                    }
                } else {
                    // 新パターンと登録済みパターンが対応できる数は同じ
                    HashSet<NumberPieces> newSet = new HashSet<>(canBuildBlocks);
                    newSet.retainAll(registeredBlocks);

                    // 登録済みパターンを残す
                    nextMasters.add(targetPair);

                    if (newSet.size() == registeredBlocks.size()) {
                        // 完全に同一の対応パターンなので新パターンはいらない
                        // 残りの登録済みパターンは無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(minimal);
                        break;
                    }
                }
            }

            // 新パターンが必要
            if (isSetNeed)
                nextMasters.add(pair);

            minimal = nextMasters;
        }

        this.minimal = minimal;
    }

    List<Pair<Result, HashSet<NumberPieces>>> getUnique() {
        return unique;
    }

    List<Pair<Result, HashSet<NumberPieces>>> getMinimal() {
        return minimal;
    }
}

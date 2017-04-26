package entry.path;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.invoker.Pair;
import concurrent.checkmate.CheckmateNoHoldThreadLocal;
import concurrent.checkmate.invoker.no_hold.ConcurrentCheckmateCommonInvoker;
import concurrent.full_checkmate.FullCheckmateNoHoldThreadLocal;
import concurrent.full_checkmate.invoker.no_hold.ConcurrentFullCheckmateNoHoldInvoker;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.searching_pieces.EnumeratePiecesCore;
import entry.searching_pieces.HoldBreakEnumeratePieces;
import entry.searching_pieces.NormalEnumeratePieces;
import misc.BlockField;
import misc.Build;
import misc.OperationWithKey;
import misc.iterable.PermutationIterable;
import misc.pattern.PiecesGenerator;
import searcher.common.Operation;
import searcher.common.Operations;
import searcher.common.Result;
import searcher.common.action.Action;
import searcher.common.validator.FullValidator;
import searcher.common.validator.PathFullValidator;
import searcher.common.validator.PerfectValidator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;

class PathCore {
    private final ConcurrentCheckmateCommonInvoker invoker;
    private final ConcurrentFullCheckmateNoHoldInvoker invokerFull;

    private TreeSet<Operations> allUniqueOperations;
    private List<Operations> uniqueOperations;

    static EnumeratePiecesCore createEnumeratePiecesCore(PiecesGenerator generator, int maxDepth, boolean isUsingHold) throws IOException {
        if (isUsingHold) {
            return new HoldBreakEnumeratePieces(generator, maxDepth);
        } else {
            return new NormalEnumeratePieces(generator, maxDepth, false);
        }
    }

    PathCore(int maxClearLine, ExecutorService executorService, int taskSplitCount) {
        CheckmateNoHoldThreadLocal<Action> checkmateThreadLocal = new CheckmateNoHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        this.invoker = new ConcurrentCheckmateCommonInvoker(executorService, candidateThreadLocal, checkmateThreadLocal, taskSplitCount);

        FullCheckmateNoHoldThreadLocal<Action> fullCheckmateThreadLocal = new FullCheckmateNoHoldThreadLocal<>();
        this.invokerFull = new ConcurrentFullCheckmateNoHoldInvoker(executorService, candidateThreadLocal, fullCheckmateThreadLocal);
    }

    void run(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        // 探索パターンをホールドなしで列挙
        // 同じ地形は統合される
        List<Pair<List<Block>, List<Result>>> allMergedPatterns = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // パフェできるホールドなしパターンから派生するパターンをすべて列挙
        // 途中で同じ地形になる統合されたパターンを探索
        PerfectValidator perfectValidator = new PerfectValidator();
        MinoFactory minoFactory = new MinoFactory();

        // マージされた探索結果から派生するパスを探索する準備
        List<Pair<List<Operation>, FullValidator>> pathCheckList = createPathCheckList(field, maxClearLine, allMergedPatterns, perfectValidator, minoFactory);

        // 統合される瞬間までのパスを探索する
        List<Pair<List<Operation>, List<Result>>> allDerivationPath = invokerFull.search(field, pathCheckList, maxClearLine, maxDepth);

        // Operationsに変換し、重複を取り除く  // 全パス
        this.allUniqueOperations = parseToUniqueOperations(allDerivationPath);

        // Blockごとに置き分けたフィールド群に変換する  // 同一ミノ配置を取り除く
        TreeSet<BlockFieldOperations> blockFieldOperationsList = createBlockFields(field, maxClearLine, minoFactory, allUniqueOperations);

        // 操作順に並び替える
        this.uniqueOperations = blockFieldOperationsList.stream()
                .sorted(Comparator.comparing(BlockFieldOperations::getOperations))
                .map(BlockFieldOperations::getOperations)
                .collect(Collectors.toList());

        // さらに整理
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        LinkedList<Pair<Operations, Set<List<Block>>>> masters = new LinkedList<>();
        for (BlockFieldOperations blockFieldOperations : blockFieldOperationsList) {
            Operations operations = blockFieldOperations.getOperations();
            System.out.println(operations);
            List<OperationWithKey> operationWithKeys = Build.createOperationWithKeys(field, operations, minoFactory, maxClearLine);

            HashSet<List<Block>> set = new HashSet<>();
            PermutationIterable<OperationWithKey> permutationIterable = new PermutationIterable<>(operationWithKeys, operationWithKeys.size());
            for (List<OperationWithKey> targetCheckOperationsWithKey : permutationIterable) {
                boolean cansBuild = Build.cansBuild(field, targetCheckOperationsWithKey, maxClearLine, reachable);
                if (cansBuild) {
                    List<Block> blocks = targetCheckOperationsWithKey.stream().map(o -> o.getMino().getBlock()).collect(Collectors.toList());
                    set.add(blocks);
                }
            }
//            System.out.println(set);

            // 比較
            Pair<Operations, Set<List<Block>>> pair = new Pair<>(blockFieldOperations.getOperations(), set);
            LinkedList<Pair<Operations, Set<List<Block>>>> nextMasters = new LinkedList<>();
            boolean isSetNeed = true;

            while (!masters.isEmpty()) {
                Pair<Operations, Set<List<Block>>> targetPair = masters.pollFirst();
                Set<List<Block>> target = targetPair.getValue();

                if (target.size() < set.size()) {
                    // setがmasterになる
                    HashSet<List<Block>> newTarget = new HashSet<>(target);
                    newTarget.removeAll(set);

                    if (newTarget.size() != 0) {
                        // targetも残る
                        nextMasters.add(targetPair);
                    }
                } else if (set.size() < target.size()) {
                    // targetがmasterになる
                    HashSet<List<Block>> newSet = new HashSet<>(set);
                    newSet.removeAll(target);

                    // targetは必ず残る
                    nextMasters.add(targetPair);

                    if (newSet.size() == 0) {
                        // 上位のtargetが存在するのでsetはいらない
                        // 残りのtargetも無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(masters);
                        break;
                    }
                } else {
                    HashSet<List<Block>> newSet = new HashSet<>(set);
                    newSet.retainAll(target);

                    // targetは必ず残る
                    nextMasters.add(targetPair);

                    if (newSet.size() == target.size()) {
                        // 完全に同一のtargetが存在するのでsetはいらない
                        // 残りのtargetも無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(masters);
                        break;
                    }
                }
            }

            if (isSetNeed)
                nextMasters.add(pair);

            masters = nextMasters;
        }

        System.out.println(masters.size());
        for (Pair<Operations, Set<List<Block>>> master : masters) {
            System.out.println(master.getKey());
        }
    }

    private TreeSet<BlockFieldOperations> createBlockFields(Field field, int maxClearLine, MinoFactory minoFactory, TreeSet<Operations> allUniqueOperations) {
        TreeSet<BlockFieldOperations> blockFieldOperationsList = new TreeSet<>();
        for (Operations allOperation : allUniqueOperations) {
            // 操作を取り出す
            List<Operation> operations = allOperation.getOperations();

            // 初期化
            BlockField blockField2 = new BlockField(maxClearLine);
            BlockFieldOperations blockFieldOperations = new BlockFieldOperations(blockField2, allOperation);
            Field freeze = field.freeze(maxClearLine);  // このフィールドには行が揃っていてもライン消去されていない状態で記録

            // 操作を再生する
            for (Operation operation : operations) {
                // 接着情報を取り出す
                Rotate rotate = operation.getRotate();
                Block block = operation.getBlock();
                int x = operation.getX();
                int y = operation.getY();
                Mino mino = minoFactory.create(block, rotate);

                // 一度、フィールドをライン消去する
                long newdeletekey = freeze.clearLineReturnKey();

                // 何もないフィールドにミノをおき、これまでに消去されたラインを空白で復元させる
                Field vanilla = FieldFactory.createField(maxClearLine);
                vanilla.putMino(mino, x, y);
                vanilla.insertWhiteLineWithKey(newdeletekey);

                // おいたミノをこれまでの結果に統合する
                blockFieldOperations.merge(vanilla, block);

                // ライン消去されたフィールドにミノをおく
                freeze.putMino(mino, x, y);

                // ライン消去前の状態に戻す
                freeze.insertBlackLineWithKey(newdeletekey);
            }

            blockFieldOperationsList.add(blockFieldOperations);
        }
        return blockFieldOperationsList;
    }

    private TreeSet<Operations> parseToUniqueOperations(List<Pair<List<Operation>, List<Result>>> allDerivationPath) {
        TreeSet<Operations> allOperations = new TreeSet<>();
        for (Pair<List<Operation>, List<Result>> allPathPatternPair : allDerivationPath) {
            List<Operation> baseOperations = allPathPatternPair.getKey();
            List<Result> search = allPathPatternPair.getValue();

            // Resultからオペレーションに変換。オペレーションが長い順に並び替える
            List<List<Operation>> sortedOperations = search.stream()
                    .map(Result::createOperations)
                    .sorted(Comparator.comparing(List::size, reverseOrder()))
                    .collect(Collectors.toList());

            ArrayList<Operations> newOperations = new ArrayList<>();
            newOperations.add(new Operations(baseOperations));

            // すでに確定しているオペレーション順をもとに、新たに派生するオペレーションをつなげて追加する
            for (List<Operation> operation : sortedOperations) {
                // すべての確定分をもとに、派生パターンを生成
                for (int index = 0, size = newOperations.size(); index < size; index++) {
                    List<Operation> base = newOperations.get(index).getOperations();
                    ArrayList<Operation> list = new ArrayList<>(operation);
                    list.addAll(base.subList(operation.size(), base.size()));
                    assert list.size() == base.size();
                    newOperations.add(new Operations(list));
                }
            }

            allOperations.addAll(newOperations);
        }

        return allOperations;
    }

    private List<Pair<List<Operation>, FullValidator>> createPathCheckList(Field field, int maxClearLine, List<Pair<List<Block>, List<Result>>> allMergedPatterns, PerfectValidator perfectValidator, MinoFactory minoFactory) {
        List<Pair<List<Operation>, FullValidator>> pathCheckList = new ArrayList<>();
        for (Pair<List<Block>, List<Result>> mergedPatternPair : allMergedPatterns) {
            List<Result> results = mergedPatternPair.getValue();
            for (Result result : results) {
                // resultから1手ごとの地形リストを作成
                ArrayList<Field> expectField = createExpectedFieldListFromResult(field, maxClearLine, minoFactory, result);

                // 統合される瞬間までのパスを探索するためのValidator
                PathFullValidator pathFullValidator = PathFullValidator.createWithoutHold(expectField, perfectValidator);

                pathCheckList.add(new Pair<>(result.createOperations(), pathFullValidator));
            }
        }
        return pathCheckList;
    }

    private ArrayList<Field> createExpectedFieldListFromResult(Field field, int maxClearLine, MinoFactory minoFactory, Result result) {
        ArrayList<Field> expectField = new ArrayList<>();

        int currentMaxClearLine = maxClearLine;
        Field current = field.freeze(currentMaxClearLine);
        for (Operation operation : result.createOperations()) {
            Block block = operation.getBlock();
            Rotate rotate = operation.getRotate();
            int x = operation.getX();
            int y = operation.getY();

            Mino mino = minoFactory.create(block, rotate);
            current.putMino(mino, x, y);
            currentMaxClearLine -= current.clearLine();
            expectField.add(current);

            current = current.freeze(currentMaxClearLine);
        }
        return expectField;
    }

    TreeSet<Operations> getAllOperations() {
        return allUniqueOperations;
    }

    List<Operations> getUniqueOperations() {
        return uniqueOperations;
    }
}

package entry.path;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Selector<K extends HaveSet<V>, V> {
    private final List<K> candidates;

    public Selector(List<K> candidates) {
        this.candidates = candidates;
    }

    public List<K> select(boolean specified_only) {
        // 他のパターンではカバーできないものだけを列挙する
        LinkedList<K> selected = new LinkedList<>();

        for (K pair : candidates) {
            Set<V> canBuildBlocks = pair.getSet(specified_only);
            boolean isSetNeed = true;
            LinkedList<K> nextMasters = new LinkedList<>();

            // すでに登録済みのパターンでカバーできるか確認
            while (!selected.isEmpty()) {
                K targetPair = selected.pollFirst();
                Set<V> registeredBlocks = targetPair.getSet(specified_only);

                if (registeredBlocks.size() < canBuildBlocks.size()) {
                    // 新しいパターンの方が多く対応できる  // 新パターンが残る
                    HashSet<V> newTarget = new HashSet<>(registeredBlocks);
                    newTarget.removeAll(canBuildBlocks);

                    // 新パターンでも対応できないパターンがあるときは残す
                    if (newTarget.size() != 0)
                        nextMasters.add(targetPair);
                } else if (canBuildBlocks.size() < registeredBlocks.size()) {
                    // 登録済みパターンの方が多く対応できる
                    HashSet<V> newSet = new HashSet<>(canBuildBlocks);
                    newSet.removeAll(registeredBlocks);

                    // 登録済みパターンを残す
                    nextMasters.add(targetPair);

                    if (newSet.size() == 0) {
                        // 上位のパターンが存在するので新パターンはいらない
                        // 残りの登録済みパターンは無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(selected);
                        break;
                    }
                } else {
                    // 新パターンと登録済みパターンが対応できる数は同じ
                    HashSet<V> newSet = new HashSet<>(canBuildBlocks);
                    newSet.retainAll(registeredBlocks);

                    // 登録済みパターンを残す
                    nextMasters.add(targetPair);

                    if (newSet.size() == registeredBlocks.size()) {
                        // 完全に同一の対応パターンなので新パターンはいらない
                        // 残りの登録済みパターンは無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(selected);
                        break;
                    }
                }
            }

            // 新パターンが必要
            if (isSetNeed)
                nextMasters.add(pair);

            selected = nextMasters;
        }

        return selected;
    }
}

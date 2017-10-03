package entry.path;

import common.iterable.CombinationIterable;

import java.util.*;

public class Selector<K extends HaveSet<V>, V> {
    private final List<K> candidates;

    public Selector(List<K> candidates) {
        this.candidates = candidates;
    }

//    public List<K> select() {
//        // 他のパターンではカバーできないものだけを列挙する
//        LinkedList<K> selected = new LinkedList<>();
//
//        for (K pair : candidates) {
//            Set<V> canBuildBlocks = pair.getSet();
//            boolean isSetNeed = true;
//            LinkedList<K> nextMasters = new LinkedList<>();
//
//            // すでに登録済みのパターンでカバーできるか確認
//            while (!selected.isEmpty()) {
//                K targetPair = selected.pollFirst();
//                Set<V> registeredBlocks = targetPair.getSet();
//
//                if (registeredBlocks.size() < canBuildBlocks.size()) {
//                    // 新しいパターンの方が多く対応できる  // 新パターンが残る
//                    HashSet<V> newTarget = new HashSet<>(registeredBlocks);
//                    newTarget.removeAll(canBuildBlocks);
//
//                    // 新パターンでも対応できないパターンがあるときは残す
//                    if (newTarget.size() != 0)
//                        nextMasters.add(targetPair);
//                } else if (canBuildBlocks.size() < registeredBlocks.size()) {
//                    // 登録済みパターンの方が多く対応できる
//                    HashSet<V> newSet = new HashSet<>(canBuildBlocks);
//                    newSet.removeAll(registeredBlocks);
//
//                    // 登録済みパターンを残す
//                    nextMasters.add(targetPair);
//
//                    if (newSet.size() == 0) {
//                        // 上位のパターンが存在するので新パターンはいらない
//                        // 残りの登録済みパターンは無条件で残す
//                        isSetNeed = false;
//                        nextMasters.addAll(selected);
//                        break;
//                    }
//                } else {
//                    // 新パターンと登録済みパターンが対応できる数は同じ
//                    HashSet<V> newSet = new HashSet<>(canBuildBlocks);
//                    newSet.retainAll(registeredBlocks);
//
//                    // 登録済みパターンを残す
//                    nextMasters.add(targetPair);
//
//                    if (newSet.size() == registeredBlocks.size()) {
//                        // 完全に同一の対応パターンなので新パターンはいらない
//                        // 残りの登録済みパターンは無条件で残す
//                        isSetNeed = false;
//                        nextMasters.addAll(selected);
//                        break;
//                    }
//                }
//            }
//
//            // 新パターンが必要
//            if (isSetNeed)
//                nextMasters.add(pair);
//
//            selected = nextMasters;
//        }
//
//        return selected;
//    }

    public List<K> select() {
        // 他のパターンではカバーできないものだけを列挙する
        LinkedList<K> selected = new LinkedList<>();

        ArrayList<K> sorted = new ArrayList<>(candidates);
        Comparator<K> comparator = Comparator.comparingInt(o -> o.getSet().size());
        sorted.sort(comparator.reversed());

        HashSet<V> all = new HashSet<>();
        for (K k : sorted) {
            Set<V> set = k.getSet();
            all.addAll(set);
        }
        int allSize = all.size();
        System.out.println(allSize);
        System.out.println("=====");

        for (int popCount = 1; popCount <= sorted.size(); popCount++) {
            System.out.println(popCount);
            int max = -1;
            CombinationIterable<K> combinations = new CombinationIterable<>(sorted, popCount);
            for (List<K> combination : combinations) {
                HashSet<V> sets = new HashSet<>();
                for (K k : combination) {
                    sets.addAll(k.getSet());
                }
                if (max < sets.size()) {
                    max = sets.size();
                    System.out.println(max);
                    if (max == allSize) {
                        combination.sort(comparator.reversed());
                        for (K k : combination) {
                            if (k instanceof PathPair) {
                                PathPair pair = (PathPair) k;
                                System.out.println("http://fumen.zui.jp/?v115@" + pair.getFumen());
                            }
                        }
                        return sorted;
                    }
                }
            }
        }

        return sorted;
    }
}

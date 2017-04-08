package a;

import action.reachable.LockedReachable;
import core.field.Field;
import core.mino.Mino;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class Depend {
    private final LockedReachable lockedReachable;

    public Depend(LockedReachable lockedReachable) {
        this.lockedReachable = lockedReachable;
    }

    public Map<Integer, Set<Integer>> extract(Field field, int[][] numberField, List<MinoPivot> minoPivots) {
        int height = numberField.length;

        // 全ての探索すべき番号
        Set<Integer> remainingNumbers = createRemainingNumbers(minoPivots);

        // 次に探索すべき番号
        Set<Integer> nextNumbers = new HashSet<>();

        // 最初に探索すべき番号を列挙
        for (int searchingNumber : remainingNumbers) {
            MinoPivot minoPivot = minoPivots.get(searchingNumber);

            // その位置にミノを置くことができる
            if (!isReachable(minoPivot, field, height))
                continue;

            // ミノを取り除くことができない
            if (!checksValidWhenRemoveMinoInField(numberField, minoPivots, searchingNumber, field, height, remainingNumbers))
                continue;

            nextNumbers.add(searchingNumber);
        }
        remainingNumbers.removeAll(nextNumbers);

        // 結果
        Map<Integer, Set<Integer>> results = new HashMap<>();

        // 探索更新用フィールド
        Field currentField = field.freeze(height);

        // 全ての探索が終わっていないときは探索を継続
        while (!remainingNumbers.isEmpty()) {
            System.out.println("---");
            System.out.println("Remain: " + remainingNumbers);
            System.out.println("Next: " + nextNumbers);

            // あるミノを除いたフィールドで、さらに除くことがミノのマップ
            Map<Integer, Set<Integer>> dependency = new HashMap<>();

            // ひとつのミノを取り除く
            for (int searchingNumber : nextNumbers) {
                MinoPivot minoPivot = minoPivots.get(searchingNumber);

                // ミノが分断されていない
                if (!checksDividedMinoInField(numberField, minoPivot, searchingNumber)) {
                    Field freeze = currentField.freeze(height);
                    freeze.removeMino(minoPivot.getMino(), minoPivot.getX(), minoPivot.getY());

                    // ミノを除いたフィールドで、さらに除くことができるミノを列挙する
                    // そのミノが次の探索候補となる
                    Set<Integer> numbersToPutInNext = new HashSet<>();
                    for (int nextRemovedNumber : remainingNumbers) {
                        MinoPivot nextMinoPivot = minoPivots.get(nextRemovedNumber);

                        // その位置にミノを置くことができる
                        if (!isReachable(nextMinoPivot, freeze, height))
                            continue;

                        // ミノを取り除くことができない
                        if (!checksValidWhenRemoveMinoInField(numberField, minoPivots, nextRemovedNumber, freeze, height, remainingNumbers))
                            continue;

                        numbersToPutInNext.add(nextRemovedNumber);
                    }

                    // 探索結果の保存
                    dependency.put(searchingNumber, numbersToPutInNext);
                } else {
                    // フィールドからそのまま取り除くことができない (ミノが分断されている)
                    // searchingNumber がなく、揃った行は削除
                    // y < coodinate.y　の行は削除するか検討（しなくてもなんとかなるが、どっちがいい？）

                    throw new NotImplementedException();
                }
            }

            System.out.println(" => " + dependency);

            // 最終的な結果に保存
            results.putAll(dependency);

            // 残りの探索番号を更新
            remainingNumbers.removeAll(dependency.keySet());

            // 次の探索候補の作成
            HashSet<Integer> margeNextNumbers = new HashSet<>();
            for (Set<Integer> dependencyNumbers : dependency.values())
                margeNextNumbers.addAll(dependencyNumbers);
            nextNumbers = margeNextNumbers;

            // 残りの探索番号と次の探索番号が同じ時は無条件で追加し終了
            if (remainingNumbers.equals(nextNumbers)) {
                for (Integer number : remainingNumbers)
                    results.put(number, new HashSet<>());
                break;
            }

            // 次のフィールドを作成
            for (int key : dependency.keySet()) {
                MinoPivot minoPivot = minoPivots.get(key);
                field.removeMino(minoPivot.getMino(), minoPivot.getX(), minoPivot.getY());
            }
        }

        return results;
    }

    private boolean isReachable(MinoPivot minoPivot, Field field, int height) {
        Mino mino = minoPivot.getMino();
        int x = minoPivot.getX();
        int y = minoPivot.getY();
        field.removeMino(mino, x, y);
        boolean result = lockedReachable.checksReachable(field, mino.getBlock(),x, y, mino.getRotate(), height);
        field.putMino(mino, x, y);
        return result;
    }

    private Set<Integer> createRemainingNumbers(List<MinoPivot> minoPivots) {
        Set<Integer> remainingNumbers = new HashSet<>();
        for (int number = 0; number < minoPivots.size(); number++)
            remainingNumbers.add(number);
        return remainingNumbers;
    }

    // フィールドにミノが分断されて配置されているとき true を返却
    private boolean checksDividedMinoInField(int[][] numberField, MinoPivot minoPivot, int minoNumber) {
        int minoPivotX = minoPivot.getX();
        int minoPivotY = minoPivot.getY();
        for (int[] positions : minoPivot.getMino().getPositions()) {
            int x = minoPivotX + positions[0];
            int y = minoPivotY + positions[1];
            if (numberField[y][x] != minoNumber)
                return true;
        }
        return false;
    }

    // 指定したミノを取り除いても有効なフィールドであるとき true を返却
    private boolean checksValidWhenRemoveMinoInField(int[][] numberField, List<MinoPivot> minoPivots, int number, Field field, int height, Set<Integer> remainingNumbers) {
        MinoPivot minoPivot = minoPivots.get(number);
        HashSet<Integer> numbersOnMino = extractNumbersOnMino(numberField, minoPivot, number, height);
        numbersOnMino.retainAll(remainingNumbers);

        Mino mino = minoPivot.getMino();
        int x = minoPivot.getX();
        int y = minoPivot.getY();
        field.removeMino(mino, x, y);
        boolean result = canPutAllMinosToField(minoPivots, field, numbersOnMino);
        field.putMino(mino, x, y);
        return result;
    }

    // あるミノのひとつ上にある番号を抽出
    private HashSet<Integer> extractNumbersOnMino(int[][] numberField, MinoPivot minoPivot, int number, int height) {
        int searchingMinoPivotX = minoPivot.getX();
        int searchingMinoPivotY = minoPivot.getY();

        HashSet<Integer> numbersOnMino = new HashSet<>();
        for (int[] positions : minoPivot.getMino().getPositions()) {
            int x = searchingMinoPivotX + positions[0];
            int y = searchingMinoPivotY + positions[1] + 1;
            if (y < height) {
                int target = numberField[y][x];
                if (0 <= target && target != number)
                    numbersOnMino.add(target);
            }
        }
        return numbersOnMino;
    }

    // 指定したフィールド上で、指定した番号のミノをすべて置けるなら true を返却
    private boolean canPutAllMinosToField(List<MinoPivot> minoPivots, Field field, HashSet<Integer> numbersOnMino) {
        for (int numberOnMino : numbersOnMino) {
            MinoPivot target = minoPivots.get(numberOnMino);
            if (!field.canPutMino(target.getMino(), target.getX(), target.getY()))
                return false;
        }
        return true;
    }
}

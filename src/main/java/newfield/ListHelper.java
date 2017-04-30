package newfield;

import java.util.ArrayList;
import java.util.List;

public class ListHelper {
    public static <T> List<List<T>> crossConcatList(List<List<T>> left, List<List<T>> right) {
        List<List<T>> newList = new ArrayList<>();
        for (List<T> lElements : left) {
            for (List<T> rElements : right) {
                List<T> concat = new ArrayList<>(lElements);
                concat.addAll(rElements);
                newList.add(concat);
            }
        }
        return newList;
    }
}

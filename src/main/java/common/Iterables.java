package common;

import java.util.ArrayList;
import java.util.List;

public class Iterables {
    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T t : iterable)
            list.add(t);
        return list;
    }
}

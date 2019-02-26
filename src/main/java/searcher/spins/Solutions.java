package searcher.spins;

import java.util.HashSet;
import java.util.Set;

public class Solutions<K extends Number> {
    private final Set<Set<K>> solutions;

    public Solutions() {
        this.solutions = new HashSet<>();
    }

    public boolean contains(Set<K> keys) {
        return solutions.contains(keys);
    }

    public boolean add(Set<K> keys) {
        return solutions.add(keys);
    }

    public boolean partialContains(Set<K> candidateKeys, K currentKey) {
        Set<K> keys = new HashSet<>(candidateKeys);
        keys.add(currentKey);

        if (contains(keys)) {
            return true;
        }

        // currentKey以外をひとつ抜いた組み合わせでも solutions に含まれている
        for (K prevKey : candidateKeys) {
            keys.remove(prevKey);

            if (contains(keys)) {
                return true;
            }

            keys.add(prevKey);
        }

        return false;
    }
}

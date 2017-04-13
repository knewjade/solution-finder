package misc;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class ListPiecesIterator implements Iterator<SafePieces> {
    private final List<String> patterns;
    private PiecesIterator current;
    private int index;

    ListPiecesIterator(List<String> patterns) {
        this.patterns = patterns;
        this.current = new PiecesIterator(patterns.get(index));
        this.index = 1;
    }

    @Override
    public boolean hasNext() {
        return index < patterns.size() || current.hasNext();
    }

    @Override
    public SafePieces next() {
        if (!hasNext())
            throw new NoSuchElementException();

        if (current.hasNext())
            return current.next();

        this.current = new PiecesIterator(patterns.get(index));
        this.index += 1;

        return current.next();
    }

    int getDepths() {
        return current.getDepths();
    }
}

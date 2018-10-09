package common.pattern;

import common.SyntaxException;
import common.datastore.PieceCounter;
import common.datastore.blocks.Pieces;
import common.parser.BlockInterpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class LoadedPatternGenerator implements PatternGenerator {
    private final List<PatternInterpreter> interpreters;

    public LoadedPatternGenerator(String pattern) throws SyntaxException {
        this(Collections.singletonList(pattern));
    }

    public LoadedPatternGenerator(List<String> patterns) throws SyntaxException {
        ArrayList<PatternInterpreter> interpreters = new ArrayList<>();
        int depth = -1;
        for (int index = 0; index < patterns.size(); index++) {
            String pattern = patterns.get(index);
            String trim = pattern.trim();
            if (trim.startsWith("'") && trim.endsWith("'")) {
                trim = trim.substring(1, trim.length() - 1);
            }

            if (trim.startsWith("#Q=")) {
                trim = BlockInterpreter.parseQuizToPieceString(trim);
            }

            if (trim.isEmpty() || trim.startsWith("#")) {
                continue;
            }

            PatternInterpreter interpreter = parseInterpreter(index, trim);

            if (depth == -1) {
                depth = getDepth(interpreter);
            } else if (getDepth(interpreter) != depth) {
                String message = SyntaxException.formatMessageOnLine("Num of blocks is not equal to other line", index + 1);
                throw new SyntaxException(message);
            }

            interpreters.add(interpreter);
        }

        this.interpreters = interpreters;
    }

    private PatternInterpreter parseInterpreter(int index, String trim) throws SyntaxException {
        try {
            return new PatternInterpreter(trim);
        } catch (SyntaxException e) {
            throw new SyntaxException(e, index + 1);
        }
    }

    @Override
    public int getDepth() {
        if (interpreters.isEmpty())
            return 0;
        return getDepth(interpreters.get(0));
    }

    private int getDepth(PatternInterpreter interpreter) {
        List<Element> elements = interpreter.getElements();
        return elements.stream()
                .mapToInt(Element::getPopCount)
                .sum();
    }

    @Override
    public Stream<Pieces> blocksStream() {
        Stream<Pieces> stream = Stream.empty();
        for (PatternInterpreter interpreter : interpreters) {
            List<Element> elements = interpreter.getElements();
            stream = Stream.concat(stream, new PiecesStreamBuilder(elements).blocksStream());
        }
        return stream;
    }

    @Override
    public Stream<PieceCounter> blockCountersStream() {
        Stream<PieceCounter> stream = Stream.empty();
        for (PatternInterpreter interpreter : interpreters) {
            List<Element> elements = interpreter.getElements();
            stream = Stream.concat(stream, new PiecesStreamBuilder(elements).blockCountersStream());
        }
        return stream;
    }
}



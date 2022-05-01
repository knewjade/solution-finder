package common.pattern;

import common.SyntaxException;
import common.parser.StringEnumTransform;
import core.mino.Piece;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Token {
    private static final String EMPTY_STRING = "";

    static Token createToken(List<String> tokenList) {
        return new Token(tokenList, Arrays.asList(" ", ","));
    }

    static Token createSubToken(List<String> tokenList, int lastIndex) {
        return new Token(tokenList, Collections.singletonList(" "), lastIndex);
    }

    static boolean isEmptyString(String pop) {
        return EMPTY_STRING.equals(pop);
    }

    private final LinkedList<String> token;
    private int lastIndex;
    private final List<String> blackCharacters;

    private Token(List<String> tokenList, List<String> blackCharacters) {
        this(tokenList, blackCharacters, -1);
    }

    private Token(List<String> tokenList, List<String> blackCharacters, int lastIndex) {
        this.token = new LinkedList<>(tokenList);
        this.blackCharacters = blackCharacters;
        this.lastIndex = lastIndex;
    }

    private boolean hasNext() {
        if (token.isEmpty())
            return false;
        String next = token.peekFirst();
        return !next.equals("#");
    }

    void skip() {
        token.pop();
        lastIndex += 1;
    }

    public String check() {
        while (hasNext()) {
            String next = token.peekFirst();
            assert next != null;
            if (isBlank(next)) {
                skip();
                continue;
            }
            return next.toUpperCase();
        }
        return EMPTY_STRING;
    }

    String nextString() {
        while (hasNext()) {
            String next = token.pollFirst();
            assert next != null;
            lastIndex += 1;
            if (isBlank(next))
                continue;
            return next.toUpperCase();
        }
        return EMPTY_STRING;
    }

    private boolean isBlank(String s) {
        for (String blank : blackCharacters)
            if (s.equals(blank))
                return true;
        return false;
    }

    boolean isBlankNext() {
        if (token.isEmpty())
            return true;
        String next = token.peekFirst();
        return isBlank(next);
    }

    public int nextInt() throws SyntaxException {
        String s = nextString();
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            throw new SyntaxException("Unexpected number format: value=" + s, getLastIndex());
        }
    }

    Piece nextBlock() throws SyntaxException {
        String s = nextString();
        try {
            return StringEnumTransform.toPiece(s);
        } catch (Exception e) {
            throw new SyntaxException("Unexpected block format: value=" + s, getLastIndex());
        }
    }

    public boolean isContinue() {
        return !EMPTY_STRING.equals(check());
    }

    int getLastIndex() {
        return lastIndex;
    }

    public String pop() {
        if (hasNext()) {
            String next = token.pollFirst();
            assert next != null;
            lastIndex += 1;
            return next.toUpperCase();
        }
        return EMPTY_STRING;
    }
}

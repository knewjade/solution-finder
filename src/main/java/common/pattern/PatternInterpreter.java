package common.pattern;

import common.SyntaxException;
import core.mino.Piece;

import java.util.*;

public class PatternInterpreter {
    private final ArrayList<Element> elements = new ArrayList<>();
    private final Token token;

    PatternInterpreter(String pattern) throws SyntaxException {
        List<String> tokenList = Arrays.asList(pattern.split(""));
        this.token = Token.createToken(tokenList);
        while (token.isContinue()) {
            String s = token.nextString();
            Element element = parse(s);
            elements.add(element);
        }
    }

    private Element parse(String s) throws SyntaxException {
        switch (s) {
            case "T":
                return constant(Piece.T);
            case "I":
                return constant(Piece.I);
            case "O":
                return constant(Piece.O);
            case "J":
                return constant(Piece.J);
            case "L":
                return constant(Piece.L);
            case "S":
                return constant(Piece.S);
            case "Z":
                return constant(Piece.Z);
            case "*":
                return wildcard();
            case "[":
                return combination();
            case "]":
                throw new SyntaxException("Too much ']'", token.getLastIndex());
        }
        throw new SyntaxException("Unknown block type: value=" + s, token.getLastIndex());
    }

    private SingleElement constant(Piece piece) {
        return new SingleElement(piece);
    }

    private WildcardElement wildcard() throws SyntaxException {
        boolean blankNext = token.isBlankNext();
        switch (token.check()) {
            case "P":
                if (blankNext)
                    throw new SyntaxException("Contains invalid character between * and p", token.getLastIndex());

                token.skip();
                int size = token.nextInt();

                if (size <= 0)
                    throw new SyntaxException("no pop", token.getLastIndex());
                else if (8 <= size)
                    throw new SyntaxException("over pop", token.getLastIndex());

                return new WildcardElement(size);
            case "!":
                if (blankNext)
                    throw new SyntaxException("Contains invalid character between * and !", token.getLastIndex());

                token.skip();
                return new WildcardElement(7);
            default:
                return new WildcardElement(1);
        }
    }

    private Element combination() throws SyntaxException {
        int lastIndex = token.getLastIndex();
        LinkedList<String> combinationTokenList = getCombinationTokenList();
        Token subToken = Token.createSubToken(combinationTokenList, lastIndex);
        HashSet<Piece> pieceSet = parseBlockSet(subToken);

        if (pieceSet.isEmpty())
            throw new SyntaxException("Empty in []", token.getLastIndex());

        boolean blankNext = token.isBlankNext();
        switch (token.check()) {
            case "P":
                if (blankNext)
                    throw new SyntaxException("Contains invalid character between [] and p", token.getLastIndex());
                this.token.skip();
                int size = this.token.nextInt();

                if (pieceSet.size() <= 0)
                    throw new SyntaxException("no pop", token.getLastIndex());
                else if (pieceSet.size() < size)
                    throw new SyntaxException("over pop", token.getLastIndex());

                return new BracketElement(pieceSet, size);
            case "!":
                if (blankNext)
                    throw new SyntaxException("Contains invalid character between [] and !", token.getLastIndex());
                this.token.skip();
                return new BracketElement(pieceSet, pieceSet.size());
            default:
                return new BracketElement(pieceSet, 1);
        }
    }

    private HashSet<Piece> parseBlockSet(Token token) throws SyntaxException {
        if ("^".equals(token.check())) {
            token.skip();
            return parseNotBlockSet(token);
        } else {
            return parseNormalBlockSet(token);
        }
    }

    private HashSet<Piece> parseNotBlockSet(Token token) throws SyntaxException {
        HashSet<Piece> allPieces = new HashSet<>(Piece.valueList());
        HashSet<Piece> notPieces = parseNormalBlockSet(token);
        allPieces.removeAll(notPieces);
        return allPieces;
    }

    private HashSet<Piece> parseNormalBlockSet(Token token) throws SyntaxException {
        HashSet<Piece> pieces = new HashSet<>();
        while (token.isContinue()) {
            Piece piece = token.nextBlock();
            if (pieces.contains(piece))
                throw new SyntaxException(String.format("Duplicate '%s' pieces in []", piece.getName()), token.getLastIndex());
            pieces.add(piece);
        }
        return pieces;
    }

    private LinkedList<String> getCombinationTokenList() throws SyntaxException {
        int depth = 1;
        LinkedList<String> inner = new LinkedList<>();
        while (true) {
            String pop = token.pop();
            if (Token.isEmptyString(pop))
                throw new SyntaxException("Not found ']'", token.getLastIndex());

            switch (pop) {
                case "[":
                    depth += 1;
                    break;
                case "]":
                    depth -= 1;
                    if (depth == 0)
                        return inner;
                    break;
            }

            inner.add(pop);
        }
    }

    List<Element> getElements() {
        return elements;
    }
}

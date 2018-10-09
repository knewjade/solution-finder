package common.parser;

import core.mino.Piece;

import java.util.stream.Stream;

import static common.parser.StringEnumTransform.toPiece;

public class BlockInterpreter {
    public static Stream<Piece> parse10(String a) {
        return Stream.of(
                toPiece(a.charAt(0)),
                toPiece(a.charAt(1)),
                toPiece(a.charAt(2)),
                toPiece(a.charAt(3)),
                toPiece(a.charAt(4)),
                toPiece(a.charAt(5)),
                toPiece(a.charAt(6)),
                toPiece(a.charAt(7)),
                toPiece(a.charAt(8)),
                toPiece(a.charAt(9))
        );
    }

    public static Stream<Piece> parse11(String a) {
        return Stream.of(
                toPiece(a.charAt(0)),
                toPiece(a.charAt(1)),
                toPiece(a.charAt(2)),
                toPiece(a.charAt(3)),
                toPiece(a.charAt(4)),
                toPiece(a.charAt(5)),
                toPiece(a.charAt(6)),
                toPiece(a.charAt(7)),
                toPiece(a.charAt(8)),
                toPiece(a.charAt(9)),
                toPiece(a.charAt(10))
        );
    }

    public static Stream<Piece> parse(String str) {
        Stream.Builder<Piece> builder = Stream.builder();
        for (int index = 0; index < str.length(); index++) {
            char c = str.charAt(index);
            Piece piece = StringEnumTransform.toPiece(c);
            builder.accept(piece);
        }
        return builder.build();
    }

    public static String parseQuizToPieceString(String comment) {
        StringBuilder builder = new StringBuilder();

        for (char ch : comment.toUpperCase().toCharArray()) {
            switch (ch) {
                case 'T':
                case 'I':
                case 'O':
                case 'S':
                case 'Z':
                case 'L':
                case 'J':
                case '*':
                    builder.append(ch);
            }
        }

        return builder.toString();
    }

}

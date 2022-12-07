package common.parser;

import core.mino.Piece;
import core.srs.Rotate;

public class StringEnumTransform {
    public static Piece toPiece(String name) {
        switch (name) {
            case "T":
                return Piece.T;
            case "S":
                return Piece.S;
            case "Z":
                return Piece.Z;
            case "I":
                return Piece.I;
            case "O":
                return Piece.O;
            case "J":
                return Piece.J;
            case "L":
                return Piece.L;
        }
        throw new IllegalArgumentException("No reachable");
    }

    public static Piece toPiece(char ch) {
        switch (ch) {
            case 'T':
                return Piece.T;
            case 'S':
                return Piece.S;
            case 'Z':
                return Piece.Z;
            case 'O':
                return Piece.O;
            case 'I':
                return Piece.I;
            case 'L':
                return Piece.L;
            case 'J':
                return Piece.J;
        }
        throw new IllegalArgumentException("No reachable");
    }

    public static Rotate toRotate(String name) {
        switch (name) {
            case "0":
                return Rotate.Spawn;
            case "L":
                return Rotate.Left;
            case "2":
                return Rotate.Reverse;
            case "R":
                return Rotate.Right;
        }
        throw new IllegalArgumentException("No reachable");
    }

    public static Rotate toNEWSRotate(char ch) {
        switch (ch) {
            case 'N':
                return Rotate.Spawn;
            case 'W':
                return Rotate.Left;
            case 'S':
                return Rotate.Reverse;
            case 'E':
                return Rotate.Right;
        }
        throw new IllegalArgumentException("No reachable");
    }

    public static String toString(Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return "0";
            case Left:
                return "L";
            case Reverse:
                return "2";
            case Right:
                return "R";
        }
        throw new IllegalStateException("No reachable");
    }
}

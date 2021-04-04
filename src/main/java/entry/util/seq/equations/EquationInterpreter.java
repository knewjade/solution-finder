package entry.util.seq.equations;

import core.mino.Piece;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

enum Type {
    Piece,
    Hold,
}

class Target {
    private final Type type;
    private final String typeStr;
    private final int value;
    private final Operators operator;

    public Target(Type type, String typeStr, int value, Operators operator) {
        this.type = type;
        this.typeStr = typeStr;
        this.value = value;
        this.operator = operator;
    }

    public Type getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public int getValue() {
        return value;
    }

    public Operators getOperator() {
        return operator;
    }
}

public class EquationInterpreter {
    private final List<PieceEquation> pieceEquations = new ArrayList<>();
    private HoldEquation holdEquation = null;

    public static EquationInterpreter parse(List<String> countEquations) throws FinderParseException {
        EquationInterpreter interpreter = new EquationInterpreter();

        for (String equation_ : countEquations) {
            for (String equation : equation_.split(",")) {
                String trim = equation.trim();

                // 記号の場所を取得
                int headOperator = headOperator(trim);
                if (headOperator < 0) {
                    // 記号がない
                    throw new FinderParseException("Not found operator");
                }

                int endOperator = endOperator(trim, headOperator);

                // 各アイテムに分割
                String leftStr = equation.substring(0, headOperator).trim().toLowerCase();
                String operatorStr = equation.substring(headOperator, endOperator + 1).trim().toLowerCase();
                String rightStr = equation.substring(endOperator + 1).trim().toLowerCase();

                if (leftStr.isEmpty()) {
                    throw new FinderParseException("Left operand is blank");
                }

                if (rightStr.isEmpty()) {
                    throw new FinderParseException("Right operand is blank");
                }

                // 対象と数字と演算子を取得
                Target target = extractTarget(leftStr, operatorStr, rightStr);
                int value = target.getValue();
                if (value < 0) {
                    throw new FinderParseException("Negative value is unsupported");
                }
                Operators operator = target.getOperator();

                switch (target.getType()) {
                    case Hold:
                        if (interpreter.holdEquation != null) {
                            throw new FinderParseException("Duplicate hold equations");
                        }
                        interpreter.holdEquation = new HoldEquation(value, operator);
                        break;
                    case Piece:
                        Piece piece = getPiece(target.getTypeStr());
                        interpreter.pieceEquations.add(new PieceEquation(piece, value, operator));
                        break;
                }
            }
        }

        return interpreter;
    }

    private static int headOperator(String equation) {
        int g = equation.indexOf('<');
        if (0 <= g) {
            return g;
        }

        int l = equation.indexOf('>');
        if (0 <= l) {
            return l;
        }

        int n = equation.indexOf('!');
        if (0 <= n) {
            return n;
        }

        return equation.indexOf('=');
    }

    private static int endOperator(String equation, int headSign) {
        if (equation.length() - 1 <= headSign) {
            return headSign;
        }

        if (equation.charAt(headSign + 1) == '=') {
            return headSign + 1;
        }

        return headSign;
    }

    private static Target extractTarget(
            String leftStr, String operatorStr, String rightStr
    ) throws FinderParseException {
        Optional<Type> leftOpt = checksType(leftStr);
        Optional<Type> rightOpt = checksType(rightStr);

        if (leftOpt.isPresent() == rightOpt.isPresent()) {
            throw new FinderParseException("Invalid operand");
        }

        Operators operator = extractOperator(operatorStr);

        return leftOpt.map(type -> new Target(type, leftStr, Integer.parseInt(rightStr), operator))
                .orElseGet(() -> new Target(rightOpt.get(), rightStr, Integer.parseInt(leftStr), operator.flip()));
    }

    private static Optional<Type> checksType(String str) {
        switch (str) {
            case "hold":
                return Optional.of(Type.Hold);
            case "t":
            case "i":
            case "o":
            case "s":
            case "z":
            case "l":
            case "j":
                return Optional.of(Type.Piece);
        }
        return Optional.empty();
    }

    private static Operators extractOperator(String operatorStr) throws FinderParseException {
        switch (operatorStr) {
            case "=":
            case "==":
                return Operators.EqualTo;
            case "!=":
                return Operators.NotEqualTo;
            case "<":
                return Operators.LessThan;
            case "<=":
                return Operators.LessThanOrEqualTo;
            case ">":
                return Operators.GreaterThan;
            case ">=":
                return Operators.GreaterThanOrEqualTo;
        }
        throw new FinderParseException("Invalid operator: operator=" + operatorStr);
    }

    private static Piece getPiece(String str) throws FinderParseException {
        switch (str) {
            case "t":
                return Piece.T;
            case "i":
                return Piece.I;
            case "o":
                return Piece.O;
            case "s":
                return Piece.S;
            case "z":
                return Piece.Z;
            case "l":
                return Piece.L;
            case "j":
                return Piece.J;
        }
        throw new FinderParseException("Unsupported piece: piece=" + str);
    }

    private EquationInterpreter() {
    }

    public Optional<HoldEquation> getHoldEquation() {
        return Optional.ofNullable(holdEquation);
    }

    public List<PieceEquation> getPieceEquation() {
        return pieceEquations;
    }
}

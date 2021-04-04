package entry.util.seq.equations;

import core.mino.Piece;
import exceptions.FinderParseException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class EquationInterpreterTest {
    private void assertPieceEquation(String equation, PieceEquation expected) throws FinderParseException {
        EquationInterpreter interpreter = EquationInterpreter.parse(Collections.singletonList(equation));
        assertThat(interpreter.getPieceEquation()).isEqualTo(Collections.singletonList(expected));
        assertThat(interpreter.getHoldEquation()).isEmpty();
    }

    private void assertHoldEquation(String equation, HoldEquation expected) throws FinderParseException {
        EquationInterpreter interpreter = EquationInterpreter.parse(Collections.singletonList(equation));
        assertThat(interpreter.getPieceEquation()).isEmpty();
        assertThat(interpreter.getHoldEquation()).hasValue(expected);
    }

    @Test
    void equalTo() throws FinderParseException {
        assertPieceEquation("Z=0", new PieceEquation(Piece.Z, 0, Operators.EqualTo));
        assertPieceEquation("z==1", new PieceEquation(Piece.Z, 1, Operators.EqualTo));
        assertPieceEquation("2=J", new PieceEquation(Piece.J, 2, Operators.EqualTo));
        assertPieceEquation("3==j", new PieceEquation(Piece.J, 3, Operators.EqualTo));
    }

    @Test
    void notEqualTo() throws FinderParseException {
        assertPieceEquation("O!=0", new PieceEquation(Piece.O, 0, Operators.NotEqualTo));
        assertPieceEquation("0!=o", new PieceEquation(Piece.O, 0, Operators.NotEqualTo));
    }

    @Test
    void greaterThan() throws FinderParseException {
        assertPieceEquation("T>0", new PieceEquation(Piece.T, 0, Operators.GreaterThan));
        assertPieceEquation("1<t", new PieceEquation(Piece.T, 1, Operators.GreaterThan));
    }

    @Test
    void greaterThanOrEqualTo() throws FinderParseException {
        assertPieceEquation("I>=0", new PieceEquation(Piece.I, 0, Operators.GreaterThanOrEqualTo));
        assertPieceEquation("1<=i", new PieceEquation(Piece.I, 1, Operators.GreaterThanOrEqualTo));
    }

    @Test
    void lessThan() throws FinderParseException {
        assertPieceEquation("S<0", new PieceEquation(Piece.S, 0, Operators.LessThan));
        assertPieceEquation("1>s", new PieceEquation(Piece.S, 1, Operators.LessThan));
    }

    @Test
    void lessThanOrEqualTo() throws FinderParseException {
        assertPieceEquation("L<=0", new PieceEquation(Piece.L, 0, Operators.LessThanOrEqualTo));
        assertPieceEquation("1>=l", new PieceEquation(Piece.L, 1, Operators.LessThanOrEqualTo));
    }

    @Test
    void hold() throws FinderParseException {
        assertHoldEquation("Hold=0", new HoldEquation(0, Operators.EqualTo));
        assertHoldEquation("1!=hold", new HoldEquation(1, Operators.NotEqualTo));
        assertHoldEquation("2<HOLD", new HoldEquation(2, Operators.GreaterThan));
    }
}
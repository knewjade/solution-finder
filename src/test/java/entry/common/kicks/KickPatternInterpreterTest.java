package entry.common.kicks;

import core.mino.Piece;
import core.srs.Pattern;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class KickPatternInterpreterTest {
    @Nested
    class Regular {
        @Test
        void fixed1() {
            assertThat(KickPatternInterpreter.create("T.NE", "(0,0)"))
                    .returns(new KickType(Piece.T, Rotate.Spawn, Rotate.Right), KickPattern::getKickType)
                    .returns(Pattern.noPrivilegeSpins(new int[][]{{0, 0}}), KickPattern::getPattern);
        }

        @Test
        void fixed2() {
            assertThat(KickPatternInterpreter.create("S.ES", "(1, 1), (2, 2)"))
                    .returns(new KickType(Piece.S, Rotate.Right, Rotate.Reverse), KickPattern::getKickType)
                    .returns(Pattern.noPrivilegeSpins(new int[][]{{1, 1}, {2, 2}}), KickPattern::getPattern);
        }

        @Test
        void fixed3() {
            assertThat(KickPatternInterpreter.create("O.SW", " ( +0 , -0 )( +2 , -2 ) (+3,-3) "))
                    .returns(new KickType(Piece.O, Rotate.Reverse, Rotate.Left), KickPattern::getKickType)
                    .returns(Pattern.noPrivilegeSpins(new int[][]{{0, 0}, {2, -2}, {3, -3}}), KickPattern::getPattern);
        }

        @Test
        void fixed4PrivilegeSpins() {
            assertThat(KickPatternInterpreter.create("O.SW", " (@ -0 , 0 )( -2 , -2 ) (@-3,-3) "))
                    .returns(new KickType(Piece.O, Rotate.Reverse, Rotate.Left), KickPattern::getKickType)
                    .returns(new Pattern(new int[][]{{0, 0}, {-2, -2}, {-3, -3}}, new boolean[]{true, false, true}), KickPattern::getPattern);
        }

        @Test
        void reference1() {
            KickPattern referenced = KickPatternInterpreter.create("J.WS", "(0,0)");
            Map<KickType, KickPattern> fallback = Collections.singletonMap(referenced.getKickType(), referenced);
            assertThat(KickPatternInterpreter.create("L.SW", "&J.WS"))
                    .returns(new KickType(Piece.L, Rotate.Reverse, Rotate.Left), KickPattern::getKickType)
                    .returns(null, KickPattern::getPattern)
                    .returns(Pattern.noPrivilegeSpins(new int[][]{{0, 0}}), it -> it.getPattern(fallback));
        }
    }

    @Nested
    class InvalidKey {
        @Test
        void empty() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("", "(0,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void invalidPiece() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("K.WS", "(0,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void invalidMark() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("J_WS", "(0,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void invalidRotateFrom() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("J.2S", "(0,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void invalidRotateTo() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("J.WL", "(0,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class InvalidValue {
        @Test
        void empty() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", ""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void noBracket() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "0,0)(1,1)"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "(0,0(1,1)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void recursively() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "((0,0))"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void noComma() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "(00)(1,1)"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "(0 0)(1,1)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void duplicatedMinus() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "(--1,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void invalidMark() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "(* -1,0)"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void noRefMark() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "T.EW"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void referenceToSelf() {
            assertThatThrownBy(() -> KickPatternInterpreter.create("O.WS", "&O.WS"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}

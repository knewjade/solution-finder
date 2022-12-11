package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TSpinOrHarddropReachableTest {
    private final int maxY = 8;
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseHarddrop(int required) {
        Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "X___XXXXXX" +
                    "XX_XXXXXXX"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isFalse();
        }
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "__XXXXXXXX" +
                    "_XXXXXXXXX"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseNotReachable(int required) {
        Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__XX______" +
                    "X___XXXXXX" +
                    "XX_XXXXXXX"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isFalse();
        }
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX" +
                    "XX_XXXXXXX"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseTOther(int required) {
        Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "XX__XXXXXX" +
                    "X__XXXXXXX"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.Z, Rotate.Reverse), 2, 0, maxY)).isFalse();
        }

        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "XXXXXXXX__" +
                    "XXXXXXXXX_"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.S, Rotate.Right), 8, 1, maxY)).isTrue();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseTSpinWithoutCleared(int required) {
        Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "___XXXXXX_" +
                    "X___XXXXX_" +
                    "XX_XXXXXX_"
            );
            assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isFalse();
        }
    }

    @Nested
    class TSpinZeroOrHarddropReachableTest {
        private final Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 0, false, false
        );

        @Test
        void caseMini() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 4, 1, maxY)).isFalse();
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isTrue();
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isTrue();
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isTrue();
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isTrue();
            }
            {
                // NEO
                Field field = FieldFactory.createField("" +
                        "XXX_______" +
                        "X_________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTSS() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXX_" +
                        "X___XXXXX_" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTSD() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
            {
                // FIN
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XX________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isTrue();
            }
            {
                // ISO
                Field field = FieldFactory.createField("" +
                        "___XXXXXXX" +
                        "______XXXX" +
                        "_____XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 4, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTST() {
            {
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 3, 1, maxY)).isTrue();
            }
        }
    }

    @Nested
    class TSMOrHarddropReachableTest {
        private final Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 1, false, false
        );

        @Test
        void caseMini() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 4, 1, maxY)).isFalse();
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isFalse();
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isFalse();
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isTrue();
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isTrue();
            }
            {
                // NEO
                Field field = FieldFactory.createField("" +
                        "XXX_______" +
                        "X_________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTSS() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXX_" +
                        "X___XXXXX_" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTSD() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
            {
                // FIN
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XX________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isTrue();
            }
            {
                // ISO
                Field field = FieldFactory.createField("" +
                        "___XXXXXXX" +
                        "______XXXX" +
                        "_____XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 4, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTST() {
            {
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 3, 1, maxY)).isTrue();
            }
        }
    }

    @Nested
    class TSSOrHarddropReachableTest {
        private final Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 1, true, false
        );

        @Test
        void caseMini() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isFalse();
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isFalse();
            }
            {
                // NEO
                Field field = FieldFactory.createField("" +
                        "XXX_______" +
                        "X_________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isFalse();
            }
        }

        @Test
        void caseTSS() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXX_" +
                        "X___XXXXX_" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTSD() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
            {
                // FIN
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XX________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isTrue();
            }
            {
                // ISO
                Field field = FieldFactory.createField("" +
                        "___XXXXXXX" +
                        "______XXXX" +
                        "_____XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 4, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTST() {
            {
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 3, 1, maxY)).isTrue();
            }
        }
    }

    @Nested
    class TSDOrHarddropReachableTest {
        private final Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 2, true, false
        );

        @Test
        void caseMini() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isFalse();
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isFalse();
            }
            {
                // NEO
                Field field = FieldFactory.createField("" +
                        "XXX_______" +
                        "X_________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isFalse();
            }
        }

        @Test
        void caseTSS() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXX_" +
                        "X___XXXXX_" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isFalse();
            }
        }

        @Test
        void caseTSD() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isTrue();
            }
            {
                // FIN
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XX________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isTrue();
            }
            {
                // ISO
                Field field = FieldFactory.createField("" +
                        "___XXXXXXX" +
                        "______XXXX" +
                        "_____XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 4, 1, maxY)).isTrue();
            }
        }

        @Test
        void caseTST() {
            {
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 3, 1, maxY)).isTrue();
            }
        }
    }

    @Nested
    class TSTOrHarddropReachableTest {
        private final Reachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 3, true, false
        );

        @Test
        void caseMini() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Left), 9, 1, maxY)).isFalse();
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 0, 1, maxY)).isFalse();
            }
            {
                // NEO
                Field field = FieldFactory.createField("" +
                        "XXX_______" +
                        "X_________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isFalse();
            }
        }

        @Test
        void caseTSS() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXX_" +
                        "X___XXXXX_" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isFalse();
            }
        }

        @Test
        void caseTSD() {
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "___XXXXXXX" +
                        "X___XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Reverse), 2, 1, maxY)).isFalse();
            }
            {
                // FIN
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XX________" +
                        "XX________" +
                        "XX__XXXXXX" +
                        "XX_XXXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 2, 1, maxY)).isFalse();
            }
            {
                // ISO
                Field field = FieldFactory.createField("" +
                        "___XXXXXXX" +
                        "______XXXX" +
                        "_____XXXXX" +
                        "XXXX__XXXX" +
                        "XXXX_XXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 4, 1, maxY)).isFalse();
            }
        }

        @Test
        void caseTST() {
            {
                Field field = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXX_______" +
                        "XXX_XXXXXX" +
                        "XXX__XXXXX" +
                        "XXX_XXXXXX"
                );
                assertThat(reachable.checks(field, minoFactory.create(Piece.T, Rotate.Right), 3, 1, maxY)).isTrue();
            }
        }
    }
}
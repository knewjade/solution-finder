package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.reachable.TSpinOrHarddropReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TSpinOrHarddropCandidateTest {
    private final int maxY = 8;
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

    private final HarddropCandidate harddropCandidate = new HarddropCandidate(minoFactory, minoShifter);

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseHarddrop(int required) {
        TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "X___XXXXXX" +
                    "XX_XXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).isEmpty();
        }
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "__XXXXXXXX" +
                    "_XXXXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).isEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseNotReachable(int required) {
        TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__XX______" +
                    "X___XXXXXX" +
                    "XX_XXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Reverse));
            assertReachable(field, Piece.T, required, result);
        }
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "XX_XXXXXXX" +
                    "XX__XXXXXX" +
                    "XX_XXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Right));
            assertReachable(field, Piece.T, required, result);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseTOther(int required) {
        TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "XX__XXXXXX" +
                    "X__XXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.Z, maxY);
            assertThat(result).isEqualTo(harddropCandidate.search(field, Piece.Z, maxY));
            assertReachable(field, Piece.Z, required, result);
        }

        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "XXXXXXXX__" +
                    "XXXXXXXXX_"
            );
            Set<Action> result = candidate.search(field, Piece.S, maxY);
            assertThat(result).isEqualTo(harddropCandidate.search(field, Piece.S, maxY));
            assertReachable(field, Piece.S, required, result);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void caseTSpinWithoutCleared(int required) {
        TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "___XXXXXX_" +
                    "X___XXXXX_" +
                    "XX_XXXXXX_"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Reverse));
            assertReachable(field, Piece.T, required, result);
        }
    }

    private void assertReachable(Field field, Piece piece, int required, Set<Action> result) {
        TSpinOrHarddropReachable reachable = new TSpinOrHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, required, true, false
        );
        assertThat(result)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                });
    }

    @Nested
    class TSpinZeroOrHarddropCandidateTest {
        private final TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 0, false, false
        );
        private final TSpinOrHarddropReachable reachable = new TSpinOrHarddropReachable(
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).isEmpty();
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXX_"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(9, 1, Rotate.Left));
                assertThat(result).contains(MinimalAction.create(0, 1, Rotate.Right));
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(9, 1, Rotate.Left));
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(0, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(4, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(3, 1, Rotate.Right));
                assertReachable(field, result);
            }
        }

        private void assertReachable(Field field, Set<Action> result) {
            Piece piece = Piece.T;
            assertThat(result)
                    .allMatch(action -> {
                        Mino mino = minoFactory.create(piece, action.getRotate());
                        return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                    });
        }
    }

    @Nested
    class TSMOrHarddropCandidateTest {
        private final TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 1, false, false
        );
        private final TSpinOrHarddropReachable reachable = new TSpinOrHarddropReachable(
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).isEmpty();
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXX_"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).isEmpty();
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "XXXXXXXXX_"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(9, 1, Rotate.Left));
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(0, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(4, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(3, 1, Rotate.Right));
                assertReachable(field, result);
            }
        }

        private void assertReachable(Field field, Set<Action> result) {
            Piece piece = Piece.T;
            assertThat(result)
                    .allMatch(action -> {
                        Mino mino = minoFactory.create(piece, action.getRotate());
                        return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                    });
        }
    }

    @Nested
    class TSSOrHarddropCandidateTest {
        private final TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 1, true, false
        );
        private final TSpinOrHarddropReachable reachable = new TSpinOrHarddropReachable(
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(9, 1, Rotate.Left));
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(0, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(4, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(3, 1, Rotate.Right));
                assertReachable(field, result);
            }
        }

        private void assertReachable(Field field, Set<Action> result) {
            Piece piece = Piece.T;
            assertThat(result)
                    .allMatch(action -> {
                        Mino mino = minoFactory.create(piece, action.getRotate());
                        return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                    });
        }
    }

    @Nested
    class TSDOrHarddropCandidateTest {
        private final TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 2, true, false
        );
        private final TSpinOrHarddropReachable reachable = new TSpinOrHarddropReachable(
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(9, 1, Rotate.Left));
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(0, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(4, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(3, 1, Rotate.Right));
                assertReachable(field, result);
            }
        }

        private void assertReachable(Field field, Set<Action> result) {
            Piece piece = Piece.T;
            assertThat(result)
                    .allMatch(action -> {
                        Mino mino = minoFactory.create(piece, action.getRotate());
                        return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                    });
        }
    }

    @Nested
    class TSTOrHarddropCandidateTest {
        private final TSpinOrHarddropCandidate candidate = new TSpinOrHarddropCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 3, true, false
        );
        private final TSpinOrHarddropReachable reachable = new TSpinOrHarddropReachable(
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(9, 1, Rotate.Left));
                assertReachable(field, result);
            }
            {
                Field field = FieldFactory.createField("" +
                        "X_________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                );
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(0, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Reverse));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(2, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).doesNotContain(MinimalAction.create(4, 1, Rotate.Right));
                assertReachable(field, result);
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
                Set<Action> result = candidate.search(field, Piece.T, maxY);
                assertThat(result).contains(MinimalAction.create(3, 1, Rotate.Right));
                assertReachable(field, result);
            }
        }

        private void assertReachable(Field field, Set<Action> result) {
            Piece piece = Piece.T;
            assertThat(result)
                    .allMatch(action -> {
                        Mino mino = minoFactory.create(piece, action.getRotate());
                        return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                    });
        }
    }
}
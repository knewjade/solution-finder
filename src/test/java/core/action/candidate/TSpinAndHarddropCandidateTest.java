package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.reachable.TSpinAndHarddropReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TSpinAndHarddropCandidateTest {
    private final int maxY = 8;
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = MinoRotation.create();

    private final HarddropCandidate harddropCandidate = new HarddropCandidate(minoFactory, minoShifter);

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void caseHarddrop(int required) {
        HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required
        );
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "X___XXXXXX" +
                    "XX_XXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).isEqualTo(harddropCandidate.search(field, Piece.T, maxY));
            assertReachable(field, Piece.T, required, result);
        }
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "__XXXXXXXX" +
                    "_XXXXXXXXX"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).isEqualTo(harddropCandidate.search(field, Piece.T, maxY));
            assertReachable(field, Piece.T, required, result);
        }
        {
            Field field = FieldFactory.createField("" +
                    "__________" +
                    "__________" +
                    "__________" +
                    "XXXXXXXXX_"
            );
            Set<Action> result = candidate.search(field, Piece.T, maxY);
            assertThat(result).isEqualTo(harddropCandidate.search(field, Piece.T, maxY));
            assertReachable(field, Piece.T, required, result);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void caseNotReachable(int required) {
        HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required
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
    @ValueSource(ints = {0, 1, 2, 3})
    void caseTOther(int required) {
        HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required
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
    @ValueSource(ints = {0, 1, 2, 3})
    void caseTSpinWithoutCleared(int required) {
        HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, required
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
        TSpinAndHarddropReachable reachable = new TSpinAndHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, required
        );
        assertThat(result)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    return reachable.checks(field, mino, action.getX(), action.getY(), maxY);
                });
    }

    @Nested
    class TSpinMiniTSpinAndHarddropCandidateTest {
        private final HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 0
        );
        private final TSpinAndHarddropReachable reachable = new TSpinAndHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 0
        );

        @Test
        void caseMini() {
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
    class TSSTSpinAndHarddropCandidateTest {
        private final HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 1
        );
        private final TSpinAndHarddropReachable reachable = new TSpinAndHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 1
        );

        @Test
        void caseMini() {
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
    class TSDTSpinAndHarddropCandidateTest {
        private final HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 2
        );
        private final TSpinAndHarddropReachable reachable = new TSpinAndHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 2
        );

        @Test
        void caseMini() {
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
    class TSTTSpinAndHarddropCandidateTest {
        private final HarddropAndTSpinCandidate candidate = new HarddropAndTSpinCandidate(
                minoFactory, minoShifter, minoRotation, maxY, 3
        );
        private final TSpinAndHarddropReachable reachable = new TSpinAndHarddropReachable(
                minoFactory, minoShifter, minoRotation, maxY, 3
        );

        @Test
        void caseMini() {
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
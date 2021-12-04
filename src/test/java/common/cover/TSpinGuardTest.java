package common.cover;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TSpinGuardTest {
    @Test
    void create() {
        {
            TSpinGuard guard = new TSpinGuard(0);
            assertThat(guard)
                    .returns(false, TSpinGuard::isSatisfied)
                    .returns(false, TSpinGuard::isFailed)
                    .returns(true, TSpinGuard::isAmbiguous);
        }

        {
            TSpinGuard guard = new TSpinGuard(1);
            assertThat(guard)
                    .returns(false, TSpinGuard::isSatisfied)
                    .returns(false, TSpinGuard::isFailed)
                    .returns(true, TSpinGuard::isAmbiguous);
        }

        {
            assertThat(TSpinGuard.FAILURE)
                    .returns(false, TSpinGuard::isSatisfied)
                    .returns(true, TSpinGuard::isFailed)
                    .returns(false, TSpinGuard::isAmbiguous);
        }
    }

    @Test
    void b2bContinuousAfterStartIs0() {
        TSpinGuard guard = new TSpinGuard(0);

        // B2Bの条件がないため、失敗しない
        guard = guard.recordUnrequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        guard = guard.recordNormalClearedLine(3);
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        guard = guard.recordNormalClearedLine(4);
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);
    }

    @Test
    void requiredRequired() {
        TSpinGuard guard = new TSpinGuard(2);

        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);

        // B2B継続を達成していれば、成功を維持する
        guard = guard.recordUnrequiredTSpin();
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);

        // B2B継続を達成していれば、成功を維持する
        guard = guard.recordNormalClearedLine(3);
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);
    }

    @Test
    void requiredUnrequired() {
        TSpinGuard guard = new TSpinGuard(2);

        guard = guard.recordUnrequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        // 対象TSpinしたので、成功になる
        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);
    }

    @Test
    void fourLines() {
        TSpinGuard guard = new TSpinGuard(1);

        // 4ラインの場合はB2B継続となる
        guard = guard.recordNormalClearedLine(4);
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        // B2B継続を達成しているので、失敗しない
        guard = guard.recordNormalClearedLine(3);
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        // 対象TSpinしたので、成功になる
        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);
    }

    @Test
    void unrequiredUnrequired() {
        TSpinGuard guard = new TSpinGuard(2);

        guard = guard.recordUnrequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        // B2B継続を達成しても、対象TSpinがまだなので、確定しない
        guard = guard.recordUnrequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        // B2B継続を達成しているので、失敗にはならない
        guard = guard.recordNormalClearedLine(3);
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(true, TSpinGuard::isAmbiguous);

        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(true, TSpinGuard::isSatisfied)
                .returns(false, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);
    }

    @Test
    void normalClearedLine() {
        TSpinGuard guard = new TSpinGuard(1);

        guard = guard.recordNormalClearedLine(3);
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(true, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);

        // 1度失敗した後は、成功には遷移しない
        guard = guard.recordRequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(true, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);

        guard = guard.recordUnrequiredTSpin();
        assertThat(guard)
                .returns(false, TSpinGuard::isSatisfied)
                .returns(true, TSpinGuard::isFailed)
                .returns(false, TSpinGuard::isAmbiguous);
    }
}
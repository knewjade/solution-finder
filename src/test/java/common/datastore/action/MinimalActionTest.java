package common.datastore.action;

import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinimalActionTest {
    @Test
    void testGetter() throws Exception {
        MinimalAction action = MinimalAction.create(4, 5, Rotate.Spawn);
        assertThat(action)
                .returns(Rotate.Spawn, Action::getRotate)
                .returns(4, Action::getX)
                .returns(5, Action::getY);
    }

    @Test
    void testEqual() throws Exception {
        MinimalAction action = MinimalAction.create(4, 5, Rotate.Spawn);
        assertThat(action.equals(MinimalAction.create(4, 5, Rotate.Spawn))).isTrue();
        assertThat(action.equals(MinimalAction.create(7, 5, Rotate.Spawn))).isFalse();
        assertThat(action.equals(MinimalAction.create(4, 21, Rotate.Spawn))).isFalse();
        assertThat(action.equals(MinimalAction.create(4, 5, Rotate.Right))).isFalse();
    }

    @Test
    void testHashCode() throws Exception {
        MinimalAction action = MinimalAction.create(4, 5, Rotate.Spawn);
        assertThat(MinimalAction.create(4, 5, Rotate.Spawn).hashCode()).isEqualTo(action.hashCode());
        assertThat(MinimalAction.create(2, 5, Rotate.Spawn).hashCode()).isNotEqualTo(action.hashCode());
        assertThat(MinimalAction.create(4, 12, Rotate.Spawn).hashCode()).isNotEqualTo(action.hashCode());
        assertThat(MinimalAction.create(4, 5, Rotate.Right).hashCode()).isNotEqualTo(action.hashCode());
    }

    @Test
    void testCompareTo() throws Exception {
        MinimalAction action1 = MinimalAction.create(4, 5, Rotate.Spawn);
        MinimalAction action2 = MinimalAction.create(4, 5, Rotate.Spawn);
        MinimalAction action3 = MinimalAction.create(4, 13, Rotate.Spawn);
        MinimalAction action4 = MinimalAction.create(4, 13, Rotate.Reverse);

        assertThat(action1.compareTo(action2)).isEqualTo(0);

        assertThat(action1.compareTo(action3)).isNotEqualTo(0);
        assertThat(action1.compareTo(action4)).isNotEqualTo(0);
        assertThat(action3.compareTo(action4)).isNotEqualTo(0);

        assert action1.compareTo(action3) < 0 && action3.compareTo(action4) < 0;
        assertThat(action1.compareTo(action4)).isLessThan(0);
    }
}
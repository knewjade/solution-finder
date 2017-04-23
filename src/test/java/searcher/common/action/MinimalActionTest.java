package searcher.common.action;

import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class MinimalActionTest {
    @Test
    public void testGetter() throws Exception {
        MinimalAction action = MinimalAction.create(4, 5, Rotate.Spawn);
        assertThat(action.getRotate(), is(Rotate.Spawn));
        assertThat(action.getX(), is(4));
        assertThat(action.getY(), is(5));
    }

    @Test
    public void testEqual() throws Exception {
        MinimalAction action = MinimalAction.create(4, 5, Rotate.Spawn);
        assertThat(action.equals(MinimalAction.create(4, 5, Rotate.Spawn)), is(true));
        assertThat(action.equals(MinimalAction.create(7, 5, Rotate.Spawn)), is(false));
        assertThat(action.equals(MinimalAction.create(4, 21, Rotate.Spawn)), is(false));
        assertThat(action.equals(MinimalAction.create(4, 5, Rotate.Right)), is(false));
    }

    @Test
    public void testHashCode() throws Exception {
        MinimalAction action = MinimalAction.create(4, 5, Rotate.Spawn);
        assertThat(MinimalAction.create(4, 5, Rotate.Spawn).hashCode(), is(action.hashCode()));
        assertThat(MinimalAction.create(2, 5, Rotate.Spawn).hashCode(), is(not(action.hashCode())));
        assertThat(MinimalAction.create(4, 12, Rotate.Spawn).hashCode(), is(not(action.hashCode())));
        assertThat(MinimalAction.create(4, 5, Rotate.Right).hashCode(), is(not(action.hashCode())));
    }

    @Test
    public void testCompareTo() throws Exception {
        MinimalAction action1 = MinimalAction.create(4, 5, Rotate.Spawn);
        MinimalAction action2 = MinimalAction.create(4, 5, Rotate.Spawn);
        MinimalAction action3 = MinimalAction.create(4, 13, Rotate.Spawn);
        MinimalAction action4 = MinimalAction.create(4, 13, Rotate.Reverse);

        assertThat(action1.compareTo(action2), is(0));

        assertThat(action1.compareTo(action3), is(not(0)));
        assertThat(action1.compareTo(action4), is(not(0)));
        assertThat(action3.compareTo(action4), is(not(0)));

        assert action1.compareTo(action3) < 0 && action3.compareTo(action4) < 0;
        assertThat(action1.compareTo(action4), is(lessThan(0)));
    }
}
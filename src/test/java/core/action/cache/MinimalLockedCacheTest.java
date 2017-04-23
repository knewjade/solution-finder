package core.action.cache;

import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MinimalLockedCacheTest {
    @Test
    public void testLockedCache6() throws Exception {
        int height = 6;
        MinimalLockedCache cache = new MinimalLockedCache(height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                for (Rotate rotate : Rotate.values()) {
                    assertThat(cache.isVisit(x, y, rotate), is(false));
                    cache.visit(x, y, rotate);
                    assertThat(cache.isVisit(x, y, rotate), is(true));
                }
            }
        }
    }

    @Test
    public void testLockedCache12() throws Exception {
        int height = 12;
        MinimalLockedCache cache = new MinimalLockedCache(height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                for (Rotate rotate : Rotate.values()) {
                    assertThat(cache.isVisit(x, y, rotate), is(false));
                    cache.visit(x, y, rotate);
                    assertThat(cache.isVisit(x, y, rotate), is(true));
                }
            }
        }
    }

    @Test
    public void testLockedCache24() throws Exception {
        int height = 24;
        MinimalLockedCache cache = new MinimalLockedCache(height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                for (Rotate rotate : Rotate.values()) {
                    assertThat(cache.isVisit(x, y, rotate), is(false));
                    cache.visit(x, y, rotate);
                    assertThat(cache.isVisit(x, y, rotate), is(true));
                }
            }
        }
    }

    @Test
    public void testLockedCache48() throws Exception {
        int height = 48;
        MinimalLockedCache cache = new MinimalLockedCache(height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                for (Rotate rotate : Rotate.values()) {
                    assertThat(cache.isVisit(x, y, rotate), is(false));
                    cache.visit(x, y, rotate);
                    assertThat(cache.isVisit(x, y, rotate), is(true));
                }
            }
        }
    }
}

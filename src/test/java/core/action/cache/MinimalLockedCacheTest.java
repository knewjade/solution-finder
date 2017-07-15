package core.action.cache;

import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinimalLockedCacheTest {
    @Test
    void testLockedCache6() throws Exception {
        int height = 6;
        assertCache(height);
    }

    private void assertCache(int height) {
        MinimalLockedCache cache = new MinimalLockedCache(height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                for (Rotate rotate : Rotate.values()) {
                    assertThat(cache.isVisit(x, y, rotate)).isFalse();
                    cache.visit(x, y, rotate);
                    assertThat(cache.isVisit(x, y, rotate)).isTrue();
                }
            }
        }

        cache.clear();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < 10; x++) {
                for (Rotate rotate : Rotate.values()) {
                    assertThat(cache.isVisit(x, y, rotate)).isFalse();
                }
            }
        }
    }

    @Test
    void testLockedCache12() throws Exception {
        int height = 12;
        assertCache(height);
    }

    @Test
    void testLockedCache24() throws Exception {
        int height = 24;
        assertCache(height);
    }

    @Test
    void testLockedCache48() throws Exception {
        int height = 48;
        assertCache(height);
    }
}

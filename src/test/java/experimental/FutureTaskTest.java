package experimental;

import org.junit.jupiter.api.Test;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;

class FutureTaskTest {
    // Task.runを何回実行しても、1度しか実行されないことを確認
    @Test
    void testFutureTask() throws Exception {
        assertTimeout(ofSeconds(1), () -> {
            AtomicInteger counter = new AtomicInteger();
            FutureTask<Boolean> task = new FutureTask<>(() -> {
                counter.incrementAndGet();
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(10L));
                return true;
            });

            for (int number = 0; number < 10000; number++)
                task.run();

            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(300L));

            assertThat(task.get()).isTrue();
            assertThat(counter.get()).isEqualTo(1);
        });
    }
}

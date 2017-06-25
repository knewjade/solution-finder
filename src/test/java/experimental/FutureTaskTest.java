package experimental;

import org.junit.Test;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FutureTaskTest {
    // Task.runを何回実行しても、1度しか実行されないことを確認
    @Test(timeout = 1000L)
    public void testFutureTask() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        FutureTask<Boolean> task = new FutureTask<>(() -> {
            counter.incrementAndGet();
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(10L));
            return true;
        });

        for (int number = 0; number < 10000; number++)
            task.run();

        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(300L));

        assertThat(task.get(), is(true));
        assertThat(counter.get(), is(1));
    }
}

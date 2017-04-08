package misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stopwatch {
    private static final int STOPPING_VALUE = -1;

    private int counter = 0;
    private double average = 0.0;
    private long start = STOPPING_VALUE;

    public static Stopwatch createStartedStopwatch() {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        return stopwatch;
    }

    public static Stopwatch createStoppedStopwatch() {
        return new Stopwatch();
    }

    private Stopwatch() {
    }

    public void start() {
        this.start = System.nanoTime();
    }

    public void stop() {
        if (!isStarted())
            return;

        long end = System.nanoTime();
        long time = end - start;
        counter += 1;
        average += (time - average) / counter;
        start = -1;
    }

    public void clear() {
        counter = 0;
        average = 0.0;
        start = -1;
    }

    private boolean isStarted() {
        return 0L <= start;
    }

    public String toMessage(TimeUnit... timeUnits) {
        List<String> strings = new ArrayList<>();
        for (TimeUnit timeUnit : timeUnits) {
            long convert = timeUnit.convert((long) average, TimeUnit.NANOSECONDS);
            strings.add(String.format("avg.time = %d %s [%d counts]", convert, toMeasure(timeUnit), counter));
        }
        return String.join(System.lineSeparator(), strings);
    }

    private String toMeasure(TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "sec";
            case MINUTES:
                return "min";
            case HOURS:
                return "hour";
            case DAYS:
                return "day";
        }
        throw new UnsupportedOperationException();
    }
}

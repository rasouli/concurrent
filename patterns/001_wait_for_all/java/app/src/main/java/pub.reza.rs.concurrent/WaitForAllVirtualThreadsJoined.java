package pub.reza.rs.concurrent;


import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class WaitForAllVirtualThreadsJoined {
    private static final int WORKERS = 10;
    private static final int MIN_WAIT_SECONDS = 1;
    private static final int MAX_WAIT_SECONDS = 5;

    private static final Thread.UncaughtExceptionHandler logUncaughtExceptionHandler = (thread, throwable) -> System.out.printf("Thread %s threw an Exception. %n", throwable.getMessage());


    public static void main(String[] args) throws InterruptedException {
        var workers = IntStream.range(0, WORKERS).mapToObj(i ->
                Thread.ofVirtual()
                        .name(String.format("worker-%d", i))
                        .uncaughtExceptionHandler(logUncaughtExceptionHandler)
                        .start(() -> {
                            var waitTimeSeconds = ThreadLocalRandom.current().nextInt(MIN_WAIT_SECONDS, MAX_WAIT_SECONDS);
                            System.out.printf("%s Started with wait time %d second(s).%n", Thread.currentThread().getName(), waitTimeSeconds);
                            try {
                                Thread.sleep(TimeUnit.SECONDS.toMillis(waitTimeSeconds));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.printf("%s finished.%n", Thread.currentThread().getName());
                        })
        ).toList();

        for (Thread worker : workers) {
            worker.join();
        }
    }

}

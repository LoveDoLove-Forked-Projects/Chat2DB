package ai.chat2db.spi.util;

public final class ElapsedTimer implements AutoCloseable {

    private final long startTime;
    private long endTime;

    private ElapsedTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public static ElapsedTimer start() {
        return new ElapsedTimer();
    }

    public long elapsedMs() {
        if (endTime == 0) {
            return System.currentTimeMillis() - startTime;
        }
        return endTime - startTime;
    }

    @Override
    public void close() {
        if (endTime == 0) {
            endTime = System.currentTimeMillis();
        }
    }
}

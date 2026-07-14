package ai.chat2db.spi;

/**
 * Streams a SQL file and returns one executable SQL fragment at a time.
 */
public interface ISQLFileSplitter extends AutoCloseable {

    /**
     * Reads the next SQL fragment from the underlying file or stream.
     *
     * @return next SQL fragment, or {@code null} when no more content is available.
     */
    String nextContent();
}

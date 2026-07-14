package ai.chat2db.community.domain.api.service.task;


@FunctionalInterface
public interface ITaskProgressListener {

    /**
     * Reports parsed-byte and statement-count progress.
     *
     * @param bytesRead number of bytes read from the source.
     * @param statementsParsed number of SQL statements parsed so far.
     */
    void onProgress(long bytesRead, int statementsParsed);
}

package ai.chat2db.community.jcef.listener;


@FunctionalInterface
public interface IProgressListener {


    void onProgress(long bytesWritten);
}

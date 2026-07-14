package ai.chat2db.community.jcef.listener;

import ai.chat2db.community.tools.desktop.DownloadCompleteRegistry;
import ai.chat2db.community.jcef.utils.OSOperateUtil;

public class FileManagerService {

    public FileManagerService() {
        DownloadCompleteRegistry.subscribe(OSOperateUtil::openFileManager);
    }
}

package ai.chat2db.community.web.api.util;

import cn.hutool.core.io.FileUtil;

import java.io.File;

public class DownloadUtil {

    public static File createDownloadFile(String prefix, String suffix, boolean isReCreat) {
        File userHomeDir = FileUtil.getUserHomeDir();
        if (userHomeDir == null) {
            return FileUtil.createTempFile(prefix, suffix, isReCreat);
        }
        try {
            File downloadDir = new File(userHomeDir, "Downloads");
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            return FileUtil.createTempFile(prefix, suffix, downloadDir, isReCreat);
        } catch (Exception e) {
            return FileUtil.createTempFile(prefix, suffix, isReCreat);
        }
    }
}

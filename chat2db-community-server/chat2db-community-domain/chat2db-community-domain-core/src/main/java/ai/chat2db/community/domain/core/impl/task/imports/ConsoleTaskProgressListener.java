package ai.chat2db.community.domain.core.impl.task.imports;

import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;
import ai.chat2db.community.domain.api.service.task.ITaskProgressListener;
import cn.hutool.core.date.DateUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

public class ConsoleTaskProgressListener implements ITaskProgressListener {


    private final ImportAsyncContext context;
    private final long size;

    public ConsoleTaskProgressListener(ImportAsyncContext context) {
        this.context = context;
        try {
            this.size = Files.size(context.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onProgress(long l, int i) {
        long progress = l * 100 / size;
        int p = Integer.parseInt(progress + "");
        if (p >= 100) {
            p = 99;
        }
        context.setProgress(p);
        context.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " all bytes:" + size + ",current bytes:" + l + ",progress:" + progress + "%" + " ,statement:" + i);
    }
}

package ai.chat2db.community.web.api.adapter.file;

import ai.chat2db.community.domain.api.service.file.IUploadFileService;
import ai.chat2db.community.tools.util.ConfigUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Component
public class MultipartUploadFileAdapter implements IUploadFileService<MultipartFile> {

    @Override
    public String extension(MultipartFile file) {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex + 1).toLowerCase() : "";
    }

    @Override
    public File transferToTempFile(MultipartFile file) throws IOException {
        File temp = new File(ConfigUtils.getBasePath() + File.separator + UUID.randomUUID() + ".tmp");
        file.transferTo(temp);
        return temp;
    }
}

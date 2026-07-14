package ai.chat2db.community.domain.api.service.file;

import ai.chat2db.community.domain.api.enums.file.ConfigFileTypeEnum;
import ai.chat2db.community.tools.exception.BusinessException;

import java.io.File;
import java.io.IOException;

/**
 * Handles uploaded file metadata and temporary storage.
 */
public interface IUploadFileService<T> {

    String extension(T file);

    File transferToTempFile(T file) throws IOException;

    default File transferToTempFile(T file, ConfigFileTypeEnum expectedType) {
        if (expectedType != null && !expectedType.name().equalsIgnoreCase(extension(file))) {
            throw new BusinessException("file.type.unsupported");
        }
        return transferToTempFileOrThrow(file);
    }

    default File transferToTempFileOrThrow(T file) {
        try {
            return transferToTempFile(file);
        } catch (IOException e) {
            throw new BusinessException("file.upload.failed", new Object[]{e.getMessage()}, e);
        }
    }
}

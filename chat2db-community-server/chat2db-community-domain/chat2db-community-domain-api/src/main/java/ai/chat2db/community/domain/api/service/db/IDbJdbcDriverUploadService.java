package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.tools.exception.BusinessException;

import java.io.IOException;
import java.util.List;

/**
 * Stores uploaded JDBC driver files in the managed driver directory.
 */
public interface IDbJdbcDriverUploadService<T> {

    List<String> upload(T files) throws IOException;

    default List<String> uploadOrThrow(T files) {
        try {
            return upload(files);
        } catch (IOException e) {
            throw new BusinessException("jdbc.driver.uploadFailed", new Object[]{e.getMessage()}, e);
        }
    }
}

package ai.chat2db.community.web.api.converter.task;

import ai.chat2db.community.domain.api.model.task.TaskDownload;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Component
public class TaskDownloadWebConverter {

    public ResponseEntity<Resource> toResponse(TaskDownload download) {
        try {
            Resource resource = new UrlResource(download.getFileUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid task download URI", e);
        }
    }
}

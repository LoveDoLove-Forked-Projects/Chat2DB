package ai.chat2db.community.domain.api.model.db;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class CellValueDownload {

    private InputStream inputStream;

    private String fileName;

    private String contentType;
}

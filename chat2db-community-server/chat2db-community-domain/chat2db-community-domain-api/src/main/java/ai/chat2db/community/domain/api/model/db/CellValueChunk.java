package ai.chat2db.community.domain.api.model.db;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CellValueChunk {

    private String value;

    private long offset;

    private long nextOffset;

    private boolean eof;

    private Long sizeBytes;

    private Long sizeChars;

    private String encoding;

    private String contentType;

    private String displayMode;
}

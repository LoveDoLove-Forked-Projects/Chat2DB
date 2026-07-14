package ai.chat2db.community.web.api.model.response.db.cell;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CellValueChunkResponse {

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

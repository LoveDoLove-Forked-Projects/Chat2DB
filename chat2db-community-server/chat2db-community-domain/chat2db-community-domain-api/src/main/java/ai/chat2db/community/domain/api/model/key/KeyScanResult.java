package ai.chat2db.community.domain.api.model.key;

import java.util.List;

import lombok.Data;

@Data
public class KeyScanResult {

    private List<KeyEntry> keys;

    private String nextCursor;

    private Boolean hasMore;

    private Boolean complete;

    private String stoppedReason;

    private Integer scanCalls;

    private Integer keysReturned;

    private Long elapsedMs;
}

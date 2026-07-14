package ai.chat2db.plugin.redis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisKeyScanResult {

    private List<RedisKey> keys;

    private String nextCursor;

    private Boolean hasMore;

    private Boolean complete;

    private String stoppedReason;

    private Integer scanCalls;

    private Integer keysReturned;

    private Long elapsedMs;
}

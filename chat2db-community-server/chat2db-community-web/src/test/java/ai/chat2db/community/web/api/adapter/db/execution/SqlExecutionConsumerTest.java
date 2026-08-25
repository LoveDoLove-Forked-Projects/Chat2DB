package ai.chat2db.community.web.api.adapter.db.execution;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlExecutionConsumerTest {

    @Test
    void completionMetadataOmitsRowsAndPreservesFinalPagingState() {
        ExecuteResponse result = ExecuteResponse.builder()
                .success(Boolean.TRUE)
                .dataList(List.of(
                        List.of(ResultCell.of("1")),
                        List.of(ResultCell.of("2"))))
                .pageNo(1)
                .pageSize(50_000)
                .fuzzyTotal("50000+")
                .hasNextPage(Boolean.TRUE)
                .resultSetId(1)
                .build();

        ExecuteResponse metadata = SqlExecutionConsumer.completionMetadata(result);

        assertNotSame(result, metadata);
        assertTrue(metadata.getDataList().isEmpty());
        assertEquals(2, result.getDataList().size());
        assertEquals(50_000, metadata.getPageSize());
        assertEquals("50000+", metadata.getFuzzyTotal());
        assertEquals(Boolean.TRUE, metadata.getHasNextPage());
        assertEquals(1, metadata.getResultSetId());
    }
}

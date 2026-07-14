package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.db.LargeValueToken;
import ai.chat2db.community.domain.api.model.request.db.DbLargeValueTokensAttachRequest;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.model.LoginUser;
import ai.chat2db.community.tools.util.ContextUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LargeValueTokenServiceTest {

    @AfterEach
    void tearDown() {
        ContextUtils.removeContext();
    }

    @Test
    void bindsTokenToDatasourcePrimaryKeyColumnAndOwner() {
        DbLargeValueTokenServiceImpl service = new DbLargeValueTokenServiceImpl();
        setContext(7L, 9L);
        ResultCell cell = largeTextCell();

        service.attachTokens(attachLargeValueTokensRequest(1L, "db", "public", "doc",
                List.of(rowNumberHeader(), primaryKeyHeader(), valueHeader()),
                List.of(List.of(ResultCell.builder().value("1").build(), ResultCell.builder().value("42").build(), cell)),
                true));

        assertNotNull(cell.getLargeValueId());
        LargeValueToken token = service.requireValid(cell.getLargeValueId());
        assertEquals(1L, token.getDataSourceId());
        assertEquals("content", token.getColumnName());
        assertEquals(Map.of("id", "42"), token.getPrimaryKey());
        assertEquals(7L, token.getUserId());
        assertEquals(9L, token.getOrganizationId());
    }

    @Test
    void doesNotIssueTokenWhenRowHasNoPrimaryKeyIdentity() {
        DbLargeValueTokenServiceImpl service = new DbLargeValueTokenServiceImpl();
        setContext(7L, 9L);
        ResultCell cell = largeTextCell();

        service.attachTokens(attachLargeValueTokensRequest(1L, "db", "public", "doc",
                List.of(rowNumberHeader(), valueHeader()),
                List.of(List.of(ResultCell.builder().value("1").build(), cell)),
                true));

        assertTrue(cell.getLargeValueId() == null || cell.getLargeValueId().isEmpty());
        assertNotNull(cell.getUnsupportedReason());
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        DbLargeValueTokenServiceImpl service = new DbLargeValueTokenServiceImpl();
        setContext(7L, 9L);
        ResultCell cell = largeTextCell();
        service.attachTokens(attachLargeValueTokensRequest(1L, "db", "public", "doc",
                List.of(rowNumberHeader(), primaryKeyHeader(), valueHeader()),
                List.of(List.of(ResultCell.builder().value("1").build(), ResultCell.builder().value("42").build(), cell)),
                true));
        expire(service, cell.getLargeValueId());

        assertThrows(BusinessException.class, () -> service.requireValid(cell.getLargeValueId()));
    }

    @Test
    void rejectsDifferentOwner() {
        DbLargeValueTokenServiceImpl service = new DbLargeValueTokenServiceImpl();
        setContext(7L, 9L);
        ResultCell cell = largeTextCell();
        service.attachTokens(attachLargeValueTokensRequest(1L, "db", "public", "doc",
                List.of(rowNumberHeader(), primaryKeyHeader(), valueHeader()),
                List.of(List.of(ResultCell.builder().value("1").build(), ResultCell.builder().value("42").build(), cell)),
                true));

        setContext(8L, 9L);

        assertThrows(BusinessException.class, () -> service.requireValid(cell.getLargeValueId()));
    }

    @SuppressWarnings("unchecked")
    private static void expire(DbLargeValueTokenServiceImpl service, String tokenId) throws Exception {
        Field field = DbLargeValueTokenServiceImpl.class.getDeclaredField("tokenCache");
        field.setAccessible(true);
        Map<String, LargeValueToken> tokenCache = (Map<String, LargeValueToken>) field.get(service);
        tokenCache.get(tokenId).setExpiresAt(Instant.now().minusSeconds(1));
    }

    private static void setContext(Long userId, Long organizationId) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        ContextUtils.setContext(Context.builder()
                .loginUser(loginUser)
                .organizationId(organizationId)
                .build());
    }

    private static Header rowNumberHeader() {
        return Header.builder().name("row").build();
    }

    private static DbLargeValueTokensAttachRequest attachLargeValueTokensRequest(Long dataSourceId,
                                                                               String databaseName,
                                                                               String schemaName,
                                                                               String tableName,
                                                                               List<Header> headers,
                                                                               List<List<ResultCell>> dataList,
                                                                               boolean canEdit) {
        DbLargeValueTokensAttachRequest request = new DbLargeValueTokensAttachRequest();
        request.setDataSourceId(dataSourceId);
        request.setDatabaseName(databaseName);
        request.setSchemaName(schemaName);
        request.setTableName(tableName);
        request.setHeaders(headers);
        request.setDataList(dataList);
        request.setCanEdit(canEdit);
        return request;
    }

    private static Header primaryKeyHeader() {
        return Header.builder().name("id").columnName("id").primaryKey(true).build();
    }

    private static Header valueHeader() {
        return Header.builder().name("content").columnName("content").columnType("LONGTEXT").build();
    }

    private static ResultCell largeTextCell() {
        return ResultCell.builder()
                .value("[LONGTEXT] 20.00 MB")
                .largeValue(true)
                .truncated(true)
                .valueType("TEXT")
                .columnType("LONGTEXT")
                .sizeBytes(20L * 1024L * 1024L)
                .build();
    }
}

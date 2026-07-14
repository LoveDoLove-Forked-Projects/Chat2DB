package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.db.LargeValueToken;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import ai.chat2db.community.domain.api.model.request.db.DbLargeValueTokensAttachRequest;
import ai.chat2db.community.domain.api.service.db.IDbLargeValueTokenService;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.model.LoginUser;
import ai.chat2db.community.tools.util.ContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DbLargeValueTokenServiceImpl implements IDbLargeValueTokenService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);
    private static final String UNSUPPORTED_REASON_KEY = "largeCellValue.fullValueUnsupported";

    private final Map<String, LargeValueToken> tokenCache = new ConcurrentHashMap<>();

    @Override
    public void attachTokens(DbLargeValueTokensAttachRequest attachLargeValueTokensRequest) {
        Long dataSourceId = attachLargeValueTokensRequest == null ? null : attachLargeValueTokensRequest.getDataSourceId();
        String databaseName = attachLargeValueTokensRequest == null ? null : attachLargeValueTokensRequest.getDatabaseName();
        String schemaName = attachLargeValueTokensRequest == null ? null : attachLargeValueTokensRequest.getSchemaName();
        String tableName = attachLargeValueTokensRequest == null ? null : attachLargeValueTokensRequest.getTableName();
        List<Header> headers = attachLargeValueTokensRequest == null ? null : attachLargeValueTokensRequest.getHeaders();
        List<List<ResultCell>> dataList = attachLargeValueTokensRequest == null ? null : attachLargeValueTokensRequest.getDataList();
        boolean canEdit = attachLargeValueTokensRequest != null && attachLargeValueTokensRequest.isCanEdit();
        if (CollectionUtils.isEmpty(headers) || CollectionUtils.isEmpty(dataList)) {
            return;
        }
        cleanupExpired();
        Map<Integer, Header> primaryKeyHeaders = getPrimaryKeyHeaders(headers);
        for (List<ResultCell> row : dataList) {
            if (row == null) {
                continue;
            }
            for (int colIndex = 0; colIndex < row.size() && colIndex < headers.size(); colIndex++) {
                ResultCell cell = row.get(colIndex);
                if (cell == null || !cell.isLargeValue()) {
                    continue;
                }
                Header header = headers.get(colIndex);
                String columnName = StringUtils.defaultIfBlank(header.getColumnName(), header.getName());
                if (!canEdit || CollectionUtils.isEmpty(primaryKeyHeaders.values())
                        || StringUtils.isBlank(tableName) || StringUtils.isBlank(columnName)) {
                    cell.setUnsupportedReason(UNSUPPORTED_REASON_KEY);
                    continue;
                }
                Map<String, Object> primaryKey = getPrimaryKeyValues(primaryKeyHeaders, row);
                if (primaryKey.isEmpty()) {
                    cell.setUnsupportedReason(UNSUPPORTED_REASON_KEY);
                    continue;
                }
                LargeValueToken token = createToken(dataSourceId, databaseName, schemaName, tableName,
                        columnName, primaryKey, cell);
                tokenCache.put(token.getId(), token);
                cell.setLargeValueId(token.getId());
            }
        }
    }

    @Override
    public LargeValueToken requireValid(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("largeCellValue.tokenRequired");
        }
        LargeValueToken token = tokenCache.get(id);
        if (token == null) {
            throw new BusinessException("largeCellValue.tokenExpired");
        }
        if (Instant.now().isAfter(token.getExpiresAt())) {
            tokenCache.remove(id);
            throw new BusinessException("largeCellValue.tokenExpired");
        }
        validateOwner(token);
        return token;
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        tokenCache.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpiresAt()));
    }

    private Map<Integer, Header> getPrimaryKeyHeaders(List<Header> headers) {
        Map<Integer, Header> primaryKeyHeaders = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            Header header = headers.get(i);
            if (Boolean.TRUE.equals(header.getPrimaryKey())) {
                primaryKeyHeaders.put(i, header);
            }
        }
        return primaryKeyHeaders;
    }

    private Map<String, Object> getPrimaryKeyValues(Map<Integer, Header> primaryKeyHeaders, List<ResultCell> dataRow) {
        Map<String, Object> primaryKey = new LinkedHashMap<>();
        for (Map.Entry<Integer, Header> entry : primaryKeyHeaders.entrySet()) {
            int index = entry.getKey();
            if (index >= dataRow.size()) {
                return Map.of();
            }
            String columnName = StringUtils.defaultIfBlank(entry.getValue().getColumnName(), entry.getValue().getName());
            ResultCell cell = dataRow.get(index);
            primaryKey.put(columnName, cell == null ? null : getLocatorValue(cell));
        }
        return primaryKey;
    }

    private LargeValueToken createToken(Long dataSourceId, String databaseName, String schemaName, String tableName,
                                        String columnName, Map<String, Object> primaryKey, ResultCell cell) {
        Context context = ContextUtils.queryContext();
        Long organizationId = context == null ? null : context.getOrganizationId();
        LoginUser loginUser = context == null ? null : context.getLoginUser();
        Long userId = loginUser == null ? null : loginUser.getId();
        return LargeValueToken.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .dataSourceId(dataSourceId)
                .databaseName(databaseName)
                .schemaName(schemaName)
                .tableName(tableName)
                .columnName(columnName)
                .primaryKey(primaryKey)
                .userId(userId)
                .organizationId(organizationId)
                .expiresAt(Instant.now().plus(DEFAULT_TTL))
                .valueType(cell.getValueType())
                .sqlType(cell.getSqlType())
                .columnType(cell.getColumnType())
                .sizeBytes(cell.getSizeBytes())
                .sizeChars(cell.getSizeChars())
                .build();
    }

    private void validateOwner(LargeValueToken token) {
        Context context = ContextUtils.queryContext();
        Long organizationId = context == null ? null : context.getOrganizationId();
        LoginUser loginUser = context == null ? null : context.getLoginUser();
        Long userId = loginUser == null ? null : loginUser.getId();
        if (!Objects.equals(token.getUserId(), userId) || !Objects.equals(token.getOrganizationId(), organizationId)) {
            throw new BusinessException("largeCellValue.tokenForbidden");
        }
    }

    private Object getLocatorValue(ResultCell cell) {
        return cell.getRawValue() == null ? cell.getValue() : cell.getRawValue();
    }
}

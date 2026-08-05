package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.request.db.DbExecuteResultEnhanceRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTableQueryRequest;
import ai.chat2db.community.domain.api.service.db.IDbExecuteResultEnhanceService;
import ai.chat2db.community.domain.api.service.db.IDbTableService;
import ai.chat2db.community.domain.core.util.MetaNameUtils;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.community.domain.api.enums.plugin.ResultSetEditorTypeEnum;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.ResultSetEditorMetadata;
import ai.chat2db.community.domain.api.model.metadata.PrimaryKey;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.model.request.TableMetadataRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ExecuteResultHeaderEnhancer implements IDbExecuteResultEnhanceService {

    private final IDbTableService tableService;

    public ExecuteResultHeaderEnhancer(IDbTableService tableService) {
        this.tableService = tableService;
    }

    @Override
    public void enhance(DbExecuteResultEnhanceRequest enhanceExecuteResultRequest) {
        ExecuteResponse executeResult = enhanceExecuteResultRequest == null ? null : enhanceExecuteResultRequest.getExecuteResult();
        Long dataSourceId = enhanceExecuteResultRequest == null ? null : enhanceExecuteResultRequest.getDataSourceId();
        String databaseName = enhanceExecuteResultRequest == null ? null : enhanceExecuteResultRequest.getDatabaseName();
        String schemaName = enhanceExecuteResultRequest == null ? null : enhanceExecuteResultRequest.getSchemaName();
        if (executeResult == null || !Boolean.TRUE.equals(executeResult.getSuccess()) || !executeResult.isCanEdit()
                || CollectionUtils.isEmpty(executeResult.getHeaderList())) {
            return;
        }
        executeResult.setHeaderList(setColumnInfo(executeResult.getHeaderList(), executeResult.getTableName(),
                dataSourceId, schemaName, databaseName));
    }

    private List<Header> setColumnInfo(List<Header> headers, String tableName, Long dataSourceId,
                                       String schemaName, String databaseName) {
        try {
            DbTableQueryRequest tableQueryParam = new DbTableQueryRequest();
            tableQueryParam.setDataSourceId(dataSourceId);
            tableQueryParam.setSchemaName(schemaName);
            tableQueryParam.setDatabaseName(databaseName);
            MetaNameUtils.buildRequest(tableQueryParam, tableName);
            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
            if (connectInfo != null) {
                if (tableQueryParam.getDataSourceId() == null) {
                    tableQueryParam.setDataSourceId(connectInfo.getDataSourceId());
                }
                if (StringUtils.isBlank(tableQueryParam.getDatabaseName()) && StringUtils.isNotBlank(connectInfo.getDatabaseName())) {
                    tableQueryParam.setDatabaseName(connectInfo.getDatabaseName());
                }
                if (StringUtils.isBlank(tableQueryParam.getSchemaName()) && StringUtils.isNotBlank(connectInfo.getSchemaName())) {
                    tableQueryParam.setSchemaName(connectInfo.getSchemaName());
                }
            }
            MetaNameUtils.buildRequest(tableQueryParam, tableName);
            tableQueryParam.setRefresh(true);
            List<TableColumn> columns = tableService.queryColumns(tableQueryParam);
            if (CollectionUtils.isEmpty(columns)) {
                return headers;
            }
            Map<String, TableColumn> columnMap = new HashMap<>();
            Map<String, List<TableColumn>> caseInsensitiveColumnMap = new HashMap<>();
            for (TableColumn column : columns) {
                if (StringUtils.isBlank(column.getName())) {
                    continue;
                }
                columnMap.putIfAbsent(column.getName(), column);
                caseInsensitiveColumnMap.computeIfAbsent(normalizeColumnName(column.getName()), key -> new ArrayList<>())
                        .add(column);
            }

            Connection connection = Chat2DBContext.getConnection();
            IDbMetaData metaData = Chat2DBContext.getDbMetaData();
            List<PrimaryKey> primaryKeys = metaData.getPrimaryKeys(connection,
                    new TableMetadataRequest(tableQueryParam.getDatabaseName(), tableQueryParam.getSchemaName(),
                            tableQueryParam.getTableName()));
            if (CollectionUtils.isNotEmpty(primaryKeys)) {
                for (PrimaryKey primaryKey : primaryKeys) {
                    TableColumn tableColumn = findColumn(columnMap, caseInsensitiveColumnMap,
                            primaryKey.getColumnName());
                    if (Objects.nonNull(tableColumn)) {
                        tableColumn.setPrimaryKey(true);
                    }
                }
            }
            boolean supportsEditorOptions = metaData.supportsResultSetEditorOptions();
            Map<TableColumn, ResultSetEditorMetadata> editorMetadataByColumn = resolveEditorMetadata(
                    connection, columns, metaData, supportsEditorOptions);

            for (Header header : headers) {
                TableColumn tableColumn = findColumn(columnMap, caseInsensitiveColumnMap,
                        header.getColumnName(), header.getName());
                if (tableColumn != null) {
                    header.setPrimaryKey(tableColumn.getPrimaryKey());
                    header.setComment(tableColumn.getComment());
                    header.setDefaultValue(tableColumn.getDefaultValue());
                    header.setNullable(tableColumn.getNullable());
                    header.setColumnSize(tableColumn.getColumnSize());
                    header.setDecimalDigits(tableColumn.getDecimalDigits());
                    header.setColumnType(tableColumn.getColumnType());
                    enrichEditorMetadata(header, tableColumn, metaData, supportsEditorOptions,
                            editorMetadataByColumn);
                }
            }
        } catch (Exception e) {
            log.error("setColumnInfo error:", e);
        }
        return headers;
    }

    private Map<TableColumn, ResultSetEditorMetadata> resolveEditorMetadata(
            Connection connection, List<TableColumn> columns, IDbMetaData metaData,
            boolean supportsEditorOptions) {
        if (!supportsEditorOptions) {
            return Map.of();
        }
        try {
            Map<Integer, ResultSetEditorMetadata> metadataByColumnIndex =
                    metaData.resolveResultSetEditorMetadata(connection, columns);
            if (metadataByColumnIndex == null || metadataByColumnIndex.isEmpty()) {
                return Map.of();
            }
            Map<TableColumn, ResultSetEditorMetadata> metadataByColumn = new IdentityHashMap<>();
            for (Map.Entry<Integer, ResultSetEditorMetadata> entry : metadataByColumnIndex.entrySet()) {
                Integer columnIndex = entry.getKey();
                if (columnIndex == null || columnIndex < 0 || columnIndex >= columns.size()
                        || entry.getValue() == null) {
                    continue;
                }
                metadataByColumn.put(columns.get(columnIndex), entry.getValue());
            }
            return metadataByColumn;
        } catch (Exception e) {
            log.warn("Resolve batched result-set editor metadata failed", e);
            return Map.of();
        }
    }

    private void enrichEditorMetadata(Header header, TableColumn tableColumn, IDbMetaData metaData,
                                      boolean supportsEditorOptions,
                                      Map<TableColumn, ResultSetEditorMetadata> editorMetadataByColumn) {
        try {
            if (!supportsEditorOptions) {
                ResultSetEditorTypeEnum editorType = ResultSetEditorTypeEnum.from(metaData.resolveResultSetEditorType(
                        tableColumn.getColumnType(), tableColumn.getDataType()));
                header.setEditorType(editorType.getCode());
                header.setEditorOptions(null);
                return;
            }
            ResultSetEditorMetadata editorMetadata = editorMetadataByColumn.get(tableColumn);
            if (editorMetadata == null) {
                return;
            }
            ResultSetEditorTypeEnum editorType = ResultSetEditorTypeEnum.from(editorMetadata.getEditorType());
            header.setEditorType(editorType.getCode());
            header.setEditorOptions(CollectionUtils.isEmpty(editorMetadata.getEditorOptions())
                    ? null : editorMetadata.getEditorOptions());
        } catch (Exception e) {
            log.warn("Resolve result-set editor metadata failed for column: {}", tableColumn.getName(), e);
        }
    }

    private TableColumn findColumn(Map<String, TableColumn> columnMap,
                                   Map<String, List<TableColumn>> caseInsensitiveColumnMap,
                                   String... candidateNames) {
        for (String candidateName : candidateNames) {
            if (StringUtils.isBlank(candidateName)) {
                continue;
            }
            TableColumn exactMatch = columnMap.get(candidateName);
            if (exactMatch != null) {
                return exactMatch;
            }
            List<TableColumn> caseInsensitiveMatches = caseInsensitiveColumnMap.get(normalizeColumnName(candidateName));
            if (caseInsensitiveMatches != null && caseInsensitiveMatches.size() == 1) {
                return caseInsensitiveMatches.get(0);
            }
        }
        return null;
    }

    private String normalizeColumnName(String columnName) {
        return columnName.toLowerCase(Locale.ROOT);
    }
}

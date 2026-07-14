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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            Map<String, TableColumn> columnMap = columns.stream().collect(
                    Collectors.toMap(TableColumn::getName, tableColumn -> tableColumn, (left, right) -> left));

            Connection connection = Chat2DBContext.getConnection();
            IDbMetaData metaData = Chat2DBContext.getDbMetaData();
            List<PrimaryKey> primaryKeys = metaData.getPrimaryKeys(connection,
                    new TableMetadataRequest(tableQueryParam.getDatabaseName(), tableQueryParam.getSchemaName(),
                            tableQueryParam.getTableName()));
            for (PrimaryKey primaryKey : primaryKeys) {
                TableColumn tableColumn = columnMap.get(primaryKey.getColumnName());
                if (Objects.nonNull(tableColumn)) {
                    tableColumn.setPrimaryKey(true);
                }
            }

            for (Header header : headers) {
                TableColumn tableColumn = columnMap.get(header.getName());
                if (tableColumn != null) {
                    header.setPrimaryKey(tableColumn.getPrimaryKey());
                    header.setComment(tableColumn.getComment());
                    header.setDefaultValue(tableColumn.getDefaultValue());
                    header.setNullable(tableColumn.getNullable());
                    header.setColumnSize(tableColumn.getColumnSize());
                    header.setDecimalDigits(tableColumn.getDecimalDigits());
                    header.setColumnType(tableColumn.getColumnType());
                    ResultSetEditorTypeEnum editorType = ResultSetEditorTypeEnum.from(metaData.resolveResultSetEditorType(
                            tableColumn.getColumnType(), tableColumn.getDataType()));
                    header.setEditorType(editorType.getCode());
                }
            }
        } catch (Exception e) {
            log.error("setColumnInfo error:", e);
        }
        return headers;
    }
}

package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.er.ERModel;
import ai.chat2db.community.domain.api.model.er.ERPosition;
import ai.chat2db.community.domain.api.model.request.er.DbErQueryRequest;
import ai.chat2db.community.domain.api.service.db.IDbErPositionService;
import ai.chat2db.community.domain.api.service.db.IDbErService;
import ai.chat2db.community.domain.api.model.metadata.ForeignKeyInfo;
import ai.chat2db.community.domain.api.model.metadata.PrimaryKey;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DbErServiceImpl implements IDbErService {

    private final IDbErPositionService erPositionService;

    public DbErServiceImpl(IDbErPositionService erPositionService) {
        this.erPositionService = erPositionService;
    }

    @Override
    public ERModel getModel(DbErQueryRequest erQueryParam) {
        Connection connection = Chat2DBContext.getConnection();
        List<Table> tableList = DefaultSQLExecutor.getInstance()
                .tables(connection,
                        erQueryParam.getDatabaseName(),
                        erQueryParam.getSchemaName(),
                        null,
                        new String[]{"TABLE", "SYSTEM TABLE", "BASE TABLE"});
        List<TableColumn> columnList = DefaultSQLExecutor.getInstance()
                .columns(connection,
                        erQueryParam.getDatabaseName(),
                        erQueryParam.getSchemaName(),
                        null,
                        null);

        ERModel erModel = new ERModel();
        erModel.setTables(attachTableRelations(tableList, columnList, connection));
        return erModel;
    }

    @Override
    public ERModel getModelWithPosition(DbErQueryRequest erQueryParam) {
        ERModel erModel = getModel(erQueryParam);
        if (erModel != null) {
            erModel.setPosition(erPositionService.getErPosition(erQueryParam.getDataSourceId(),
                    erQueryParam.getDatabaseName(), erQueryParam.getSchemaName()));
        }
        return erModel;
    }

    @Override
    public void savePosition(ERPosition erPosition) {
        erPositionService.savePosition(erPosition);
    }

    private List<Table> attachTableRelations(List<Table> tableList, List<TableColumn> columnList, Connection connection) {
        for (Table table : tableList) {
            List<PrimaryKey> primaryKeys = DefaultSQLExecutor.getInstance().getPrimaryKeys(connection, table.getDatabaseName(), table.getSchemaName(), table.getName());
            Set<String> primaryKeySet = primaryKeys.stream().map(PrimaryKey::getColumnName).collect(java.util.stream.Collectors.toSet());
            for (TableColumn column : columnList) {
                if (StringUtils.equals(table.getName(), column.getTableName())) {
                    List<TableColumn> columns = table.getColumnList();
                    if (columns == null) {
                        columns = new ArrayList<>();
                        table.setColumnList(columns);
                    }
                    if (primaryKeySet.contains(column.getName())) {
                        column.setPrimaryKey(true);
                    }
                    columns.add(column);
                }
            }
            List<ForeignKeyInfo> foreignKeyInfoList = DefaultSQLExecutor.getInstance()
                    .getImportedKeys(connection,
                            table.getDatabaseName(),
                            table.getSchemaName(),
                            table.getName());
            for (ForeignKeyInfo foreignKeyInfo : foreignKeyInfoList) {
                if (StringUtils.equals(table.getName(), foreignKeyInfo.getFkTableName())) {
                    List<ForeignKeyInfo> foreignKeyInfos = table.getForeignKeyList();
                    if (foreignKeyInfos == null) {
                        foreignKeyInfos = new ArrayList<>();
                        table.setForeignKeyList(foreignKeyInfos);
                    }
                    foreignKeyInfos.add(foreignKeyInfo);
                }
            }
        }
        return tableList;
    }

}

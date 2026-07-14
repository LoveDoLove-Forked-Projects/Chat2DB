package ai.chat2db.plugin.oceanbase.oracle;

import ai.chat2db.plugin.oracle.OracleMetaData;
import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ai.chat2db.plugin.oceanbase.constant.OceanbaseOracleMetaDataConstants.*;
@Slf4j
public class OceanbaseOracleMetaData extends OracleMetaData implements IDbMetaData {







    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = String.format(TABLE_DDL_SQL, tableName, schemaName);
        String tableCommentSql = String.format(TABLE_COMMENT_SQL, schemaName, tableName);
        String tableColumnCommentSql = String.format(TABLE_COLUMN_COMMENT_SQL, schemaName, tableName);
        String PUIndexSql = String.format(PU_INDEX_NAME_SQL, schemaName, tableName);
        String tableIndexNameSql = String.format(TABLE_INDEX_NAME_SQL, schemaName, tableName);
        StringBuilder ddlBuilder = new StringBuilder();
        DefaultSQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            try {
                if (resultSet.next()) {
                    ddlBuilder.append(resultSet.getString("sql")).append(";");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        DefaultSQLExecutor.getInstance().execute(connection, tableCommentSql, resultSet -> {
            if (resultSet.next()) {
                String tableComment = resultSet.getString("comments");
                if (StringUtils.isNotBlank(tableComment)) {
                    ddlBuilder.append("\nCOMMENT ON TABLE ").append(SqlUtils.quoteObjectName(tableName)).append(" IS ")
                            .append(EasyStringUtils.escapeAndQuoteString(tableComment)).append(";");
                }
            }
        });
        DefaultSQLExecutor.getInstance().execute(connection, tableColumnCommentSql, resultSet -> {
            while (resultSet.next()) {
                String columnName = resultSet.getString("column_name");
                String columnComment = resultSet.getString("comments");
                if (StringUtils.isNotBlank(columnComment)) {
                    ddlBuilder.append("\nCOMMENT ON COLUMN ")
                            .append(SqlUtils.quoteObjectName(tableName)).append(".")
                            .append(SqlUtils.quoteObjectName(columnName)).append(" IS ")
                            .append(EasyStringUtils.escapeAndQuoteString(columnComment)).append(";");
                }
            }
        });
        List<String> PUConstraintsName = DefaultSQLExecutor.getInstance().execute(connection, PUIndexSql, resultSet -> {
            List<String> PUIndexNames = new ArrayList<>();
            while (resultSet.next()) {
                String indexName = resultSet.getString("index_name");
                if (StringUtils.isNotBlank(indexName)) {
                    PUIndexNames.add(indexName);
                }
            }
            return PUIndexNames;
        });

        ArrayList<String> indexes = DefaultSQLExecutor.getInstance().execute(connection, tableIndexNameSql, resultSet -> {
            ArrayList<String> indexNames = new ArrayList<>();
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                if (CollectionUtils.isNotEmpty(PUConstraintsName) && PUConstraintsName.contains(indexName)) {
                    continue;
                }
                indexNames.add(indexName);
            }
            return indexNames;
        });
        for (String index : indexes) {
            String tableIndexSql = String.format(TABLE_INDEX_DDL_SQL, index, schemaName);
            DefaultSQLExecutor.getInstance().execute(connection, tableIndexSql, resultSet -> {
                while (resultSet.next()) {
                    String ddl = resultSet.getString("ddl");
                    if (StringUtils.isNotBlank(ddl)) {
                        ddlBuilder.append("\n\n").append(ddl);
                    }
                }
            });
        }
        return ddlBuilder.toString();
    }


}

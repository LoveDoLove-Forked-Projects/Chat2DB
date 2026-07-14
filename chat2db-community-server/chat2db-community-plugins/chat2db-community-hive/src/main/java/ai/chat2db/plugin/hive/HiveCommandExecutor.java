package ai.chat2db.plugin.hive;

import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.spi.model.request.SqlStatementExecuteRequest;
import ai.chat2db.spi.DefaultSQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HiveCommandExecutor extends DefaultSQLExecutor {


    @Override
    public List<ExecuteResponse> execute(SqlExecuteRequest command) {
        List<ExecuteResponse> result = new ArrayList<>();
        result = super.execute(command);
        if (CollectionUtils.isNotEmpty(result)) {
            for (ExecuteResponse executeResult : result) {
                if (executeResult.getHeaderList() != null) {
                    for (Header header : executeResult.getHeaderList()) {
                        header.setName(formatTableName(header.getName()));
                    }
                }
            }
        }
        return result;
    }


    @Override
    public ExecuteResponse executeUpdate(String sql, Connection connection, int n) throws SQLException {
        return super.executeUpdate(sql, connection, n);
    }


    @Override
    public ExecuteResponse execute(SqlStatementExecuteRequest request)
            throws SQLException {
        return super.execute(request);
    }

    public static String formatTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return tableName;
        }
        if (tableName.contains(".")) {
            String[] split = tableName.split("\\.");
            return split[1];
        }
        return tableName;
    }
}

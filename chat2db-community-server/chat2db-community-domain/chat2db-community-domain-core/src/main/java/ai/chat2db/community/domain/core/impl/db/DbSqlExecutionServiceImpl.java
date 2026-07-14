package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.model.request.db.DbStreamingExecuteRequest;
import ai.chat2db.community.domain.api.service.db.IDbSqlCommandService;
import ai.chat2db.community.domain.api.service.db.IDbSqlExecutionService;
import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.spi.ICommandExecutor;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class DbSqlExecutionServiceImpl implements IDbSqlExecutionService {

    private final IDbSqlCommandService sqlSqlExecuteRequestService;

    public DbSqlExecutionServiceImpl(IDbSqlCommandService sqlSqlExecuteRequestService) {
        this.sqlSqlExecuteRequestService = sqlSqlExecuteRequestService;
    }

    @Override
    public void executeStreaming(DbStreamingExecuteRequest executeStreamingRequest) throws SQLException {
        SqlExecuteRequest command = sqlSqlExecuteRequestService.toSqlExecuteRequest(
                executeStreamingRequest.getDlExecuteRequest());
        ICommandExecutor executor = Chat2DBContext.getDbMetaData().getCommandExecutor();
        if (!(executor instanceof DefaultSQLExecutor sqlExecutor)) {
            throw new IllegalStateException(I18nUtils.getMessage("sqlExecution.streamingUnsupported"));
        }
        sqlExecutor.executeStreaming(command, executeStreamingRequest.getConsumer(),
                executeStreamingRequest.getStatementListener(), executeStreamingRequest.getCancellation());
    }

}

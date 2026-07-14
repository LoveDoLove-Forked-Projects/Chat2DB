package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.metadata.RoutineOperation;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.sql.SqlPreview;
import ai.chat2db.community.domain.api.service.db.IDbRoutineOperationService;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.spi.IRoutineManager;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class DbRoutineOperationServiceImpl implements IDbRoutineOperationService {

    @Override
    public SqlPreview previewInvocation(RoutineOperation operation) {
        IRoutineManager routineManager = Chat2DBContext.getRoutineManager();
        if (routineManager == null) {
            throw new BusinessException("routine.operation.onlyMysqlSupported");
        }
        Connection connection = Chat2DBContext.getConnection();
        if (connection == null) {
            throw new BusinessException("connection error");
        }
        return routineManager.previewInvocation(connection, completeOperation(operation));
    }

    @Override
    public SqlPreview previewMigration(RoutineOperation operation) {
        IRoutineManager routineManager = Chat2DBContext.getRoutineManager();
        if (routineManager == null) {
            throw new BusinessException("routine.operation.onlyMysqlSupported");
        }
        Connection connection = Chat2DBContext.getConnection();
        if (connection == null) {
            throw new BusinessException("connection error");
        }
        return routineManager.previewMigration(connection, completeOperation(operation));
    }

    @Override
    public ExecuteResponse executeMigration(RoutineOperation operation) {
        IRoutineManager routineManager = Chat2DBContext.getRoutineManager();
        if (routineManager == null) {
            throw new BusinessException("routine.operation.onlyMysqlSupported");
        }
        Connection connection = Chat2DBContext.getConnection();
        if (connection == null) {
            throw new BusinessException("connection error");
        }
        return routineManager.executeMigration(connection, completeOperation(operation));
    }

    private RoutineOperation completeOperation(RoutineOperation operation) {
        if (StringUtils.isBlank(operation.getDatabaseName())) {
            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
            operation.setDatabaseName(connectInfo == null ? null : connectInfo.getDatabaseName());
        }
        return operation;
    }
}

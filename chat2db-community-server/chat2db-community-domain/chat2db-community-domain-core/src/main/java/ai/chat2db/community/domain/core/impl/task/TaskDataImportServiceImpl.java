package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionOperation;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan;
import ai.chat2db.community.domain.api.service.db.ISqlExecutionStatementListener;
import ai.chat2db.community.domain.api.service.task.ITaskDataImportService;
import ai.chat2db.community.domain.api.service.task.ITaskImportSqlExecutor;
import ai.chat2db.community.domain.core.impl.db.extension.SqlExecutionPolicyManager;
import ai.chat2db.community.domain.core.impl.task.extension.TaskExtensionManager;
import ai.chat2db.community.domain.core.impl.task.imports.ImportFactory;
import ai.chat2db.community.domain.core.impl.task.imports.IImportStrategy;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

@Slf4j
@Service
public class TaskDataImportServiceImpl implements ITaskDataImportService, ITaskImportSqlExecutor {

    private final TaskExtensionManager taskExtensionManager;
    private final SqlExecutionPolicyManager sqlExecutionPolicyManager;

    public TaskDataImportServiceImpl(TaskExtensionManager taskExtensionManager,
            SqlExecutionPolicyManager sqlExecutionPolicyManager) {
        this.taskExtensionManager = taskExtensionManager;
        this.sqlExecutionPolicyManager = sqlExecutionPolicyManager;
    }

    @Override
    public void importOtherFile(ImportAsyncContext asyncContext) {
        asyncContext.setSqlExecutor(this);
        IImportStrategy strategy = ImportFactory.get(asyncContext.getImportType());
        strategy.run(asyncContext);
    }

    @Override
    public String executeBatch(int batch, List<String> sqls) {
        return executeBatch(batch, sqls, null, null);
    }

    @Override
    public String executeBatch(int batch, List<String> sqls,
                               ISqlExecutionStatementListener statementListener,
                               Runnable cancellationChecker) {
        if (CollectionUtils.isEmpty(sqls)) {
            return "success";
        }
        List<String> batchSqls = new ArrayList<>();
        String result = "success";
        for (String sql : sqls) {
            checkCancelled(cancellationChecker);
            if (StringUtils.isBlank(sql)) {
                continue;
            }
            String str = sql.trim().toUpperCase();
            String executableSql = prepareStatement(sql);
            if (!str.startsWith("INSERT")) {
                try {
                    DefaultSQLExecutor.getInstance().executeBatchInsert(Chat2DBContext.getConnection(),
                            Lists.asList(executableSql, new String[]{}), statementListener, cancellationChecker);
                } catch (CancellationException e) {
                    throw e;
                } catch (Exception e) { // impl-contract: fallback - import executor reports failure through ImportAsyncContext.error.
                    log.error("execute sql error:sql:{}", sql, e);
                    result = "Fail The " + batch + "th batch of  SQL statements failed to execute " + sql
                            + " error: " + e.getMessage();
                }
            } else {
                batchSqls.add(executableSql);
            }
        }
        if (CollectionUtils.isNotEmpty(batchSqls)) {
            checkCancelled(cancellationChecker);
            try {
                DefaultSQLExecutor.getInstance().executeBatchInsert(
                        Chat2DBContext.getConnection(), batchSqls, statementListener, cancellationChecker);
            } catch (CancellationException e) {
                throw e;
            } catch (Exception e) { // impl-contract: fallback - import executor reports failure through ImportAsyncContext.error.
                log.error("batch execute sql error:sqls:{}", JSON.toJSONString(batchSqls), e);
                result = "Fail The " + batch + "th batch of  SQL statements failed to execute  error: "
                        + e.getMessage();
            }
        }
        return result;
    }

    @Override
    public String executeSql(int batch, String sql) {
        return executeSql(batch, sql, null, null);
    }

    @Override
    public String executeSql(int batch, String sql,
                             ISqlExecutionStatementListener statementListener,
                             Runnable cancellationChecker) {
        checkCancelled(cancellationChecker);
        String executableSql = prepareStatement(sql);
        try {
            DefaultSQLExecutor.getInstance().execute(
                    Chat2DBContext.getConnection(), executableSql, statementListener, cancellationChecker);
            checkCancelled(cancellationChecker);
            return "success";
        } catch (SQLException e) { // impl-contract: fallback - import executor reports failure through ImportAsyncContext.error.
            log.error("batch execute sql error:sql:{}", JSON.toJSONString(sql), e);
            return "Fail The " + batch + "th batch of  SQL statements failed to execute  error: " + e.getMessage();
        }
    }

    private void checkCancelled(Runnable cancellationChecker) {
        if (cancellationChecker != null) {
            cancellationChecker.run();
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new CancellationException("Task was cancelled");
        }
    }

    private String prepareStatement(String sql) {
        ai.chat2db.spi.model.datasource.ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        SqlExecutionContext context = new SqlExecutionContext(
                connectInfo == null ? null : connectInfo.getDataSourceId(),
                connectInfo == null ? null : connectInfo.getDbType(),
                connectInfo == null ? null : connectInfo.getDatabaseName(),
                connectInfo == null ? null : connectInfo.getSchemaName(),
                null, sql, SqlExecutionOperation.IMPORT, null);
        SqlExecutionPlan plan = sqlExecutionPolicyManager.plan(context);
        taskExtensionManager.beforeStatement(sql);
        sqlExecutionPolicyManager.beforeExecute(plan);
        return plan.getSql();
    }
}

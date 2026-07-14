package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;
import ai.chat2db.community.domain.api.service.task.ITaskDataImportService;
import ai.chat2db.community.domain.api.service.task.ITaskImportSqlExecutor;
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

@Slf4j
@Service
public class TaskDataImportServiceImpl implements ITaskDataImportService, ITaskImportSqlExecutor {

    @Override
    public void importOtherFile(ImportAsyncContext asyncContext) {
        asyncContext.setSqlExecutor(this);
        IImportStrategy strategy = ImportFactory.get(asyncContext.getImportType());
        strategy.run(asyncContext);
    }

    @Override
    public String executeBatch(int batch, List<String> sqls) {
        if (CollectionUtils.isEmpty(sqls)) {
            return "success";
        }
        List<String> batchSqls = new ArrayList<>();
        String result = "success";
        try {
            for (String sql : sqls) {
                if (StringUtils.isBlank(sql)) {
                    continue;
                }
                String str = sql.trim().toUpperCase();
                if (!str.startsWith("INSERT")) {
                    try {
                        DefaultSQLExecutor.getInstance().executeBatchInsert(Chat2DBContext.getConnection(),
                                Lists.asList(sql, new String[]{}));
                    } catch (Exception e) { // impl-contract: fallback - import executor reports failure through ImportAsyncContext.error.
                        log.error("execute sql error:sql:{}", sql, e);
                        result = "Fail The " + batch + "th batch of  SQL statements failed to execute " + sql
                                + " error: " + e.getMessage();
                    }
                } else {
                    batchSqls.add(sql);
                }
            }
            if (CollectionUtils.isNotEmpty(batchSqls)) {
                DefaultSQLExecutor.getInstance().executeBatchInsert(Chat2DBContext.getConnection(), batchSqls);
            }
        } catch (Exception e) { // impl-contract: fallback - import executor reports failure through ImportAsyncContext.error.
            log.error("batch execute sql error:sqls:{}", JSON.toJSONString(batchSqls), e);
            result = "Fail The " + batch + "th batch of  SQL statements failed to execute  error: " + e.getMessage();
        }
        return result;
    }

    @Override
    public String executeSql(int batch, String sql) {
        try {
            DefaultSQLExecutor.getInstance().execute(Chat2DBContext.getConnection(), sql);
            return "success";
        } catch (SQLException e) { // impl-contract: fallback - import executor reports failure through ImportAsyncContext.error.
            log.error("batch execute sql error:sql:{}", JSON.toJSONString(sql), e);
            return "Fail The " + batch + "th batch of  SQL statements failed to execute  error: " + e.getMessage();
        }
    }
}

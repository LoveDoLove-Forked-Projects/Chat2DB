package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;
import ai.chat2db.community.domain.api.service.task.ITaskAsyncCall;
import ai.chat2db.community.domain.api.service.task.ITaskFileService;
import ai.chat2db.community.tools.model.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class TaskFileServiceImpl implements ITaskFileService {

    private static final String DEFAULT_TASK_NAME = "chat2db_export";

    @Override
    public String defaultExportPath() {
        return System.getProperty("user.dir");
    }

    @Override
    public String resolveTaskName(String databaseName, String schemaName, List<String> tableNames) {
        String taskName = "";
        if (CollectionUtils.isNotEmpty(tableNames)) {
            if (tableNames.size() > 2) {
                taskName = tableNames.get(0) + "_" + tableNames.get(1);
            } else {
                taskName = String.join("_", tableNames);
            }
        }
        if (StringUtils.isBlank(taskName) && StringUtils.isNotBlank(schemaName)) {
            taskName = schemaName;
        }
        if (StringUtils.isBlank(taskName) && StringUtils.isNotBlank(databaseName)) {
            taskName = databaseName;
        }
        return StringUtils.defaultIfBlank(taskName, DEFAULT_TASK_NAME);
    }

    @Override
    public AsyncContext createSqlExportContext(Long taskId, String exportPath, String databaseName, String schemaName,
            List<String> tableNames, boolean containData, ITaskAsyncCall asyncCall, Context context) {
        File writeFile = newFileAutoAppendNo(exportPath, resolveTaskName(databaseName, schemaName, tableNames),
                "sql");
        return new AsyncContext(asyncCall, context, writeFile, containData);
    }

    @Override
    public ExportAsyncContext createOtherExportContext(Long taskId, String exportPath, String databaseName,
            String schemaName, List<String> tableNames, String exportType, Boolean containsHeader,
            ITaskAsyncCall asyncCall, Context context) {
        File writeFile;
        if (tableNames != null && tableNames.size() == 1) {
            writeFile = newFileAutoAppendNo(exportPath, tableNames.get(0), exportType.toLowerCase());
        } else {
            writeFile = newFileAutoAppendNo(exportPath, resolveTaskName(databaseName, schemaName, tableNames),
                    "zip");
        }
        return new ExportAsyncContext(asyncCall, context, writeFile, exportType, tableNames, "single", containsHeader);
    }

    @Override
    public ImportAsyncContext createImportContext(Long taskId, String importType, String tableName, String fileName,
            ITaskAsyncCall asyncCall, Context context) {
        return new ImportAsyncContext(asyncCall, context, importType, tableName, new File(fileName));
    }

    private File newFileAutoAppendNo(String path, String fileName, String suffix) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File newFile = new File(path + File.separator + fileName + "." + suffix);
        if (newFile.exists()) {
            int i = 1;
            while (i < 1000) {
                newFile = new File(path + File.separator + fileName + "_" + i + "." + suffix);
                if (!newFile.exists()) {
                    break;
                }
                i++;
            }
        }
        if (newFile.exists()) {
            newFile = new File(path + File.separator + fileName + "_" + System.currentTimeMillis() + "." + suffix);
        }
        return newFile;
    }
}

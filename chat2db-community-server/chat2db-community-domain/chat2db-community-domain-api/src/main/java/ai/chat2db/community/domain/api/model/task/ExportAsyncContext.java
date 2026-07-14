package ai.chat2db.community.domain.api.model.task;

import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.community.domain.api.service.task.ITaskAsyncCall;
import ai.chat2db.community.tools.model.Context;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Setter
@Getter
public class ExportAsyncContext extends AsyncContext {

    private String exportType;

    private List<String> tableNames;

    private String sqyType;

    private Boolean containsHeader;

    public ExportAsyncContext(ITaskAsyncCall call, Context context, File writeFile, String exportType,
                              List<String> tableNames, String sqyType, Boolean containsHeader) {
        super(call, context, writeFile, true);
        this.exportType = exportType;
        this.tableNames = tableNames;
        this.sqyType = sqyType;
        this.containsHeader = containsHeader;
    }
}

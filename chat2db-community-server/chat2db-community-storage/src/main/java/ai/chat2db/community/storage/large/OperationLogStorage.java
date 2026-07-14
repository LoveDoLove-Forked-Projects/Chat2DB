package ai.chat2db.community.storage.large;

import ai.chat2db.community.domain.api.model.operation.OperationLog;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class OperationLogStorage extends LargeDataStorage<OperationLog> {

    public static final OperationLogStorage INSTANCE = new OperationLogStorage();

    protected OperationLogStorage() {
        super("operation_log", OperationLog.class, 1000);
    }

    @Override
    public List<OperationLog> getDataList() {
        List<OperationLog> list = super.getDataList();
        if (CollectionUtils.isNotEmpty(list)) {
            Collections.reverse(list);
        }
        return list;
    }

}

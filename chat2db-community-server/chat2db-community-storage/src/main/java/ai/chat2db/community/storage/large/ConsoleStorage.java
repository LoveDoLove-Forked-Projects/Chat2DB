package ai.chat2db.community.storage.large;

import ai.chat2db.community.domain.api.model.operation.Operation;
import com.google.common.collect.Lists;

import java.util.List;

public class ConsoleStorage extends LargeDataStorage<Operation> {

    public static final ConsoleStorage INSTANCE = new ConsoleStorage();

    protected ConsoleStorage() {
        super("console", Operation.class, 10000);
    }

    public List<Operation> getDataList(Operation operation, int page, int pageSize) {
        List<Operation> list = getDataList();
        if (operation != null) {
           return operation.select(list, page, pageSize);
        }
        return Lists.newArrayList();
    }

}

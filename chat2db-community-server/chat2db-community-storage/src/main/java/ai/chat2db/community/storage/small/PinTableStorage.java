package ai.chat2db.community.storage.small;

import ai.chat2db.community.domain.api.model.pin.PinTable;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PinTableStorage extends SmallDataStorage<PinTable> {

    public static final PinTableStorage INSTANCE = new PinTableStorage();

    protected PinTableStorage() {
        super("pin_table", PinTable.class);
    }

    public void delete(PinTable pinTable) {
        List<PinTable> pinTables = getDataList();
        if (CollectionUtils.isNotEmpty(pinTables)) {
            for (PinTable table : pinTables) {
                if (table == null) {
                    continue;
                }
                if (table.equals(pinTable)) {
                    pinTables.remove(table);
                    delete(table.getId());
                    break;
                }
            }
        }
    }
    public List<String> getPinTables(PinTable pinTable) {
        List<PinTable> result = new ArrayList<>();
        if(pinTable == null ){
            return Lists.newArrayList();
        }
        List<PinTable> pinTables = getDataList();
        if (CollectionUtils.isNotEmpty(pinTables)) {
            for (PinTable table : pinTables) {
                if (table == null) {
                    continue;
                }
                if (table.select(pinTable)) {
                    result.add(table);
                }
            }
        }
        Collections.reverse(result);
        return result.stream().map(PinTable::getTableName).collect(java.util.stream.Collectors.toList());
    }

}

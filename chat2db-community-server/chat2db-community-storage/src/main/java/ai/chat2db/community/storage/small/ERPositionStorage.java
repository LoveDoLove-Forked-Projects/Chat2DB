package ai.chat2db.community.storage.small;

import ai.chat2db.community.domain.api.model.er.ERPosition;
import cn.hutool.core.util.ObjectUtil;

import java.util.List;

public class ERPositionStorage extends SmallDataStorage<ERPosition> {

    public static final ERPositionStorage INSTANCE = new ERPositionStorage();

    protected ERPositionStorage() {
        super("er_position", ERPosition.class);
    }

    public String getPosition(Long dataSourceId, String databaseName, String schemaName) {
        List<ERPosition> list = super.getDataList();
        for (ERPosition param : list) {
            if (param.getDataSourceId().equals(dataSourceId) &&
                    ObjectUtil.equals(param.getDatabaseName(), databaseName)
                    && ObjectUtil.equals(param.getSchemaName(), schemaName)) {
                return param.getPosition();
            }
        }
        return null;
    }

    public void savePosition(ERPosition param) {
        List<ERPosition> list = super.getDataList();
        for (ERPosition p : list) {
            if (p.getDataSourceId().equals(param.getDataSourceId()) &&
                    ObjectUtil.equals(p.getDatabaseName(), param.getDatabaseName())
                    && ObjectUtil.equals(p.getSchemaName(), param.getSchemaName())) {
                update(p);
                return;
            }
        }
        super.save(param);
    }
}

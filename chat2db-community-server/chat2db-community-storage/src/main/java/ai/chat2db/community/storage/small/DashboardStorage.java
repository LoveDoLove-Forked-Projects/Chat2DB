package ai.chat2db.community.storage.small;

import ai.chat2db.community.domain.api.model.chart.Dashboard;

import java.util.Comparator;
import java.util.List;

public class DashboardStorage extends SmallDataStorage<Dashboard> {

    public static final DashboardStorage INSTANCE = new DashboardStorage();

    protected DashboardStorage() {
        super("dashboard", Dashboard.class);
    }

    @Override
    public List<Dashboard> getDataList() {
        return super.getDataList().stream()
                .sorted(Comparator.comparing(Dashboard::getGmtModified,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }
}

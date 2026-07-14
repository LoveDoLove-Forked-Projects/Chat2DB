package ai.chat2db.community.storage.small;

import ai.chat2db.community.domain.api.model.chart.Chart;

public class ChartStorage extends SmallDataStorage<Chart> {

    public static final ChartStorage INSTANCE = new ChartStorage();

    protected ChartStorage() {
        super("chart", Chart.class);
    }
}

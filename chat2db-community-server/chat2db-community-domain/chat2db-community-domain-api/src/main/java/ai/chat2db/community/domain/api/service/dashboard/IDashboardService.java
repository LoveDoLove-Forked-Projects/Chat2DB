package ai.chat2db.community.domain.api.service.dashboard;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.chart.Chart;
import ai.chat2db.community.domain.api.model.chart.Dashboard;

public interface IDashboardService {

    PageResponse<Dashboard> listDashboards(Integer pageNo, Integer pageSize, String searchKey);

    Dashboard getDashboard(Long id);

    Long createDashboard(Dashboard dashboard);

    void updateDashboard(Dashboard dashboard);

    void deleteDashboard(Long id);

    Chart getChart(Long id);

    Chart getChartDetail(Long chartId, Boolean refresh);

    Long createChart(Chart chart);

    void updateChart(Chart chart);

    void deleteChart(Long id);
}

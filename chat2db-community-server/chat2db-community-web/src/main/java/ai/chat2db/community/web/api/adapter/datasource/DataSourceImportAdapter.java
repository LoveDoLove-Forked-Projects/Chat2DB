package ai.chat2db.community.web.api.adapter.datasource;

import ai.chat2db.community.domain.api.service.db.IDbDataSourceImportService;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageFacade;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.util.ConfigUtils;
import ai.chat2db.community.web.api.converter.data.source.DataSourceWebConverter;
import ai.chat2db.community.web.api.model.request.data.source.DataSourceCreateRequest;
import ai.chat2db.community.web.api.model.http.CookieUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataSourceImportAdapter implements IDbDataSourceImportService {

    private static final String LEGACY_H2_URL = "jdbc:h2:~/.chat2db/db/chat2db;MODE=MYSQL;FILE_LOCK=NO";

    private final IWorkspaceStorageFacade workspaceStorageFacade;
    private final DataSourceWebConverter dataSourceWebConverter;

    public DataSourceImportAdapter(IWorkspaceStorageFacade workspaceStorageFacade,
            DataSourceWebConverter dataSourceWebConverter) {
        this.workspaceStorageFacade = workspaceStorageFacade;
        this.dataSourceWebConverter = dataSourceWebConverter;
    }

    @Override
    public void importCommunityDataSources() {
        if (!ConfigUtils.isDesktop() || CookieUtil.getUserIdCookie() == null) {
            throw new BusinessException("web.not.support.db.type");
        }
        try (Connection conn = DriverManager.getConnection(LEGACY_H2_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM DATA_SOURCE")) {
            while (rs.next()) {
                DataSourceCreateRequest request = dataSourceWebConverter.rs2Request(rs);
                request.setPassword(null);
                workspaceStorageFacade.createDataSource(
                        dataSourceWebConverter.response2storage(dataSourceWebConverter.request2response(request)));
            }
        } catch (Exception e) {
            log.error("import.datasource.error", e);
            throw new BusinessException("import.datasource.error");
        }
    }

}

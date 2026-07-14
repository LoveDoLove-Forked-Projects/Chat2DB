package ai.chat2db.community.domain.core.impl.cli;

import java.util.List;

import ai.chat2db.community.domain.api.model.cli.CliRuntimeCapabilities;
import ai.chat2db.community.domain.api.model.cli.CliRuntimeHealth;
import ai.chat2db.community.domain.api.service.cli.ICliRuntimeService;
import ai.chat2db.community.tools.util.ConfigUtils;
import org.springframework.stereotype.Service;

@Service
public class CliRuntimeServiceImpl implements ICliRuntimeService {

    private static final String API_VERSION = "v1";

    @Override
    public CliRuntimeHealth health() {
        CliRuntimeHealth health = new CliRuntimeHealth();
        health.setReady(Boolean.TRUE);
        if (ConfigUtils.isCommunity()) {
            health.setEdition("community");
        } else if (ConfigUtils.isOffline()) {
            health.setEdition("local");
        } else {
            health.setEdition("pro");
        }
        health.setVersion(ConfigUtils.getLocalVersion());
        health.setApiVersion(API_VERSION);
        return health;
    }

    @Override
    public CliRuntimeCapabilities capabilities() {
        CliRuntimeCapabilities capabilities = new CliRuntimeCapabilities();
        capabilities.setFeatures(List.of(
                "datasource.list",
                "datasource.get",
                "datasource.create",
                "datasource.update",
                "datasource.delete",
                "metadata.database.list",
                "metadata.schema.list",
                "metadata.table.list",
                "metadata.table.detail",
                "metadata.table.columns",
                "metadata.table.indexes",
                "sql.query"
        ));
        return capabilities;
    }
}

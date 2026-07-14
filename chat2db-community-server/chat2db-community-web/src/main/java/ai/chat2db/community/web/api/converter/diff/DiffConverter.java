package ai.chat2db.community.web.api.converter.diff;

import ai.chat2db.community.domain.api.model.request.datasource.DbConnectionDiffRequest;
import ai.chat2db.community.web.api.model.request.db.StructureDiffRequest;
import org.springframework.stereotype.Component;

@Component
public class DiffConverter {

    public DbConnectionDiffRequest structureInfo2param(StructureDiffRequest.StructureInfo request) {
        if (request == null) {
            return null;
        }
        return new DbConnectionDiffRequest(request.getDataSourceId(), request.getDatabaseName(), request.getSchemaName());
    }
}

package ai.chat2db.community.web.api.converter.db;

import ai.chat2db.community.web.api.model.request.db.DatabaseCreateRequest;
import ai.chat2db.community.domain.api.model.metadata.Database;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DatabaseConverter {

    public abstract Database request2param(DatabaseCreateRequest request);

    public Database createRequest2param(DatabaseCreateRequest request) {
        Database database = request2param(request);
        if (database != null && StringUtils.isBlank(database.getName())) {
            database.setName(request.getDatabaseName());
        }
        return database;
    }
}

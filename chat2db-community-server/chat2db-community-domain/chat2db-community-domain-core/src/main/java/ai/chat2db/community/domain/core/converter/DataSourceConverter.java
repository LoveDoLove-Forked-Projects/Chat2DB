package ai.chat2db.community.domain.core.converter;

import ai.chat2db.community.domain.api.model.datasource.DataSource;
import ai.chat2db.community.domain.api.model.request.datasource.DbDataSourcePreConnectRequest;
import ai.chat2db.community.domain.api.model.request.datasource.DbDataSourceTestRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public abstract class DataSourceConverter {

    @Mappings({
        @Mapping(source = "type", target = "dbType"),
        @Mapping(source = "user", target = "username")
    })
    public abstract DbDataSourceTestRequest param2param(
        DbDataSourcePreConnectRequest dataSourcePreConnectParam);


    @Mappings({
        @Mapping(target = "id", ignore = true),
    })
    public abstract void add(@MappingTarget DataSource target, DataSource source);
}

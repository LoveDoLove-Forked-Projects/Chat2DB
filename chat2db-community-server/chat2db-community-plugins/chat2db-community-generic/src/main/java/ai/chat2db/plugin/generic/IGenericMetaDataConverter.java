package ai.chat2db.plugin.generic;

import ai.chat2db.community.domain.api.model.metadata.ColumnType;
import ai.chat2db.community.domain.api.model.metadata.Type;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IGenericMetaDataConverter {

    IGenericMetaDataConverter INSTANCE = Mappers.getMapper(IGenericMetaDataConverter.class);

    ColumnType type2columnType(Type type);

    List<ColumnType> type2columnType(List<Type> types);
}

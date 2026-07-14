package ai.chat2db.plugin.oscar;

import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.plugin.oscar.constant.OscarConstants;
import ai.chat2db.plugin.oscar.enums.type.OscarTypeAliasEnum;
import ai.chat2db.plugin.oscar.util.OscarUtils;
import ai.chat2db.community.domain.api.model.metadata.FunctionParameter;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.DefaultSQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OscarDBManager extends OscarBaseDBManager {

    @Override
    public void connectDatabase(Connection connection, String database) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (ObjectUtils.anyNull(connectInfo) || StringUtils.isBlank(connectInfo.getSchemaName())) {
            return;
        }
        try {
            execute(connection, OscarConstants.SET_SEARCH_PATH_SQL
                    + OscarUtils.quoteIdentifierIgnoreCase(connectInfo.getSchemaName()));
        } catch (Exception e) {
            log.error(OscarConstants.CONNECT_DATABASE_ERROR_MESSAGE, e);
        }
    }

    @Override
    public void dropFunction(Connection connection, String databaseName, String schemaName, String functionName) {
        execute(connection, SQLConstants.DROP_FUNCTION_SQL_PREFIX + qualifiedName(schemaName, functionName)
                + buildFunctionSignature(connection, databaseName, schemaName, functionName));
    }

    private String buildFunctionSignature(Connection connection, String databaseName, String schemaName,
                                          String functionName) {
        List<FunctionParameter> parameters = DefaultSQLExecutor.getInstance().getFunctionParameters(connection,
                normalizeCatalog(databaseName),
                OscarUtils.normalizeSchema(schemaName),
                OscarUtils.normalizeIdentifier(functionName));
        List<String> types = parameters.stream()
                .filter(parameter -> StringUtils.isNotBlank(parameter.getTypeName()))
                .filter(parameter -> parameter.getColumnType() != null)
                .filter(parameter -> parameter.getOrdinalPosition() != null && parameter.getOrdinalPosition() > 0)
                .sorted(Comparator.comparing(FunctionParameter::getOrdinalPosition,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(parameter -> OscarTypeAliasEnum.normalize(parameter.getTypeName()))
                .collect(Collectors.toCollection(ArrayList::new));
        return SQLConstants.OPEN_PARENTHESIS
                + types.stream().collect(Collectors.joining(SQLConstants.COMMA))
                + SQLConstants.CLOSE_PARENTHESIS;
    }

    private String normalizeCatalog(String databaseName) {
        return StringUtils.isBlank(databaseName) ? null : OscarUtils.normalizeIdentifier(databaseName);
    }
}

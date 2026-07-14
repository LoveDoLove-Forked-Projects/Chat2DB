package ai.chat2db.plugin.redis;

import ai.chat2db.plugin.redis.constant.RedisConstants;
import ai.chat2db.spi.constant.SQLConstants;

import ai.chat2db.plugin.redis.model.RedisKey;
import ai.chat2db.plugin.redis.enums.type.RedisDataType;
import ai.chat2db.plugin.redis.type.ITypeScript;
import ai.chat2db.spi.ISqlBuilder;
import ai.chat2db.spi.sql.builder.IDatabaseSqlBuilder;
import ai.chat2db.spi.sql.builder.IDdlSqlBuilder;
import ai.chat2db.spi.sql.builder.IDmlSqlBuilder;
import ai.chat2db.spi.sql.builder.IDqlSqlBuilder;
import ai.chat2db.spi.sql.builder.ISchemaSqlBuilder;
import ai.chat2db.spi.sql.builder.ITableSqlBuilder;
import ai.chat2db.spi.sql.builder.IViewSqlBuilder;
import ai.chat2db.spi.model.request.PageLimitRequest;
import ai.chat2db.spi.model.request.DeleteSqlRequest;
import ai.chat2db.spi.model.request.MultiInsertSqlRequest;
import ai.chat2db.spi.model.request.SingleInsertSqlRequest;
import ai.chat2db.spi.model.request.UpdateSqlRequest;
import ai.chat2db.community.domain.api.model.account.*;
import ai.chat2db.community.domain.api.model.async.*;
import ai.chat2db.community.domain.api.config.*;
import ai.chat2db.spi.model.datasource.*;
import ai.chat2db.community.domain.api.model.form.*;
import ai.chat2db.community.domain.api.model.metadata.*;
import ai.chat2db.community.domain.api.model.result.*;
import ai.chat2db.community.domain.api.model.sql.*;
import ai.chat2db.spi.model.value.*;
import ai.chat2db.community.domain.api.model.view.*;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import ai.chat2db.spi.model.request.DropTableRequest;
import ai.chat2db.spi.model.request.TruncateTableRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class RedisSqlBuilder implements ISqlBuilder, IDqlSqlBuilder, IDmlSqlBuilder, IDdlSqlBuilder,
        IDatabaseSqlBuilder, ISchemaSqlBuilder, ITableSqlBuilder, IViewSqlBuilder {

    private static final RedisSqlBuilder INSTANCE = new RedisSqlBuilder();

    public static RedisSqlBuilder getInstance() {
        return INSTANCE;
    }

    public RedisSqlBuilder() {

    }

    @Override
    public IDqlSqlBuilder dql() {
        return this;
    }

    @Override
    public IDmlSqlBuilder dml() {
        return this;
    }

    @Override
    public IDdlSqlBuilder ddl() {
        return this;
    }

    @Override
    public IDatabaseSqlBuilder database() {
        return this;
    }

    @Override
    public ISchemaSqlBuilder schema() {
        return this;
    }

    @Override
    public ITableSqlBuilder table() {
        return this;
    }

    @Override
    public IViewSqlBuilder view() {
        return this;
    }

    @Override
    public String buildCreateTable(Table table, TableBuilderConfig tableBuilderConfig) {
        throw unsupported(RedisConstants.METHOD_BUILD_CREATE_TABLE);
    }

    public String buildCreateKeySql(RedisKey redisKey) {
        List<String> scripts = RedisDataType.fromCode(redisKey.getType()).getScript().createKey(redisKey);
        if (CollectionUtils.isEmpty(scripts)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(scripts, SQLConstants.SEMICOLON_LINE_SEPARATOR);
    }

    @Override
    public String buildAlterTable(Table oldTable, Table newTable) {
        throw unsupported(RedisConstants.METHOD_BUILD_ALTER_TABLE);
    }

    @Override
    public String buildDropTable(DropTableRequest request) {
        throw unsupported(RedisConstants.METHOD_BUILD_DROP_TABLE);
    }

    @Override
    public String buildTruncateTable(TruncateTableRequest request) {
        throw unsupported(RedisConstants.METHOD_BUILD_TRUNCATE_TABLE);
    }

    @Override
    public String buildPageLimit(PageLimitRequest request) {
        throw unsupported(RedisConstants.METHOD_BUILD_PAGE_LIMIT);
    }

    @Override
    public String buildCreateDatabase(Database database) {
        throw unsupported(RedisConstants.METHOD_BUILD_CREATE_DATABASE);
    }

    @Override
    public String buildAlterDatabase(Database oldDatabase, Database newDatabase) {
        throw unsupported(RedisConstants.METHOD_BUILD_ALTER_DATABASE);
    }

    @Override
    public String buildDropDatabase(String databaseName) {
        throw unsupported(RedisConstants.METHOD_BUILD_DROP_DATABASE);
    }

    @Override
    public String buildUseDatabase(String databaseName) {
        throw unsupported(RedisConstants.METHOD_BUILD_USE_DATABASE);
    }

    @Override
    public String buildCreateSchema(Schema schemaName) {
        throw unsupported(RedisConstants.METHOD_BUILD_CREATE_SCHEMA);
    }

    @Override
    public String buildAlterSchema(String oldSchemaName, String newSchemaName) {
        throw unsupported(RedisConstants.METHOD_BUILD_ALTER_SCHEMA);
    }

    @Override
    public String buildDropSchema(String schemaName) {
        throw unsupported(RedisConstants.METHOD_BUILD_DROP_SCHEMA);
    }

    @Override
    public String buildOrderBy(String originSql, List<OrderBy> orderByList) {
        throw unsupported(RedisConstants.METHOD_BUILD_ORDER_BY);
    }

    @Override
    public String buildByQueryResult(QueryResponse queryResult) {
        List<Header> headerList = queryResult.getHeaderList();
        List<ResultOperation> operations = queryResult.getOperations();
        String tableName = queryResult.getTableName();
        if (CollectionUtils.isEmpty(operations)) {
            return StringUtils.EMPTY;
        }
        String redisKeyType = RedisScriptExecutor.getInstance().getKeyType(tableName);
        ITypeScript typeScript = RedisDataType.fromCode(redisKeyType).getScript();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RedisConstants.REDIS_MULTI_COMMAND);
        stringBuilder.append(renameOrUpdateTtl(queryResult));
        for (ResultOperation operation : operations) {
        }
        stringBuilder.append(RedisConstants.REDIS_EXEC_COMMAND);
        return stringBuilder.toString();
    }

    private String renameOrUpdateTtl(QueryResponse queryResult) {
        if (MapUtils.isEmpty(queryResult.getExtra())) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> extra = queryResult.getExtra();
        StringBuilder stringBuilder = new StringBuilder();
        String key = MapUtils.getString(extra, RedisConstants.FIELD_KEY);
        String ttl = MapUtils.getString(extra, RedisConstants.FIELD_TTL);
        if (StringUtils.isNotBlank(key)) {
            stringBuilder.append(RedisConstants.SQL_RENAME_KEY_PREFIX).append(queryResult.getTableName()).append(SQLConstants.SPACE).append(key).append(SQLConstants.LINE_SEPARATOR);
            queryResult.setTableName(key);
        }
        if (StringUtils.isNotBlank(ttl)) {
            stringBuilder.append(RedisConstants.COMMAND_EXPIRE_KEY_PREFIX).append(queryResult.getTableName()).append(SQLConstants.SPACE).append(ttl).append(SQLConstants.LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }


    @Override
    public String buildTemplate(Table table, String type) {
        if (table == null) {
            return SQLConstants.EMPTY;
        }
        return SQLConstants.EMPTY;
    }

    @Override
    public String buildSelectTable(String databaseName, String schemaName, String tableName) {
        throw unsupported(RedisConstants.METHOD_BUILD_SELECT_TABLE);
    }

    @Override
    public String buildSelectCount(String databaseName, String schemaName, String tableName) {
        throw unsupported(RedisConstants.METHOD_BUILD_SELECT_COUNT);
    }

    @Override
    public String buildInsert(SingleInsertSqlRequest request) {
        throw unsupported(RedisConstants.METHOD_BUILD_INSERT);
    }

    @Override
    public String buildBatchInsert(MultiInsertSqlRequest request) {
        throw unsupported(RedisConstants.METHOD_BUILD_BATCH_INSERT);
    }

    @Override
    public String buildUpdate(UpdateSqlRequest request) {
        throw unsupported(RedisConstants.METHOD_BUILD_UPDATE);
    }

    @Override
    public String buildDelete(DeleteSqlRequest deleteSqlRequest) {
        throw unsupported(RedisConstants.METHOD_BUILD_DELETE);
    }

    @Override
    public String buildCopyByQueryResult(QueryResponse queryResult) {
        return SQLConstants.EMPTY;
    }

    @Override
    public String buildCreateView(ModifyView modifyView) {
        throw unsupported(RedisConstants.METHOD_BUILD_CREATE_VIEW);
    }

    @Override
    public String buildAlterView(ModifyView modifyView) {
        throw unsupported(RedisConstants.METHOD_BUILD_ALTER_VIEW);
    }

    @Override
    public String buildDropView(String databaseName, String schemaName, String viewName) {
        throw unsupported(RedisConstants.METHOD_BUILD_DROP_VIEW);
    }

    @Override
    public String buildShowCreateView(String databaseName, String schemaName, String viewName) {
        throw unsupported(RedisConstants.METHOD_BUILD_SHOW_CREATE_VIEW);
    }

    @Override
    public String buildExplain(String sql) {
        throw unsupported(RedisConstants.METHOD_BUILD_EXPLAIN);
    }

    @Override
    public String buildAITableSchema(Table table) {
        throw unsupported(RedisConstants.METHOD_BUILD_AI_TABLE_SCHEMA);
    }

    private UnsupportedOperationException unsupported(String methodName) {
        return new UnsupportedOperationException(RedisConstants.ERROR_UNSUPPORTED_SQL_BUILDER_METHOD_PREFIX + methodName);
    }
}

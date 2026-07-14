package ai.chat2db.plugin.mongodb.constant;

import ai.chat2db.community.tools.wrapper.result.PageResult;
import ai.chat2db.spi.ICommandExecutor;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.ISqlBuilder;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.converter.DocumentConverter;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.SortUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public final class MongodbMetaDataConstants {

    public static final String SCRIPT_USE_SCHEMA = "use %s";
    public static final String SQL_SHOW_DBS = "show dbs";
    public static final String SQL_SHOW_TABLES = "show tables";
    public static final String SELECT_TABLE_INDEX = "db.%s.getIndexes()";
    public static final String SELECT_TABLE_COLUMNS = "db.%s.aggregate([{$project: {documentKeys: { $objectToArray: \"$$ROOT\" }}},{$unwind: \"$documentKeys\"},{$group: {_id: \"$documentKeys.k\",types: { $addToSet: { $type: \"$documentKeys.v\" } }}},{$project: {key: \"$_id\",types: 1,_id: 0}}])";
    public static final List<String> SYSTEM_SCHEMAS = List.of("admin", "config", "local");


    private MongodbMetaDataConstants() {
    }
}

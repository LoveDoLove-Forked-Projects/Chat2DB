package ai.chat2db.plugin.mongodb.constant;

import ai.chat2db.spi.constant.SQLConstants;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.community.domain.api.model.result.Header;
import ai.chat2db.community.domain.api.model.result.QueryResponse;
import ai.chat2db.community.domain.api.model.result.ResultOperation;
import lombok.extern.slf4j.Slf4j;



public final class MongodbSqlBuilderConstants {

    public static final String LOG_UNSUPPORTED_OPERATION_TYPE = "operationType is not support:{}";
    public static final String ERROR_DELETE_OLD_DATA_LIST_EMPTY = "DELETE-oldDataList is empty";
    public static final String SQL_DB_DOT_FORMAT_DOT_DELETEONE_OPEN_PAREN_OPEN_BRACE = "db.%s.deleteOne({_id: ObjectId(\"%s\")})";
    public static final String LOG_DELETE_SQL = "delete sql: {}";
    public static final String SQL_DB_DOT_FORMAT_DOT_INSERTONE = "db.%s.insertOne";
    public static final String LOG_INSERT_SQL = "insert sql:{}";
    public static final String SQL_DB_DOT_FORMAT_DOT_UPDATEONE = "db.%s.updateOne";
    public static final String MONGODB_ID_FIELD = "_id";
    public static final String MONGODB_OBJECT_ID_TYPE = "ObjectId";
    public static final String MONGODB_SET_OPERATOR_PREFIX = "$set:";
    public static final String LOG_UPDATE_SQL = "update sql:{}";

    private MongodbSqlBuilderConstants() {
    }
}

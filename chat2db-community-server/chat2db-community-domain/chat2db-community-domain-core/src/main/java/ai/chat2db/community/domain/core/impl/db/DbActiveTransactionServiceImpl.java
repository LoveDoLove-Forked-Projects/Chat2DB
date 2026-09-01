package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.service.db.IDbActiveTransactionService;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DbActiveTransactionServiceImpl implements IDbActiveTransactionService {

    /**
     * InnoDB transaction metadata joined with the owning processlist row. The
     * transaction source is always {@code information_schema.innodb_trx}; each refresh is
     * a new live snapshot and does not retain rows for transactions that have committed
     * or rolled back.
     */
    private static final String SQL_ACTIVE_TRANSACTIONS =
            "SELECT t.trx_id, t.trx_state, t.trx_started, "
                    + "TIMESTAMPDIFF(SECOND, t.trx_started, NOW()) AS trx_age_seconds, "
                    + "t.trx_isolation_level, t.trx_rows_locked, t.trx_rows_modified, "
                    + "t.trx_lock_structs, t.trx_mysql_thread_id, "
                    + "p.ID AS process_id, p.USER AS process_user, p.HOST AS process_host, p.DB AS process_db, "
                    + "t.trx_query, "
                    + "%s "
                    + "FROM information_schema.innodb_trx t "
                    + "LEFT JOIN information_schema.processlist p ON t.trx_mysql_thread_id = p.ID "
                    + "%s "
                    + "ORDER BY t.trx_started";

    private static final String LOCK_COLUMNS_80 =
            "w.requesting_lock_id, w.blocking_lock_id, w.blocking_trx_id, "
                    + "w.requesting_ps_thread_id, w.blocking_ps_thread_id, "
                    + "w.waiting_object_schema, w.waiting_object_name, w.waiting_index_name, "
                    + "w.waiting_lock_type, w.waiting_lock_mode, w.waiting_lock_status, w.waiting_lock_data, "
                    + "w.blocking_object_schema, w.blocking_object_name, w.blocking_index_name, "
                    + "w.blocking_lock_type, w.blocking_lock_mode, w.blocking_lock_status, w.blocking_lock_data, "
                    + "bt.trx_mysql_thread_id AS blocking_thread_id, bp.ID AS blocking_process_id, "
                    + "bp.USER AS blocking_user, bp.HOST AS blocking_host, bp.DB AS blocking_db";

    private static final String LOCK_JOIN_80 =
            "LEFT JOIN ("
                    + "SELECT dlw.REQUESTING_ENGINE_TRANSACTION_ID AS requesting_trx_id, "
                    + "dlw.REQUESTING_ENGINE_LOCK_ID AS requesting_lock_id, "
                    + "dlw.BLOCKING_ENGINE_LOCK_ID AS blocking_lock_id, "
                    + "dlw.BLOCKING_ENGINE_TRANSACTION_ID AS blocking_trx_id, "
                    + "dlw.REQUESTING_THREAD_ID AS requesting_ps_thread_id, "
                    + "dlw.BLOCKING_THREAD_ID AS blocking_ps_thread_id, "
                    + "rl.OBJECT_SCHEMA AS waiting_object_schema, rl.OBJECT_NAME AS waiting_object_name, "
                    + "rl.INDEX_NAME AS waiting_index_name, rl.LOCK_TYPE AS waiting_lock_type, "
                    + "rl.LOCK_MODE AS waiting_lock_mode, rl.LOCK_STATUS AS waiting_lock_status, "
                    + "rl.LOCK_DATA AS waiting_lock_data, "
                    + "bl.OBJECT_SCHEMA AS blocking_object_schema, bl.OBJECT_NAME AS blocking_object_name, "
                    + "bl.INDEX_NAME AS blocking_index_name, bl.LOCK_TYPE AS blocking_lock_type, "
                    + "bl.LOCK_MODE AS blocking_lock_mode, bl.LOCK_STATUS AS blocking_lock_status, "
                    + "bl.LOCK_DATA AS blocking_lock_data "
                    + "FROM performance_schema.data_lock_waits dlw "
                    + "LEFT JOIN performance_schema.data_locks rl "
                    + "ON dlw.REQUESTING_ENGINE_LOCK_ID = rl.ENGINE_LOCK_ID "
                    + "AND dlw.ENGINE = rl.ENGINE "
                    + "LEFT JOIN performance_schema.data_locks bl "
                    + "ON dlw.BLOCKING_ENGINE_LOCK_ID = bl.ENGINE_LOCK_ID "
                    + "AND dlw.ENGINE = bl.ENGINE"
                    + ") w ON w.requesting_trx_id = t.trx_id "
                    + "LEFT JOIN information_schema.innodb_trx bt ON bt.trx_id = w.blocking_trx_id "
                    + "LEFT JOIN information_schema.processlist bp ON bt.trx_mysql_thread_id = bp.ID";

    private static final String LOCK_COLUMNS_57 =
            "w.requesting_lock_id, w.blocking_lock_id, w.blocking_trx_id, "
                    + "NULL AS requesting_ps_thread_id, NULL AS blocking_ps_thread_id, "
                    + "NULL AS waiting_object_schema, w.waiting_object_name, w.waiting_index_name, "
                    + "w.waiting_lock_type, w.waiting_lock_mode, NULL AS waiting_lock_status, w.waiting_lock_data, "
                    + "NULL AS blocking_object_schema, w.blocking_object_name, w.blocking_index_name, "
                    + "w.blocking_lock_type, w.blocking_lock_mode, NULL AS blocking_lock_status, w.blocking_lock_data, "
                    + "bt.trx_mysql_thread_id AS blocking_thread_id, bp.ID AS blocking_process_id, "
                    + "bp.USER AS blocking_user, bp.HOST AS blocking_host, bp.DB AS blocking_db";

    private static final String LOCK_JOIN_57 =
            "LEFT JOIN ("
                    + "SELECT lw.requesting_trx_id, lw.requested_lock_id AS requesting_lock_id, "
                    + "lw.blocking_lock_id, lw.blocking_trx_id, "
                    + "rl.lock_table AS waiting_object_name, rl.lock_index AS waiting_index_name, "
                    + "rl.lock_type AS waiting_lock_type, rl.lock_mode AS waiting_lock_mode, "
                    + "rl.lock_data AS waiting_lock_data, "
                    + "bl.lock_table AS blocking_object_name, bl.lock_index AS blocking_index_name, "
                    + "bl.lock_type AS blocking_lock_type, bl.lock_mode AS blocking_lock_mode, "
                    + "bl.lock_data AS blocking_lock_data "
                    + "FROM information_schema.innodb_lock_waits lw "
                    + "LEFT JOIN information_schema.innodb_locks rl ON lw.requested_lock_id = rl.lock_id "
                    + "LEFT JOIN information_schema.innodb_locks bl ON lw.blocking_lock_id = bl.lock_id"
                    + ") w ON w.requesting_trx_id = t.trx_id "
                    + "LEFT JOIN information_schema.innodb_trx bt ON bt.trx_id = w.blocking_trx_id "
                    + "LEFT JOIN information_schema.processlist bp ON bt.trx_mysql_thread_id = bp.ID";

    private static final String LOCK_COLUMNS_UNAVAILABLE =
            "NULL AS requesting_lock_id, NULL AS blocking_lock_id, NULL AS blocking_trx_id, "
                    + "NULL AS requesting_ps_thread_id, NULL AS blocking_ps_thread_id, "
                    + "NULL AS waiting_object_schema, NULL AS waiting_object_name, NULL AS waiting_index_name, "
                    + "NULL AS waiting_lock_type, NULL AS waiting_lock_mode, NULL AS waiting_lock_status, "
                    + "NULL AS waiting_lock_data, "
                    + "NULL AS blocking_object_schema, NULL AS blocking_object_name, NULL AS blocking_index_name, "
                    + "NULL AS blocking_lock_type, NULL AS blocking_lock_mode, NULL AS blocking_lock_status, "
                    + "NULL AS blocking_lock_data, "
                    + "NULL AS blocking_thread_id, NULL AS blocking_process_id, "
                    + "NULL AS blocking_user, NULL AS blocking_host, NULL AS blocking_db";

    private static final String SQL_PROBE_MYSQL80_LOCKS =
            "SELECT 1 FROM performance_schema.data_lock_waits WHERE 1 = 0";

    private static final String SQL_PROBE_MYSQL57_LOCKS =
            "SELECT 1 FROM information_schema.innodb_lock_waits WHERE 1 = 0";

    private static final String LOCK_METADATA_AVAILABLE = "AVAILABLE";
    private static final String LOCK_METADATA_UNAVAILABLE = "UNAVAILABLE";

    @Override
    public List<Map<String, Object>> activeTransactions() {
        Connection connection = Chat2DBContext.getConnection();
        LockMetadataQuery lockMetadataQuery = resolveLockMetadataQuery(connection);
        try {
            return DefaultSQLExecutor.getInstance().execute(connection, lockMetadataQuery.sql(), resultSet -> {
                List<Map<String, Object>> transactions = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("trxId", resultSet.getString("trx_id"));
                    row.put("state", resultSet.getString("trx_state"));
                    row.put("startedAt", resultSet.getTimestamp("trx_started"));
                    row.put("ageSeconds", resultSet.getLong("trx_age_seconds"));
                    row.put("isolationLevel", resultSet.getString("trx_isolation_level"));
                    row.put("rowsLocked", resultSet.getLong("trx_rows_locked"));
                    row.put("rowsModified", resultSet.getLong("trx_rows_modified"));
                    row.put("lockStructs", resultSet.getLong("trx_lock_structs"));
                    Long threadId = nullableLong(resultSet, "trx_mysql_thread_id");
                    row.put("threadId", threadId);
                    row.put("user", resultSet.getString("process_user"));
                    row.put("host", resultSet.getString("process_host"));
                    row.put("db", resultSet.getString("process_db"));
                    String query = resultSet.getString("trx_query");
                    row.put("query", query);
                    Long processId = nullableLong(resultSet, "process_id");
                    row.put("sessionAvailable", processId != null);
                    row.put("sessionState", processId == null ? "DISAPPEARED_OR_HIDDEN" : "LIVE");
                    row.put("canOpenSession", processId != null && threadId != null);
                    row.put("queryState", query == null ? "UNAVAILABLE" : "VISIBLE");
                    row.put("lockMetadataState", lockMetadataQuery.available()
                            ? LOCK_METADATA_AVAILABLE : LOCK_METADATA_UNAVAILABLE);
                    row.put("lockMetadataSource", lockMetadataQuery.source());
                    row.put("lockMetadataMessage", lockMetadataQuery.message());
                    putLockWait(row, resultSet);
                    transactions.add(row);
                }
                return transactions;
            });
        } catch (RuntimeException exception) {
            if (lockMetadataQuery.available() && hasMissingOrDeniedMetadataError(exception)) {
                return activeTransactionsWithoutLockMetadata(connection, metadataErrorMessage(exception));
            }
            if (hasProcessPrivilegeError(exception)) {
                throw new BusinessException("mysql.activeTransaction.processPrivilegeRequired", null, exception);
            }
            throw exception;
        }
    }

    private List<Map<String, Object>> activeTransactionsWithoutLockMetadata(Connection connection, String message) {
        LockMetadataQuery fallback = unavailableLockMetadataQuery(message);
        return DefaultSQLExecutor.getInstance().execute(connection, fallback.sql(), resultSet -> {
            List<Map<String, Object>> transactions = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("trxId", resultSet.getString("trx_id"));
                row.put("state", resultSet.getString("trx_state"));
                row.put("startedAt", resultSet.getTimestamp("trx_started"));
                row.put("ageSeconds", resultSet.getLong("trx_age_seconds"));
                row.put("isolationLevel", resultSet.getString("trx_isolation_level"));
                row.put("rowsLocked", resultSet.getLong("trx_rows_locked"));
                row.put("rowsModified", resultSet.getLong("trx_rows_modified"));
                row.put("lockStructs", resultSet.getLong("trx_lock_structs"));
                Long threadId = nullableLong(resultSet, "trx_mysql_thread_id");
                row.put("threadId", threadId);
                row.put("user", resultSet.getString("process_user"));
                row.put("host", resultSet.getString("process_host"));
                row.put("db", resultSet.getString("process_db"));
                String query = resultSet.getString("trx_query");
                Long processId = nullableLong(resultSet, "process_id");
                row.put("query", query);
                row.put("sessionAvailable", processId != null);
                row.put("sessionState", processId == null ? "DISAPPEARED_OR_HIDDEN" : "LIVE");
                row.put("canOpenSession", processId != null && threadId != null);
                row.put("queryState", query == null ? "UNAVAILABLE" : "VISIBLE");
                row.put("lockMetadataState", LOCK_METADATA_UNAVAILABLE);
                row.put("lockMetadataSource", null);
                row.put("lockMetadataMessage", fallback.message());
                putLockWait(row, resultSet);
                transactions.add(row);
            }
            return transactions;
        });
    }

    private static void putLockWait(Map<String, Object> row, ResultSet resultSet) throws SQLException {
        Long blockingThreadId = nullableLong(resultSet, "blocking_thread_id");
        Long blockingProcessId = nullableLong(resultSet, "blocking_process_id");
        String waitingLockId = resultSet.getString("requesting_lock_id");
        String blockingLockId = resultSet.getString("blocking_lock_id");
        row.put("waitingLockId", waitingLockId);
        row.put("blockingLockId", blockingLockId);
        row.put("blockingTrxId", resultSet.getString("blocking_trx_id"));
        row.put("waitingPerformanceSchemaThreadId", nullableLong(resultSet, "requesting_ps_thread_id"));
        row.put("blockingPerformanceSchemaThreadId", nullableLong(resultSet, "blocking_ps_thread_id"));
        row.put("blockingThreadId", blockingThreadId);
        row.put("blockingSessionAvailable", blockingProcessId != null);
        row.put("canOpenBlockingSession", blockingProcessId != null && blockingThreadId != null);
        row.put("blockingUser", resultSet.getString("blocking_user"));
        row.put("blockingHost", resultSet.getString("blocking_host"));
        row.put("blockingDb", resultSet.getString("blocking_db"));
        row.put("waitingObject", lockObject(resultSet, "waiting_object_schema", "waiting_object_name"));
        row.put("waitingIndex", resultSet.getString("waiting_index_name"));
        row.put("waitingLockType", resultSet.getString("waiting_lock_type"));
        row.put("waitingLockMode", resultSet.getString("waiting_lock_mode"));
        row.put("waitingLockStatus", resultSet.getString("waiting_lock_status"));
        row.put("waitingLockData", resultSet.getString("waiting_lock_data"));
        row.put("blockingObject", lockObject(resultSet, "blocking_object_schema", "blocking_object_name"));
        row.put("blockingIndex", resultSet.getString("blocking_index_name"));
        row.put("blockingLockType", resultSet.getString("blocking_lock_type"));
        row.put("blockingLockMode", resultSet.getString("blocking_lock_mode"));
        row.put("blockingLockStatus", resultSet.getString("blocking_lock_status"));
        row.put("blockingLockData", resultSet.getString("blocking_lock_data"));
        row.put("lockWaitAvailable", waitingLockId != null || blockingLockId != null);
    }

    private static String lockObject(ResultSet resultSet, String schemaColumn, String objectColumn) throws SQLException {
        String schema = resultSet.getString(schemaColumn);
        String object = resultSet.getString(objectColumn);
        if (schema == null || schema.isBlank()) {
            return object;
        }
        if (object == null || object.isBlank()) {
            return schema;
        }
        return schema + "." + object;
    }

    private static Long nullableLong(ResultSet resultSet, String column) throws SQLException {
        long value = resultSet.getLong(column);
        return resultSet.wasNull() ? null : value;
    }

    private static LockMetadataQuery resolveLockMetadataQuery(Connection connection) {
        try {
            executeProbe(connection, SQL_PROBE_MYSQL80_LOCKS);
            return new LockMetadataQuery(activeTransactionSql(LOCK_COLUMNS_80, LOCK_JOIN_80),
                    true, "MYSQL_80_PERFORMANCE_SCHEMA", null);
        } catch (RuntimeException exception) {
            if (!hasMissingOrDeniedMetadataError(exception)) {
                throw exception;
            }
        }

        try {
            executeProbe(connection, SQL_PROBE_MYSQL57_LOCKS);
            return new LockMetadataQuery(activeTransactionSql(LOCK_COLUMNS_57, LOCK_JOIN_57),
                    true, "MYSQL_57_INFORMATION_SCHEMA", null);
        } catch (RuntimeException exception) {
            if (!hasMissingOrDeniedMetadataError(exception)) {
                throw exception;
            }
            return unavailableLockMetadataQuery(metadataErrorMessage(exception));
        }
    }

    private static void executeProbe(Connection connection, String sql) {
        DefaultSQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }

    static String activeTransactionSql(String lockColumns, String lockJoin) {
        return String.format(SQL_ACTIVE_TRANSACTIONS, lockColumns, lockJoin);
    }

    static LockMetadataQuery unavailableLockMetadataQuery(String message) {
        return new LockMetadataQuery(activeTransactionSql(LOCK_COLUMNS_UNAVAILABLE, ""),
                false, null, message);
    }

    static boolean hasProcessPrivilegeError(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SQLException sqlException
                    && (sqlException.getErrorCode() == 1227
                    || sqlException.getMessage() != null
                    && sqlException.getMessage().toUpperCase().contains("PROCESS"))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    static boolean hasMissingOrDeniedMetadataError(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SQLException sqlException) {
                int errorCode = sqlException.getErrorCode();
                String message = sqlException.getMessage();
                String normalized = message == null ? "" : message.toUpperCase();
                if (errorCode == 1142 || errorCode == 1146 || errorCode == 1227
                        || normalized.contains("DATA_LOCK")
                        || normalized.contains("INNODB_LOCK")
                        || normalized.contains("PERFORMANCE_SCHEMA")
                        || normalized.contains("COMMAND DENIED")
                        || normalized.contains("DOESN'T EXIST")
                        || normalized.contains("DOES NOT EXIST")
                        || normalized.contains("PROCESS")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    static String metadataErrorMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SQLException) {
                return "Lock wait metadata is unavailable or access was denied.";
            }
            current = current.getCause();
        }
        return "Lock wait metadata is not available for this MySQL version or account.";
    }

    record LockMetadataQuery(String sql, boolean available, String source, String message) {
    }
}

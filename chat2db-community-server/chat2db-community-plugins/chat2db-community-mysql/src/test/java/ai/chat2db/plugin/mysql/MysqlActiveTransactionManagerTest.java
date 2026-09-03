package ai.chat2db.plugin.mysql;

import ai.chat2db.community.domain.api.model.transaction.ActiveTransaction;
import ai.chat2db.community.domain.api.model.transaction.ActiveTransaction.LockMetadataSource;
import ai.chat2db.community.domain.api.model.transaction.ActiveTransaction.LockMetadataState;
import ai.chat2db.community.domain.api.model.transaction.ActiveTransaction.QueryState;
import ai.chat2db.community.tools.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MysqlActiveTransactionManagerTest {

    @Test
    void recognizesProcessPrivilegeFailuresThroughWrappedCauses() {
        SQLException denied = new SQLException(
                "Access denied; you need (at least one of) the PROCESS privilege", "42000", 1227);

        assertTrue(MysqlActiveTransactionManager.hasProcessPrivilegeError(
                new IllegalStateException("execution failed", denied)));
        assertFalse(MysqlActiveTransactionManager.hasProcessPrivilegeError(
                new SQLException("table not found", "42S02", 1146)));
    }

    @Test
    void buildsLiveTransactionQueryWithMySql80LockWaitMetadata() {
        String sql = MysqlActiveTransactionManager.activeTransactionSql("w.requesting_lock_id", "JOIN wait_table w");

        assertTrue(sql.contains("FROM information_schema.innodb_trx t"));
        assertTrue(sql.contains("LEFT JOIN information_schema.processlist p ON t.trx_mysql_thread_id = p.ID"));
        assertTrue(sql.contains("w.requesting_lock_id"));
        assertTrue(sql.contains("JOIN wait_table w"));
        assertTrue(sql.contains("ORDER BY t.trx_started"));
    }

    @Test
    void unavailableLockMetadataQueryKeepsLiveTransactionSource() {
        MysqlActiveTransactionManager.LockMetadataQuery query =
                MysqlActiveTransactionManager.unavailableLockMetadataQuery();

        assertFalse(query.available());
        assertTrue(query.sql().contains("FROM information_schema.innodb_trx t"));
        assertTrue(query.sql().contains("NULL AS blocking_trx_id"));
        assertTrue(query.sql().contains("LEFT JOIN information_schema.processlist p"));
        assertTrue(query.sql().contains("ORDER BY t.trx_started"));
    }

    @Test
    void recognizesMissingOrDeniedLockMetadataFailures() {
        assertTrue(MysqlActiveTransactionManager.hasMissingOrDeniedMetadataError(
                new SQLException("Table 'performance_schema.data_lock_waits' doesn't exist", "42S02", 1146)));
        assertTrue(MysqlActiveTransactionManager.hasMissingOrDeniedMetadataError(
                new SQLException("SELECT command denied to user for table 'data_locks'", "42000", 1142)));
        assertFalse(MysqlActiveTransactionManager.hasMissingOrDeniedMetadataError(
                new SQLException("Syntax error", "42000", 1064)));
    }

    @Test
    void mapsResultSetToTypedTransactionResponse() throws SQLException {
        ResultSet resultSet = resultSet(Map.ofEntries(
                Map.entry("trx_id", "421337"),
                Map.entry("trx_state", "LOCK WAIT"),
                Map.entry("trx_started", Timestamp.valueOf("2026-08-31 12:00:00")),
                Map.entry("trx_age_seconds", 12L),
                Map.entry("trx_isolation_level", "REPEATABLE READ"),
                Map.entry("trx_rows_locked", 1L),
                Map.entry("trx_rows_modified", 0L),
                Map.entry("trx_lock_structs", 2L),
                Map.entry("trx_mysql_thread_id", 45L),
                Map.entry("process_id", 45L),
                Map.entry("process_user", "ops002_user"),
                Map.entry("process_host", "127.0.0.1:50000"),
                Map.entry("process_db", "ops002_test"),
                Map.entry("requesting_lock_id", "421337:7:3:2"),
                Map.entry("blocking_lock_id", "421336:7:3:2"),
                Map.entry("blocking_trx_id", "421336"),
                Map.entry("blocking_thread_id", 44L),
                Map.entry("blocking_process_id", 44L)
        ));

        ActiveTransaction transaction = MysqlActiveTransactionManager.readTransaction(
                resultSet,
                new MysqlActiveTransactionManager.LockMetadataQuery(
                        "SELECT ...", true, LockMetadataSource.MYSQL_80_PERFORMANCE_SCHEMA)
        );

        assertEquals("421337", transaction.getTrxId());
        assertEquals(45L, transaction.getThreadId());
        assertEquals(QueryState.UNAVAILABLE, transaction.getQueryState());
        assertEquals(LockMetadataState.AVAILABLE, transaction.getLockMetadataState());
        assertEquals(LockMetadataSource.MYSQL_80_PERFORMANCE_SCHEMA, transaction.getLockMetadataSource());
        assertTrue(transaction.getCanOpenSession());
        assertEquals("SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO\n"
                + "FROM information_schema.PROCESSLIST\nWHERE ID = 45;",
                transaction.getConnectionInspectionSql());
        assertTrue(transaction.getLockWaitAvailable());
        assertEquals(44L, transaction.getBlockingThreadId());
        assertEquals("SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO\n"
                + "FROM information_schema.PROCESSLIST\nWHERE ID = 44;",
                transaction.getBlockingConnectionInspectionSql());
        assertNull(transaction.getQuery());
    }

    @Test
    void unavailableConnectionsDoNotExposeInspectionSql() throws SQLException {
        ResultSet resultSet = resultSet(Map.ofEntries(
                Map.entry("trx_id", "421338"),
                Map.entry("trx_state", "RUNNING"),
                Map.entry("trx_started", Timestamp.valueOf("2026-08-31 12:00:00")),
                Map.entry("trx_mysql_thread_id", 46L)
        ));

        ActiveTransaction transaction = MysqlActiveTransactionManager.readTransaction(
                resultSet,
                MysqlActiveTransactionManager.unavailableLockMetadataQuery()
        );

        assertFalse(transaction.getCanOpenSession());
        assertNull(transaction.getConnectionInspectionSql());
        assertFalse(transaction.getCanOpenBlockingSession());
        assertNull(transaction.getBlockingConnectionInspectionSql());
    }

    @Test
    void fallbackProcessPrivilegeFailureUsesSanitizedBusinessCode() {
        RuntimeException executionFailure = new IllegalStateException("execution failed",
                new SQLException("Access denied; you need (at least one of) the PROCESS privilege", "42000", 1227));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            throw MysqlActiveTransactionManager.sanitizeActiveTransactionQueryException(executionFailure);
        });

        assertEquals(MysqlActiveTransactionManager.PROCESS_PRIVILEGE_ERROR_CODE, exception.getCode());
        assertSame(executionFailure, exception.getCause());
    }

    @Test
    void nonProcessFallbackFailureRemainsOriginalRuntimeException() {
        RuntimeException executionFailure = new IllegalStateException("syntax error",
                new SQLException("Syntax error", "42000", 1064));

        assertSame(executionFailure,
                MysqlActiveTransactionManager.sanitizeActiveTransactionQueryException(executionFailure));
    }

    private static ResultSet resultSet(Map<String, Object> values) {
        AtomicBoolean wasNull = new AtomicBoolean();
        return (ResultSet) Proxy.newProxyInstance(
                MysqlActiveTransactionManagerTest.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, arguments) -> {
                    if ("wasNull".equals(method.getName())) {
                        return wasNull.get();
                    }
                    if (arguments != null && arguments.length == 1 && arguments[0] instanceof String column) {
                        Object value = values.get(column);
                        wasNull.set(value == null);
                        return switch (method.getName()) {
                            case "getString" -> value == null ? null : value.toString();
                            case "getTimestamp" -> value;
                            case "getLong" -> value == null ? 0L : ((Number) value).longValue();
                            default -> throw new UnsupportedOperationException(method.getName());
                        };
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}

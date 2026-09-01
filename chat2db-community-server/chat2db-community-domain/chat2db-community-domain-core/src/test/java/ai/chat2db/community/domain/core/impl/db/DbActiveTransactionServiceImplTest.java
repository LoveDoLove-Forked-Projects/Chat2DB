package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.tools.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DbActiveTransactionServiceImplTest {

    @Test
    void recognizesProcessPrivilegeFailuresThroughWrappedCauses() {
        SQLException denied = new SQLException(
                "Access denied; you need (at least one of) the PROCESS privilege", "42000", 1227);

        assertTrue(DbActiveTransactionServiceImpl.hasProcessPrivilegeError(
                new IllegalStateException("execution failed", denied)));
        assertFalse(DbActiveTransactionServiceImpl.hasProcessPrivilegeError(
                new SQLException("table not found", "42S02", 1146)));
    }

    @Test
    void buildsLiveTransactionQueryWithMySql80LockWaitMetadata() {
        String sql = DbActiveTransactionServiceImpl.activeTransactionSql("w.requesting_lock_id", "JOIN wait_table w");

        assertTrue(sql.contains("FROM information_schema.innodb_trx t"));
        assertTrue(sql.contains("LEFT JOIN information_schema.processlist p ON t.trx_mysql_thread_id = p.ID"));
        assertTrue(sql.contains("w.requesting_lock_id"));
        assertTrue(sql.contains("JOIN wait_table w"));
        assertTrue(sql.contains("ORDER BY t.trx_started"));
    }

    @Test
    void unavailableLockMetadataQueryKeepsLiveTransactionSource() {
        DbActiveTransactionServiceImpl.LockMetadataQuery query =
                DbActiveTransactionServiceImpl.unavailableLockMetadataQuery("missing metadata");

        assertFalse(query.available());
        assertTrue(query.sql().contains("FROM information_schema.innodb_trx t"));
        assertTrue(query.sql().contains("NULL AS blocking_trx_id"));
        assertTrue(query.sql().contains("LEFT JOIN information_schema.processlist p"));
        assertTrue(query.sql().contains("ORDER BY t.trx_started"));
    }

    @Test
    void recognizesMissingOrDeniedLockMetadataFailures() {
        assertTrue(DbActiveTransactionServiceImpl.hasMissingOrDeniedMetadataError(
                new SQLException("Table 'performance_schema.data_lock_waits' doesn't exist", "42S02", 1146)));
        assertTrue(DbActiveTransactionServiceImpl.hasMissingOrDeniedMetadataError(
                new SQLException("SELECT command denied to user for table 'data_locks'", "42000", 1142)));
        assertFalse(DbActiveTransactionServiceImpl.hasMissingOrDeniedMetadataError(
                new SQLException("Syntax error", "42000", 1064)));
    }

    @Test
    void lockMetadataDiagnosticDoesNotExposeJdbcDetails() {
        String message = DbActiveTransactionServiceImpl.metadataErrorMessage(
                new SQLException("SELECT denied for private_schema.data_locks", "42000", 1142));

        assertTrue(message.contains("unavailable or access was denied"));
        assertFalse(message.contains("private_schema"));
    }

    @Test
    void fallbackProcessPrivilegeFailureUsesSanitizedBusinessCode() {
        RuntimeException executionFailure = new IllegalStateException("execution failed",
                new SQLException("Access denied; you need (at least one of) the PROCESS privilege", "42000", 1227));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            throw DbActiveTransactionServiceImpl.sanitizeActiveTransactionQueryException(executionFailure);
        });

        assertEquals("mysql.activeTransaction.processPrivilegeRequired", exception.getCode());
        assertSame(executionFailure, exception.getCause());
    }

    @Test
    void nonProcessFallbackFailureRemainsOriginalRuntimeException() {
        RuntimeException executionFailure = new IllegalStateException("syntax error",
                new SQLException("Syntax error", "42000", 1064));

        assertSame(executionFailure,
                DbActiveTransactionServiceImpl.sanitizeActiveTransactionQueryException(executionFailure));
    }
}

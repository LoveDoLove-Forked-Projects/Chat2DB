package ai.chat2db.community.domain.core.impl.db;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}

package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DbTableServiceImplTest {

    private static final String TEST_DB_TYPE = "ISSUE_1830_TEST";
    private static final String CREATE_EXAMPLE = "CREATE TABLE issue_1830 (id BIGINT)";
    private static final String ALTER_EXAMPLE = "ALTER TABLE issue_1830 ADD name VARCHAR(64)";

    private IPlugin previousPlugin;
    private DbTableServiceImpl tableService;

    @BeforeEach
    void setUp() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setDbType(TEST_DB_TYPE);
        dbConfig.setSimpleCreateTable(CREATE_EXAMPLE);
        dbConfig.setSimpleAlterTable(ALTER_EXAMPLE);

        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, () -> dbConfig);
        Chat2DBContext.removeContext();
        tableService = new DbTableServiceImpl(null);
    }

    @AfterEach
    void tearDown() {
        Chat2DBContext.removeContext();
        if (previousPlugin == null) {
            Chat2DBContext.PLUGIN_MAP.remove(TEST_DB_TYPE);
        } else {
            Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, previousPlugin);
        }
    }

    @Test
    void createTableExampleUsesRequestedDatabaseTypeWithoutConnectionContext() {
        assertEquals(CREATE_EXAMPLE, tableService.createTableExample(TEST_DB_TYPE));
    }

    @Test
    void alterTableExampleUsesRequestedDatabaseTypeWithoutConnectionContext() {
        assertEquals(ALTER_EXAMPLE, tableService.alterTableExample(TEST_DB_TYPE));
    }
}

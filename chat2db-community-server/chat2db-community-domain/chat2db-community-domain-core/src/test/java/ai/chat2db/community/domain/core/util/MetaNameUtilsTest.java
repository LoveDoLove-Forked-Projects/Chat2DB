package ai.chat2db.community.domain.core.util;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.request.db.DbTableQueryRequest;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MetaNameUtilsTest {

    private static final String TEST_DB_TYPE = "ORACLE";

    private IPlugin previousPlugin;

    @BeforeEach
    void setUp() {
        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, new TestPlugin());
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
    void buildRequestUppercasesUnquotedOracleTableName() {
        Chat2DBContext.putContext(connectInfo());
        DbTableQueryRequest request = new DbTableQueryRequest();

        MetaNameUtils.buildRequest(request, "ys_mz_jzls");

        assertEquals("YS_MZ_JZLS", request.getTableName());
        assertNull(request.getDatabaseName());
    }

    @Test
    void buildRequestKeepsQuotedOracleTableNameCase() {
        Chat2DBContext.putContext(connectInfo());
        DbTableQueryRequest request = new DbTableQueryRequest();

        MetaNameUtils.buildRequest(request, "\"ys_mz_jzls\"");

        assertEquals("ys_mz_jzls", request.getTableName());
    }

    @Test
    void buildRequestUsesOracleSchemaSupport() {
        Chat2DBContext.putContext(connectInfo());
        DbTableQueryRequest request = new DbTableQueryRequest();

        MetaNameUtils.buildRequest(request, "his.ys_mz_jzls");

        assertEquals("HIS", request.getSchemaName());
        assertEquals("YS_MZ_JZLS", request.getTableName());
        assertNull(request.getDatabaseName());
    }

    @Test
    void buildRequestNormalizesExistingOracleSchemaAndKeepsQuotedTableCase() {
        Chat2DBContext.putContext(connectInfo());
        DbTableQueryRequest request = new DbTableQueryRequest();
        request.setSchemaName("his");

        MetaNameUtils.buildRequest(request, "\"ys_mz_jzls\"");

        assertEquals("HIS", request.getSchemaName());
        assertEquals("ys_mz_jzls", request.getTableName());
        assertNull(request.getDatabaseName());
    }

    private static ConnectInfo connectInfo() {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(1L);
        connectInfo.setDbType(TEST_DB_TYPE);
        return connectInfo;
    }

    private static final class TestPlugin implements IPlugin {

        private final DBConfig dbConfig;
        private final IDbMetaData metaData = new DefaultMetaService() {
            private final DefaultSQLIdentifierProcessor identifierProcessor = new DefaultSQLIdentifierProcessor() {
                @Override
                public String convertIdentifierCase(String identifier) {
                    return identifier == null ? null : identifier.toUpperCase(Locale.ROOT);
                }
            };

            @Override
            public DefaultSQLIdentifierProcessor getSQLIdentifierProcessor() {
                return identifierProcessor;
            }
        };

        private TestPlugin() {
            DriverConfig driverConfig = new DriverConfig();
            driverConfig.setDbType(TEST_DB_TYPE);
            driverConfig.setDefaultDriver(true);

            dbConfig = new DBConfig();
            dbConfig.setDbType(TEST_DB_TYPE);
            dbConfig.setDefaultDriverConfig(driverConfig);
            dbConfig.setSupportSchema(true);
            dbConfig.setSupportDatabase(false);
        }

        @Override
        public DBConfig getDBConfig() {
            return dbConfig;
        }

        @Override
        public IDbMetaData getDbMetaData() {
            return metaData;
        }
    }
}

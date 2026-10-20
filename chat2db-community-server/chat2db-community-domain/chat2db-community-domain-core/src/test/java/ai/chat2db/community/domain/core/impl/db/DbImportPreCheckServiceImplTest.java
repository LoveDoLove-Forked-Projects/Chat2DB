package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.db.ImportPreCheck;
import ai.chat2db.community.domain.api.model.db.ImportPreCheckIssue;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.task.ImportColumnMapping;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.model.request.TableMetadataRequest;
import ai.chat2db.spi.model.request.TablesRequest;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pre-flight numbers: full-file row count, sample primary-key conflicts and sample type errors. */
class DbImportPreCheckServiceImplTest {

    private static final String DB_TYPE = "IMPORT_PRECHECK_TEST";

    private static final long DATA_SOURCE_ID = 930_202L;

    private static final String DATABASE = "app";

    private final RecordingMetaData metaData = new RecordingMetaData();

    private IPlugin previousPlugin;

    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        DBConfig config = new DBConfig();
        config.setDbType(DB_TYPE);
        config.setDefaultDriverConfig(new DriverConfig());
        config.setSupportDatabase(true);
        config.setSupportSchema(false);
        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(DB_TYPE, plugin(config));
        connection = DriverManager.getConnection("jdbc:h2:mem:import_precheck;MODE=MySQL");
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS \"app\"");
            statement.execute("CREATE TABLE \"app\".\"orders\" (\"id\" INT PRIMARY KEY, \"name\" VARCHAR(20))");
            statement.execute("INSERT INTO \"app\".\"orders\" (\"id\", \"name\") VALUES (2, 'existing')");
        }
        Chat2DBContext.putContext(connectInfo());
    }

    @AfterEach
    void tearDown() throws Exception {
        Chat2DBContext.removeContext();
        if (previousPlugin == null) {
            Chat2DBContext.PLUGIN_MAP.remove(DB_TYPE);
        } else {
            Chat2DBContext.PLUGIN_MAP.put(DB_TYPE, previousPlugin);
        }
        connection.close();
    }

    @Test
    void reportsTotalRowsAndSamplePrimaryKeyConflicts(@TempDir Path directory) throws Exception {
        Path csv = write(directory, "id,name\n1,Alice\n2,Bob\n3,Carol\n");

        ImportPreCheck check = service().analyze(DATA_SOURCE_ID, DATABASE, null, "orders", csv.toFile(),
                null, null, null, mappings());

        assertEquals(3, check.getSampledRows());
        assertTrue(check.isConflictCheckable());
        assertEquals(List.of("id"), check.getKeyColumns());
        assertEquals(1, check.getConflictingSampleRows(), "only id=2 already exists");
        assertEquals(List.of(), check.getTypeMismatches());
    }

    @Test
    void reportsSampleTypeMismatchesWithTheirTargetColumn(@TempDir Path directory) throws Exception {
        Path csv = write(directory, "id,name\n1,Alice\nabc,Bob\n3,Carol\n");

        ImportPreCheck check = service().analyze(DATA_SOURCE_ID, DATABASE, null, "orders", csv.toFile(),
                null, null, null, mappings());

        assertEquals(1, check.getTypeMismatches().size());
        ImportPreCheckIssue issue = check.getTypeMismatches().get(0);
        assertEquals(3L, issue.getRowNumber(), "file row, not data-row index: the header is row 1");
        assertEquals("id", issue.getSourceColumn());
        assertEquals("id", issue.getTargetColumn());
        assertEquals("abc", issue.getValue());
        assertEquals("NUMBER", issue.getReasonCode());
        assertFalse(issue.getReason().isBlank());
    }

    @Test
    void samplesAtMostOneHundredRows(@TempDir Path directory) throws Exception {
        StringBuilder content = new StringBuilder("id,name\n");
        for (int id = 1; id <= 150; id++) {
            content.append(id).append(",name-").append(id).append('\n');
        }

        ImportPreCheck check = service().analyze(DATA_SOURCE_ID, DATABASE, null, "orders",
                write(directory, content.toString()).toFile(), null, null, null, mappings());

        assertEquals(100, check.getSampledRows());
    }

    @Test
    void marksTheConflictCheckUnavailableWithoutAPrimaryKey(@TempDir Path directory) throws Exception {
        metaData.primaryKey = false;
        Path csv = write(directory, "id,name\n1,Alice\n");

        ImportPreCheck check = service().analyze(DATA_SOURCE_ID, DATABASE, null, "orders", csv.toFile(),
                null, null, null, mappings());

        assertFalse(check.isConflictCheckable());
        assertEquals(List.of(), check.getKeyColumns());
        assertEquals(0, check.getConflictingSampleRows());
    }

    private Path write(Path directory, String content) throws Exception {
        Path csv = directory.resolve("orders.csv");
        Files.writeString(csv, content, StandardCharsets.UTF_8);
        return csv;
    }

    private List<ImportColumnMapping> mappings() {
        return List.of(
                ImportColumnMapping.builder().sourceColumn("id").targetColumn("id").build(),
                ImportColumnMapping.builder().sourceColumn("name").targetColumn("name").build());
    }

    private DbImportPreCheckServiceImpl service() {
        return new DbImportPreCheckServiceImpl(new ImportPreviewFileParser());
    }

    private IPlugin plugin(DBConfig config) {
        return new IPlugin() {
            @Override
            public DBConfig getDBConfig() {
                return config;
            }

            @Override
            public IDbMetaData getDbMetaData() {
                return metaData;
            }
        };
    }

    private ConnectInfo connectInfo() {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(DATA_SOURCE_ID);
        connectInfo.setDbType(DB_TYPE);
        connectInfo.setDatabaseName(DATABASE);
        connectInfo.setConnection(connection);
        connectInfo.setDriverConfig(new DriverConfig());
        return connectInfo;
    }

    private static final class RecordingMetaData extends DefaultMetaService {

        private boolean primaryKey = true;

        @Override
        public List<Table> tables(Connection connection, TablesRequest request) {
            return List.of(Table.builder().databaseName(request.getDatabaseName())
                    .schemaName(request.getSchemaName()).name("orders").build());
        }

        @Override
        public List<TableColumn> columns(Connection connection, TableMetadataRequest request) {
            return List.of(
                    TableColumn.builder().name("id").columnType("INT").dataType(Types.INTEGER)
                            .primaryKey(primaryKey).primaryKeyOrder(primaryKey ? 1 : 0).build(),
                    TableColumn.builder().name("name").columnType("VARCHAR").dataType(Types.VARCHAR).build());
        }
    }
}

package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.enums.value.CellValueFormatEnum;
import ai.chat2db.community.domain.api.enums.value.LargeValueTypeEnum;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.db.CellValueChunk;
import ai.chat2db.community.domain.api.model.db.LargeValueReference;
import ai.chat2db.community.domain.api.model.result.ResultCell;
import ai.chat2db.community.domain.api.model.request.db.DbCellValueChunkReadRequest;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CellValueServiceImplTest {

    private static final String TEST_DB_TYPE = "CELL_VALUE_TEST_H2";

    private Connection connection;
    private DbCellValueServiceImpl service;
    private IPlugin previousPlugin;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:large_cell_value;MODE=MySQL;DB_CLOSE_DELAY=-1");
        try (var statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS doc");
            statement.execute("DROP TABLE IF EXISTS \"doc\"");
            statement.execute("CREATE TABLE \"doc\" (\"id\" INT PRIMARY KEY, \"content\" CLOB, \"payload\" BLOB, \"image\" BLOB)");
        }
        try (var statement = connection.prepareStatement("INSERT INTO \"doc\" (\"id\", \"content\", \"payload\", \"image\") VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, 1);
            statement.setCharacterStream(2, new StringReader(repeated("x", 20 * 1024 * 1024)), 20 * 1024 * 1024);
            statement.setBinaryStream(3, new ByteArrayInputStream(binaryValue(20 * 1024 * 1024)), 20 * 1024 * 1024);
            byte[] imageBytes = firstImageBytes(128 * 1024);
            statement.setBinaryStream(4, new ByteArrayInputStream(imageBytes), imageBytes.length);
            statement.executeUpdate();
        }
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDataSourceId(101L);
        connectInfo.setDbType(TEST_DB_TYPE);
        connectInfo.setDatabaseName("");
        connectInfo.setSchemaName("PUBLIC");
        connectInfo.setConnection(connection);
        connectInfo.setDriverConfig(new DriverConfig());
        previousPlugin = Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, new TestPlugin());
        Chat2DBContext.putContext(connectInfo);
        service = new DbCellValueServiceImpl();
    }

    @AfterEach
    void tearDown() throws Exception {
        Chat2DBContext.removeContext();
        if (previousPlugin == null) {
            Chat2DBContext.PLUGIN_MAP.remove(TEST_DB_TYPE);
        } else {
            Chat2DBContext.PLUGIN_MAP.put(TEST_DB_TYPE, previousPlugin);
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void readsTextChunksWithOffsetNextOffsetEofAndMaxLimit() {
        LargeValueReference reference = reference("content", LargeValueTypeEnum.TEXT.code(), "CLOB");

        CellValueChunk first = readChunk(reference, 0L, 300 * 1024, CellValueFormatEnum.TEXT);
        assertEquals(256 * 1024, first.getValue().length());
        assertEquals(0, first.getOffset());
        assertEquals(256 * 1024, first.getNextOffset());
        assertFalse(first.isEof());

        CellValueChunk next = readChunk(reference, first.getNextOffset(), 17, CellValueFormatEnum.TEXT);
        assertEquals(17, next.getValue().length());
        assertEquals(first.getNextOffset() + 17, next.getNextOffset());
    }

    @Test
    void encodesTextChunksFromCharacterStream() {
        LargeValueReference reference = reference("content", LargeValueTypeEnum.TEXT.code(), "CLOB");

        CellValueChunk chunk = readChunk(reference, 0L, 3, CellValueFormatEnum.HEX);

        assertEquals(BaseEncoding.base16().encode("xxx".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                chunk.getValue());
        assertEquals("hex", chunk.getEncoding());
        assertEquals(LargeValueTypeEnum.TEXT.code(), chunk.getDisplayMode());
    }

    @Test
    void rejectsUnsupportedFormatAsBusinessException() {
        assertThrows(BusinessException.class, () -> CellValueFormatEnum.fromRequest("bad-format"));
    }

    @Test
    void readsBinaryAsHexAndBase64WithoutLoadingWholeValue() {
        LargeValueReference reference = reference("payload", LargeValueTypeEnum.BINARY.code(), "BLOB");

        CellValueChunk hex = readChunk(reference, 0L, 12, CellValueFormatEnum.HEX);
        assertEquals(BaseEncoding.base16().encode(firstBytes(12)), hex.getValue());
        assertEquals(12, hex.getNextOffset());
        assertEquals("hex", hex.getEncoding());

        CellValueChunk base64 = readChunk(reference, 0L, 10, CellValueFormatEnum.BASE64);
        assertEquals(9, base64.getNextOffset());
        assertEquals(Base64.getEncoder().encodeToString(firstBytes(9)), base64.getValue());
        assertEquals("base64", base64.getEncoding());
    }

    @Test
    void promotesBlobImageBytesToImageDisplayMode() {
        LargeValueReference reference = reference("image", LargeValueTypeEnum.BINARY.code(), "BLOB");

        CellValueChunk chunk = readChunk(reference, 0L, 9, CellValueFormatEnum.BASE64);

        assertEquals(LargeValueTypeEnum.IMAGE.code(), chunk.getDisplayMode());
        assertEquals("image/png", chunk.getContentType());
        assertEquals(Base64.getEncoder().encodeToString(firstImageBytes(9)), chunk.getValue());
    }

    @Test
    void readsQualifiedTableNameWithoutDuplicatingSchema() {
        LargeValueReference reference = reference("\"PUBLIC\".\"doc\"", "content", LargeValueTypeEnum.TEXT.code(), "CLOB");

        CellValueChunk chunk = readChunk(reference, 0L, 4, CellValueFormatEnum.TEXT);

        assertEquals("xxxx", chunk.getValue());
    }

    @Test
    void rawLocatorValueIsNotSerializedInResultCellApi() throws Exception {
        ResultCell cell = ResultCell.builder()
                .value("1")
                .rawValue(1)
                .build();

        String json = new ObjectMapper().writeValueAsString(cell);

        assertFalse(json.contains("rawValue"));
    }

    private static LargeValueReference reference(String columnName, String valueType, String columnType) {
        return reference("doc", columnName, valueType, columnType);
    }

    private CellValueChunk readChunk(LargeValueReference reference, Long offset, Integer limit,
                                     CellValueFormatEnum format) {
        DbCellValueChunkReadRequest readCellValueChunkRequest = new DbCellValueChunkReadRequest();
        readCellValueChunkRequest.setReference(reference);
        readCellValueChunkRequest.setOffset(offset);
        readCellValueChunkRequest.setLimit(limit);
        readCellValueChunkRequest.setFormat(format == null ? null : format.code());
        return service.readChunk(readCellValueChunkRequest);
    }

    private static LargeValueReference reference(String tableName, String columnName, String valueType, String columnType) {
        return LargeValueReference.builder()
                .dataSourceId(101L)
                .databaseName("")
                .schemaName("PUBLIC")
                .tableName(tableName)
                .columnName(columnName)
                .primaryKey(Map.of("id", 1))
                .valueType(valueType)
                .columnType(columnType)
                .sizeBytes(20L * 1024L * 1024L)
                .build();
    }

    private static String repeated(String value, int times) {
        return value.repeat(times);
    }

    private static byte[] binaryValue(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i % 251);
        }
        return bytes;
    }

    private static byte[] firstBytes(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i % 251);
        }
        return bytes;
    }

    private static byte[] firstImageBytes(int size) {
        byte[] bytes = firstBytes(size);
        bytes[0] = (byte) 0x89;
        bytes[1] = 0x50;
        bytes[2] = 0x4e;
        bytes[3] = 0x47;
        bytes[4] = 0x0d;
        bytes[5] = 0x0a;
        bytes[6] = 0x1a;
        bytes[7] = 0x0a;
        return bytes;
    }

    private static class H2MetaData extends DefaultMetaService implements IDbMetaData {
        @Override
        public String getMetaDataName(String... names) {
            return java.util.Arrays.stream(names)
                    .filter(name -> name != null && !name.isBlank())
                    .map(name -> "\"" + name.replace("\"", "") + "\"")
                    .reduce((first, second) -> first + "." + second)
                    .orElse("");
        }
    }

    private static final class TestPlugin implements IPlugin {

        private final DBConfig dbConfig;
        private final IDbMetaData metaData = new H2MetaData();

        private TestPlugin() {
            dbConfig = new DBConfig();
            dbConfig.setDbType(TEST_DB_TYPE);
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

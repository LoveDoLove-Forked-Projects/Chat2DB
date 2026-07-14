package ai.chat2db.spi;

import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.spi.model.request.DropTableRequest;
import ai.chat2db.spi.model.request.PageLimitRequest;
import ai.chat2db.spi.model.request.TruncateTableRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultSqlBuilderSegmentTest {

    private final DefaultSqlBuilder builder = new DefaultSqlBuilder();

    @Test
    void buildsDqlThroughUnifiedSegment() {
        assertEquals("SELECT * FROM app.public.users",
                builder.dql().buildSelectTable("app", "public", "users"));
        assertEquals("SELECT COUNT(1) FROM app.public.users",
                builder.dql().buildSelectCount("app", "public", "users"));
        assertEquals("SELECT * FROM users\n LIMIT 10",
                builder.dql().buildPageLimit(PageLimitRequest.builder()
                        .sql("SELECT * FROM users")
                        .offset(0)
                        .pageSize(10)
                        .build()));
    }

    @Test
    void buildsDdlThroughUnifiedSegment() {
        Table table = new Table();
        table.setName("users");
        table.setColumnList(List.of());
        Database database = new Database();
        database.setName("app");
        Schema schema = new Schema();
        schema.setName("public");

        assertEquals("CREATE DATABASE app",
                builder.ddl().database().buildCreateDatabase(database));
        assertEquals("USE app",
                builder.ddl().database().buildUseDatabase("app"));
        assertEquals("CREATE SCHEMA public",
                builder.ddl().schema().buildCreateSchema(schema));
        assertEquals("CREATE TABLE \"users\" \n);",
                builder.ddl().table().buildCreateTable(table, TableBuilderConfig.defaultConfig()));
        assertEquals("DROP TABLE app.public.users",
                builder.ddl().table().buildDropTable(new DropTableRequest("app", "public", "users")));
        assertEquals("TRUNCATE TABLE app.public.users",
                builder.ddl().table().buildTruncateTable(new TruncateTableRequest("app", "public", "users")));
    }

    @Test
    void buildsDmlThroughUnifiedSegment() {
        assertEquals("",
                builder.dml().buildTemplate(null, "INSERT"));
    }
}

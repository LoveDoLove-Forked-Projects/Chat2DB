package ai.chat2db.community.start.test.sql;

import java.sql.Connection;
import java.sql.Statement;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;


@Slf4j
@Disabled("Requires a manually configured MySQL database and performs INSERT statements")
public class DbhubJdbcTemplateTest {

    private static JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void prepare() throws Exception {
        log.info("Connecting to MySQL");
    }

    @Test
    @Order(2)
    public void test() throws Exception {

        jdbcTemplate.execute("use data_ops_test");

        Connection connection = jdbcTemplate.getDataSource().getConnection();
        Statement statement = connection.createStatement();
        boolean execute = statement.execute("select * from test_query");
        log.info("execute：{}",execute);

        statement = connection.createStatement();
        execute = statement.execute("INSERT INTO `test_query` (name,date,number) VALUES ('姓名','2022-01-01',123);");
        log.info("execute：{}",execute);
        statement = connection.createStatement();
        execute = statement.execute("show tables");
        log.info("execute：{}",execute);
    }
}

package ai.chat2db.plugin.redis;

import ai.chat2db.community.tools.enums.DataSourceTypeEnum;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisMetaDataTest {

    @AfterEach
    void tearDown() {
        Chat2DBContext.close();
    }

    @Test
    void shouldFallbackToSelectProbeWhenConfigIsForbiddenAndRestoreDatabaseFromConnectInfo() {
        RedisConnectionStub stub = new RedisConnectionStub("5", 4, true);
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDbType(DataSourceTypeEnum.REDIS.name());
        connectInfo.setDriverConfig(new DriverConfig());
        connectInfo.setDatabaseName("2");
        Chat2DBContext.putContext(connectInfo);

        List<Database> databases = new RedisMetaData().databases(stub.connection());

        assertEquals(List.of("0", "1", "2", "3"), names(databases));
        assertEquals(List.of("config get databases", "select 0", "select 1", "select 2", "select 3", "select 4",
            "select 2"), stub.commands());
    }

    @Test
    void shouldUseDatabaseFromRedisUrlWhenConnectInfoDatabaseNameIsBlank() {
        RedisConnectionStub stub = new RedisConnectionStub("0", 2, true);
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setDbType(DataSourceTypeEnum.REDIS.name());
        connectInfo.setDriverConfig(new DriverConfig());
        connectInfo.setUrl("jdbc:redis://127.0.0.1:6379/7");
        Chat2DBContext.putContext(connectInfo);

        List<Database> databases = new RedisMetaData().databases(stub.connection());

        assertEquals(List.of("0", "1"), names(databases));
        assertEquals("select 7", stub.commands().get(stub.commands().size() - 1));
    }

    @Test
    void shouldFallbackToCurrentDatabaseWhenProbeReturnsNoDatabase() {
        RedisConnectionStub stub = new RedisConnectionStub("3", 0, true);

        List<Database> databases = new RedisMetaData().databases(stub.connection());

        assertEquals(List.of("3"), names(databases));
        assertEquals(List.of("config get databases", "select 0", "select 3"), stub.commands());
    }

    @Test
    void shouldReadDatabasesFromConfigWhenConfigIsAllowed() {
        RedisConnectionStub stub = new RedisConnectionStub("0", 16, false);

        List<Database> databases = new RedisMetaData().databases(stub.connection());

        assertEquals(16, databases.size());
        assertEquals("0", databases.get(0).getName());
        assertEquals("15", databases.get(15).getName());
        assertEquals(List.of("config get databases"), stub.commands());
    }

    private List<String> names(List<Database> databases) {
        return databases.stream().map(Database::getName).toList();
    }

    private static final class RedisConnectionStub {

        private final String catalog;
        private final int databaseCount;
        private final boolean forbidConfig;
        private final List<String> commands = new ArrayList<>();

        private RedisConnectionStub(String catalog, int databaseCount, boolean forbidConfig) {
            this.catalog = catalog;
            this.databaseCount = databaseCount;
            this.forbidConfig = forbidConfig;
        }

        Connection connection() {
            return proxy(Connection.class, (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "prepareStatement" -> statement((String) args[0]);
                    case "getCatalog" -> catalog;
                    case "isClosed" -> false;
                    case "close" -> null;
                    default -> defaultValue(method.getReturnType());
                };
            });
        }

        List<String> commands() {
            return commands;
        }

        private PreparedStatement statement(String sql) {
            final ResultSet[] resultSet = new ResultSet[1];
            return proxy(PreparedStatement.class, (proxy, method, args) -> {
                String methodName = method.getName();
                if ("execute".equals(methodName)) {
                    commands.add(sql);
                    if (forbidConfig && "config get databases".equals(sql)) {
                        throw new SQLException("NOPERM this user has no permissions to run the 'config|get' command");
                    }
                    if (sql.startsWith("select ")) {
                        int database = Integer.parseInt(sql.substring("select ".length()));
                        if (database >= databaseCount) {
                            throw new SQLException("ERR DB index is out of range");
                        }
                        return false;
                    }
                    resultSet[0] = configDatabasesResultSet(databaseCount);
                    return true;
                }
                if ("getResultSet".equals(methodName)) {
                    return resultSet[0];
                }
                if ("close".equals(methodName)) {
                    return null;
                }
                return defaultValue(method.getReturnType());
            });
        }

        private ResultSet configDatabasesResultSet(int count) {
            return proxy(ResultSet.class, new InvocationHandler() {
                private boolean beforeFirst = true;

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("next".equals(method.getName())) {
                        if (beforeFirst) {
                            beforeFirst = false;
                            return true;
                        }
                        return false;
                    }
                    if ("getObject".equals(method.getName())) {
                        return String.valueOf(count);
                    }
                    if ("close".equals(method.getName())) {
                        return null;
                    }
                    return defaultValue(method.getReturnType());
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == double.class) {
            return 0D;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }
}

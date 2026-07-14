package ai.chat2db.spi;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Consumes a JDBC {@link ResultSet} without returning a value.
 */
@FunctionalInterface
public interface IResultSetConsumer {

    /**
     * Processes the current result set provided by the command executor.
     *
     * @param resultSet JDBC result set to consume.
     * @throws SQLException when the consumer cannot read from the result set.
     * <p>
     * Typical usage:
     */
    void accept(ResultSet resultSet) throws SQLException;
}

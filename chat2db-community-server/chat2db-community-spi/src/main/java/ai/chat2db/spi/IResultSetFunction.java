package ai.chat2db.spi;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a JDBC {@link ResultSet} to a caller-defined value.
 *
 */
public interface IResultSetFunction<R> {

    /**
     * Applies the mapping logic to a JDBC result set.
     *
     * @param t JDBC result set to read.
     * @return mapped value produced from the result set.
     * @throws SQLException when the mapper cannot read from the result set.
     * <p>
     * Typical usage:
     */
    R apply(ResultSet t) throws SQLException;
}

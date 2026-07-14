package ai.chat2db.spi;


import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;

/**
 * Converts JDBC and SQL data values into dialect-specific display and DML formats.
 */
public interface IValueProcessor {

    /**
     * Converts a typed SQL data value into a literal that can be embedded in a DML statement.
     * <p>
     * Implementations should apply the quoting, escaping, and type wrappers required by their dialect.
     *
     * @param dataValue SQL value metadata and raw value to convert.
     * @return SQL literal text for the value, or the dialect-specific null literal when the value is null.
     */
    String getSqlValueString(SQLDataValue dataValue);


    /**
     * Converts a JDBC result value into text that is suitable for frontend display.
     * <p>
     * Implementations should format numbers, dates, strings, null values, and large values in a
     * user-readable form without adding SQL quoting that is only needed for DML.
     *
     * @param dataValue JDBC result-set metadata, result-set handle, and column index used to read the value.
     * @return display text for the value.
     */
    String getJdbcValue(JDBCDataValue dataValue);

    /**
     * Converts a JDBC result value into a SQL literal that can be reused in a DML statement.
     * <p>
     * This method is used when a value read from a result set must be copied into INSERT or UPDATE SQL.
     *
     * @param dataValue JDBC result-set metadata, result-set handle, and column index used to read the value.
     * @return SQL literal text that preserves the source JDBC value for DML.
     */
    String getJdbcSqlValueString(JDBCDataValue dataValue);

    /**
     * Returns whether the database type name should be treated as a string-like type.
     * <p>
     * SQL builders use this decision to choose string comparison and quoting behavior.
     *
     * @param dataType database type name, usually from JDBC metadata or dialect metadata.
     * @return {@code true} when the type stores textual data; otherwise {@code false}.
     */
    boolean isStringDataType(String dataType);
}

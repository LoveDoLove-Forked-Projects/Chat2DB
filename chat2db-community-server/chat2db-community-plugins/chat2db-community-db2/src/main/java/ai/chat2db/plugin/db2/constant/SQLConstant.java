package ai.chat2db.plugin.db2.constant;


public class SQLConstant {


    public static final String TABLES_SQL = """
            SELECT TABNAME
            FROM SYSCAT.TABLES
            WHERE TABSCHEMA = ?
              AND TYPE = 'T'
            ORDER BY TABNAME
            """;


    public static final String TABLE_DDL_FUNCTION_SQL
            = """
            CREATE OR REPLACE FUNCTION generate_table_ddl(schema_name VARCHAR(128), table_name VARCHAR(128))
                RETURNS CLOB
                LANGUAGE SQL
            BEGIN
                DECLARE ddl CLOB;

                -- Read table comments
                DECLARE table_remarks CLOB;
                SELECT REMARKS
                INTO table_remarks
                FROM SYSCAT.TABLES
                WHERE TABSCHEMA = schema_name
                  AND TABNAME = table_name;

                -- Build the table creation statement
                SET ddl = 'CREATE TABLE ' || table_name || ' (';

                -- Read column metadata and append it to the DDL statement
                FOR col_info AS
                    SELECT COLNAME, TYPENAME, LENGTH, SCALE, NULLS, DEFAULT as default, REMARKS
                    FROM SYSCAT.COLUMNS
                    WHERE TABSCHEMA = schema_name
                      AND TABNAME = table_name
                    ORDER BY COLNO
                    DO
                        SET ddl = ddl || col_info.COLNAME || ' ';
                        IF col_info.TYPENAME = 'INTEGER' THEN
                            SET ddl = ddl || col_info.TYPENAME;
                        ELSE
                            SET ddl = ddl || col_info.TYPENAME;
                            IF col_info.LENGTH IS NOT NULL THEN
                                SET ddl = ddl || '(' || col_info.LENGTH;
                                IF col_info.TYPENAME != 'VARCHAR' AND col_info.SCALE IS NOT NULL THEN
                                    SET ddl = ddl || ',' || col_info.SCALE;
                                END IF;
                                SET ddl = ddl || ')';
                            END IF;
                        END IF;
                        IF col_info.NULLS = 'N' THEN
                            SET ddl = ddl || ' NOT NULL';
                        END IF;
                        IF col_info.default IS NOT NULL THEN
                            SET ddl = ddl || ' DEFAULT ' || col_info.default;
                        END IF;
                        SET ddl = ddl || ','; -- Add the column-definition separator
                    END FOR;

                -- Remove the trailing comma
                SET ddl = LEFT(ddl, LENGTH(ddl) - 1);
                SET ddl = ddl || ');';

                -- Append the table comment
                IF table_remarks IS NOT NULL THEN
                    SET ddl = ddl || 'comment on table ' || table_name || ' is ''' || table_remarks || ''';';
                END IF;

                for column as
                    SELECT COLNAME, REMARKS
                    FROM SYSCAT.COLUMNS
                    WHERE TABSCHEMA = schema_name
                      AND TABNAME = table_name
                    ORDER BY COLNO
                    do
                        if column.REMARKS is not null then
                            set ddl = ddl || 'comment on column ' || table_name || '.' || column.COLNAME || ' is ''' ||
                                      column.REMARKS || ''';';
                        end if;
                    end for;

                -- Read index metadata and append it to the DDL statement
                FOR index_info AS
                    SELECT INDNAME, SUBSTR(COLNAMES, 2) AS COLNAMES, UNIQUERULE
                    FROM SYSCAT.INDEXES
                    WHERE TABSCHEMA = schema_name
                      AND TABNAME = table_name
                    DO
                        IF index_info.UNIQUERULE = 'P' THEN
                            SET ddl = ddl || ' ALTER TABLE ' || table_name || ' ADD PRIMARY KEY (' ||
                                      index_info.COLNAMES || ');';
                        ELSEIF index_info.UNIQUERULE = 'U' THEN
                            SET ddl = ddl || ' CREATE UNIQUE INDEX ' || index_info.INDNAME || ' ON ' ||
                                      table_name || ' (' || index_info.COLNAMES || ');';
                        END IF;
                    END FOR;

                RETURN ddl;
            END;""";
}

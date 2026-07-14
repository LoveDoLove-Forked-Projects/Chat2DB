package ai.chat2db.plugin.xugudb.constant;

import java.util.List;

public final class XUGUDBMetaDataConstants {

    public static final String FUNCTIONS_SQL = "select p.PROC_NAME From ALL_PROCEDURES p LEFT JOIN DBA_DATABASES DB ON p.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON p.SCHEMA_ID = CH.SCHEMA_ID where  DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and p.RET_TYPE is not null ";
    public static final String ROUTINES_SQL = "select DB.DB_NAME, CH.SCHEMA_NAME, p.PROC_NAME, p.DEFINE From ALL_PROCEDURES p LEFT JOIN DBA_DATABASES DB ON p.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON p.SCHEMA_ID = CH.SCHEMA_ID where  DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and p.PROC_NAME = '%s' and p.RET_TYPE is not null ";
    public static final String PROCEDURE_SQL = "select DB.DB_NAME, CH.SCHEMA_NAME, p.PROC_NAME, p.DEFINE From ALL_PROCEDURES p LEFT JOIN DBA_DATABASES DB ON p.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON p.SCHEMA_ID = CH.SCHEMA_ID where  DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and p.PROC_NAME = '%s' and p.RET_TYPE is null ";
    public static final String PROCEDURES_SQL = "select p.PROC_NAME From ALL_PROCEDURES p LEFT JOIN DBA_DATABASES DB ON p.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON p.SCHEMA_ID = CH.SCHEMA_ID where  DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and p.RET_TYPE is null ";
    public static final String TRIGGER_SQL = "select t.trig_name,t.DEFINE from all_triggers t LEFT JOIN DBA_DATABASES DB ON t.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON t.SCHEMA_ID = CH.SCHEMA_ID where  DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and t.trig_name = '%s'";
    public static final String TRIGGER_SQL_LIST = "select t.trig_name from all_triggers t LEFT JOIN DBA_DATABASES DB ON t.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON t.SCHEMA_ID = CH.SCHEMA_ID where  DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s'";
    public static final String VIEW_SQL_LIST = "SELECT DB.DB_NAME, CH.SCHEMA_NAME, v.VIEW_NAME, v.DEFINE, v.OPTION,v.VALID, v.IS_SYS, v.COMMENTS FROM all_views v LEFT JOIN DBA_DATABASES DB ON v.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON v.SCHEMA_ID = CH.SCHEMA_ID where DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s'";
    public static final String VIEW_SQL = " SELECT CH.SCHEMA_NAME, v.VIEW_NAME, v.DEFINE FROM ALL_VIEWS v LEFT JOIN DBA_DATABASES DB ON v.DB_ID = DB.DB_ID LEFT JOIN DBA_SCHEMAS CH ON v.SCHEMA_ID = CH.SCHEMA_ID where DB.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and v.VIEW_NAME = '%s'";
    public static final String INDEX_SQL = "SELECT i.INDEX_NAME, CASE i.INDEX_TYPE WHEN 0 THEN 'BTREE' WHEN 1 THEN 'RTREE' WHEN 2 THEN 'FULLTEXT' WHEN 3 THEN 'BITMAP' WHEN 4 THEN 'UNION' END AS INDEX_TYPE, i.IS_PRIMARY,"
            + " i.IS_UNIQUE, i.FIELD_NUM, REPLACE (KEYS, '\"', '') AS KEYS FROM ALL_INDEXES i LEFT JOIN ALL_TABLES T ON i.TABLE_ID = T.TABLE_ID LEFT JOIN ALL_SCHEMAS CH ON CH.USER_ID = T.USER_ID AND CH.DB_ID = i.DB_ID"
            + " where CH.SCHEMA_NAME = '%s' and T.TABLE_NAME = '%s'";
    public static final String SELECT_TABLE_COLUMNS = "select c.* from ALL_COLUMNS c LEFT JOIN ALL_TABLES T ON c.TABLE_ID = T.TABLE_ID LEFT JOIN ALL_SCHEMAS CH ON CH.USER_ID = T.USER_ID AND CH.DB_ID = c.DB_ID LEFT JOIN ALL_DATABASES db ON db.DB_ID = c.DB_ID where db.DB_NAME = '%s' and CH.SCHEMA_NAME = '%s' and T.TABLE_NAME = '%s' order by c.COL_NO";
    public static final List<String> SYSTEM_DATABASES = List.of("information_schema", "performance_schema", "sys");
    public static final List<String> SYSTEM_SCHEMAS = List.of("CTISYS", "SYS", "SYSDBA", "SYSSSO", "SYSAUDITOR");

    private XUGUDBMetaDataConstants() {
    }
}

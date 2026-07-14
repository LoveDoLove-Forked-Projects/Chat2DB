package ai.chat2db.plugin.oscar.enums.type;

import java.util.Arrays;
import java.util.List;

public enum OscarSystemSchemaEnum {

    SYS,
    SYSDBA,
    SYSTEM,
    SYSAUDITOR,
    SYSSSO,
    SYSSMAC,
    CTXSYS,
    PUBLIC,
    INFORMATION_SCHEMA,
    PG_CATALOG;

    public String getSchemaName() {
        return name();
    }

    public static List<String> schemaNames() {
        return Arrays.stream(values())
                .map(OscarSystemSchemaEnum::getSchemaName)
                .toList();
    }
}

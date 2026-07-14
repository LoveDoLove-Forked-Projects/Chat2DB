package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.model.RoutineParameter;
import ai.chat2db.community.domain.api.model.metadata.FunctionParameter;
import ai.chat2db.community.domain.api.model.metadata.ProcedureParameter;
import org.apache.commons.lang3.StringUtils;

import java.sql.DatabaseMetaData;
import java.util.Locale;
import java.util.Objects;


public final class MysqlRoutineConverterConstants {

    public static final String IN = "IN";
    public static final String OUT = "OUT";
    public static final String INOUT = "INOUT";

    private MysqlRoutineConverterConstants() {
    }
}

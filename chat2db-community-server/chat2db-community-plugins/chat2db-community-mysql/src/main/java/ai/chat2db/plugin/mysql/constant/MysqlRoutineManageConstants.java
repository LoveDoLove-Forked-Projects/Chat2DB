package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.plugin.mysql.converter.MysqlRoutineConverter;
import ai.chat2db.plugin.mysql.model.RoutineParameter;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.spi.IRoutineManager;
import ai.chat2db.community.domain.api.enums.plugin.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.metadata.RoutineOperation;
import ai.chat2db.community.domain.api.model.sql.SqlPreview;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.model.request.SqlStatementExecuteRequest;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;



public final class MysqlRoutineManageConstants {

    public static final String FUNCTION = "FUNCTION";
    public static final String PROCEDURE = "PROCEDURE";

    private MysqlRoutineManageConstants() {
    }
}

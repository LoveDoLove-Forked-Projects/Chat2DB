package ai.chat2db.plugin.redshift.constant;

import ai.chat2db.plugin.postgresql.PostgreSQLMetaData;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.community.domain.api.model.metadata.Function;
import ai.chat2db.community.domain.api.model.metadata.Procedure;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.model.metadata.Trigger;
import ai.chat2db.spi.DefaultSQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;



public final class RedshiftMetaDataConstants {

    public static final String FUNCTION_DEFINITION = """
            SELECT pg_get_functiondef(p.oid) as definition
            FROM pg_proc p
            JOIN pg_namespace n ON p.pronamespace = n.oid
            WHERE proname = ?
              AND n.nspname = ?
              """;

    private RedshiftMetaDataConstants() {
    }
}

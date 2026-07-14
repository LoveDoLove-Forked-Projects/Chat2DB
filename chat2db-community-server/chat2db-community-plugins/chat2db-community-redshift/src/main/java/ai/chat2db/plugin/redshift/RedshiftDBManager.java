package ai.chat2db.plugin.redshift;

import ai.chat2db.plugin.postgresql.PostgreSQLDBManager;
import ai.chat2db.spi.IDbManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedshiftDBManager extends PostgreSQLDBManager implements IDbManager {
}

package ai.chat2db.community.domain.api.service.db;

import java.sql.SQLException;

public interface IDbMybatisGenerateService {

    /**
     * Generates MyBatis classes for a table.
     *
     * @param tableName table name to generate from.
     * @param schemaName schema name that scopes the table.
     * @param exportPath target directory for generated classes.
     * @throws SQLException when table metadata cannot be read.
     */
    void generateClass(String tableName, String schemaName, String exportPath) throws SQLException;
}

package ai.chat2db.plugin.h2.builder;

import ai.chat2db.spi.constant.SQLConstants;

import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import org.apache.commons.lang3.StringUtils;

import static ai.chat2db.plugin.h2.constant.H2SqlBuilderConstants.*;
public class H2SqlBuilder extends DefaultSqlBuilder  {




    @Override
    public String buildCreateSchema(Schema schema) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(SQL_CREATE_SCHEMA + schema.getName() + SQLConstants.DOUBLE_QUOTE_SEMICOLON);

        if (StringUtils.isNotBlank(schema.getComment())) {
            sqlBuilder.append(SQL_COMMENT_ON_SCHEMA_DOUBLE_QUOTE).append(schema.getName()).append(VALUE_DOUBLE_QUOTE_IS_SINGLE_QUOTE).append(schema.getComment()).append(SQLConstants.SINGLE_QUOTE_SEMICOLON);
        }

        return sqlBuilder.toString();
    }

}

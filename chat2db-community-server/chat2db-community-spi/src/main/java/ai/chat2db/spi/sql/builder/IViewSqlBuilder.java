package ai.chat2db.spi.sql.builder;

import ai.chat2db.community.domain.api.model.view.ModifyView;

public interface IViewSqlBuilder {

    String buildCreateView(ModifyView view);

    String buildAlterView(ModifyView view);

    String buildDropView(String databaseName, String schemaName, String viewName);

    String buildShowCreateView(String databaseName, String schemaName, String viewName);
}

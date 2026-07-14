package ai.chat2db.plugin.sqlserver.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertRowTokenRange;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParser;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParserBaseVisitor;
import ai.chat2db.plugin.sqlserver.parser.util.SqlServerStringUtil;
import ai.chat2db.spi.util.InsertValueTokenUtil;
import ai.chat2db.spi.util.TokenUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
public class SqlServerParserVisitor extends TSqlParserBaseVisitor<Void> {

    private final StatementContext context;

    public SqlServerParserVisitor(StatementContext context) {
        this.context = context;
    }

    @Override
    public Void visitTsql_file(TSqlParser.Tsql_fileContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TSqlParser.BatchContext batchContext) {
                visit(batchContext);
            } else if (child instanceof TSqlParser.Execute_body_batchContext executeBodyBatchContext) {
                visit(executeBodyBatchContext);
            }
        }
        return null;
    }

    @Override
    public Void visitBatch(TSqlParser.BatchContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TSqlParser.Execute_body_batchContext executeBodyBatchContext) {
                visit(executeBodyBatchContext);
            } else {
                String sql = context.getText(child.getSourceInterval());
                if (StringUtils.equalsAnyIgnoreCase(sql, "GO", ";")) {
                    continue;
                }
                Statement statement = new Statement();
                if (sql.endsWith(";")) {
                    statement.setOriginalSql(sql);
                    sql = sql.substring(0, sql.length() - 1);
                } else {
                    statement.setOriginalSql(sql);
                }
                statement.setSql(sql);
                statement.setFirstToken(((ParserRuleContext) child).getStart());
                statement.setLastToken(((ParserRuleContext) child).getStop());
                statement.setStatementType(StatementValidTypeEnum.VALID.name());
                context.setCurrentStatement(statement);
                context.addStatement(statement);
                visit(child);
            }
        }
        return null;
    }

    @Override
    public Void visitExecute_body_batch(TSqlParser.Execute_body_batchContext ctx) {
        String sql = context.getText(ctx.getSourceInterval());
        if (StringUtils.equalsAnyIgnoreCase(sql, "GO", ";")) {
            return null;
        }
        Statement statement = new Statement();
        if (sql.endsWith(";")) {
            statement.setOriginalSql(sql);
            sql = sql.substring(0, sql.length() - 1);
        } else {
            statement.setOriginalSql(sql);
        }
        statement.setSql(sql);
        statement.setFirstToken(ctx.getStart());
        statement.setLastToken(ctx.getStop());
        statement.setType(SqlTypeEnum.EXECUTE_BODY_BATCH.name());
        statement.setStatementType(StatementValidTypeEnum.VALID.name());
        context.addStatement(statement);
        return null;
    }


    @Override
    public Void visitDeclare_statement(TSqlParser.Declare_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DECLARE.name());
        return null;
    }

    @Override
    public Void visitSet_statement(TSqlParser.Set_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET.name());
        return null;
    }

    @Override
    public Void visitAlter_application_role(TSqlParser.Alter_application_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_xml_schema_collection(TSqlParser.Alter_xml_schema_collectionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly(TSqlParser.Alter_assemblyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_start(TSqlParser.Alter_assembly_startContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_clause(TSqlParser.Alter_assembly_clauseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_from_clause(TSqlParser.Alter_assembly_from_clauseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_from_clause_start(TSqlParser.Alter_assembly_from_clause_startContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_drop_clause(TSqlParser.Alter_assembly_drop_clauseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_drop_multiple_files(TSqlParser.Alter_assembly_drop_multiple_filesContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_drop(TSqlParser.Alter_assembly_dropContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_add_clause(TSqlParser.Alter_assembly_add_clauseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_asssembly_add_clause_start(TSqlParser.Alter_asssembly_add_clause_startContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_client_file_clause(TSqlParser.Alter_assembly_client_file_clauseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_file_name(TSqlParser.Alter_assembly_file_nameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_file_bits(TSqlParser.Alter_assembly_file_bitsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_as(TSqlParser.Alter_assembly_asContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_with_clause(TSqlParser.Alter_assembly_with_clauseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_assembly_with(TSqlParser.Alter_assembly_withContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_asymmetric_key(TSqlParser.Alter_asymmetric_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_asymmetric_key_start(TSqlParser.Alter_asymmetric_key_startContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_authorization(TSqlParser.Alter_authorizationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_authorization_start(TSqlParser.Alter_authorization_startContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_authorization_for_sql_database(TSqlParser.Alter_authorization_for_sql_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_authorization_for_azure_dw(TSqlParser.Alter_authorization_for_azure_dwContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_authorization_for_parallel_dw(TSqlParser.Alter_authorization_for_parallel_dwContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_availability_group(TSqlParser.Alter_availability_groupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_availability_group_start(TSqlParser.Alter_availability_group_startContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_availability_group_options(TSqlParser.Alter_availability_group_optionsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_certificate(TSqlParser.Alter_certificateContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_column_encryption_key(TSqlParser.Alter_column_encryption_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_credential(TSqlParser.Alter_credentialContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_cryptographic_provider(TSqlParser.Alter_cryptographic_providerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_external_data_source(TSqlParser.Alter_external_data_sourceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_external_library(TSqlParser.Alter_external_libraryContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_external_resource_pool(TSqlParser.Alter_external_resource_poolContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_fulltext_catalog(TSqlParser.Alter_fulltext_catalogContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_fulltext_stoplist(TSqlParser.Alter_fulltext_stoplistContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_login_sql_server(TSqlParser.Alter_login_sql_serverContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_login_azure_sql(TSqlParser.Alter_login_azure_sqlContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_login_azure_sql_dw_and_pdw(TSqlParser.Alter_login_azure_sql_dw_and_pdwContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_master_key_sql_server(TSqlParser.Alter_master_key_sql_serverContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_master_key_azure_sql(TSqlParser.Alter_master_key_azure_sqlContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_message_type(TSqlParser.Alter_message_typeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_partition_function(TSqlParser.Alter_partition_functionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_partition_scheme(TSqlParser.Alter_partition_schemeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_remote_service_binding(TSqlParser.Alter_remote_service_bindingContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_resource_governor(TSqlParser.Alter_resource_governorContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_database_audit_specification(TSqlParser.Alter_database_audit_specificationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_db_role(TSqlParser.Alter_db_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_schema_sql(TSqlParser.Alter_schema_sqlContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_schema_azure_sql_dw_and_pdw(TSqlParser.Alter_schema_azure_sql_dw_and_pdwContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_sequence(TSqlParser.Alter_sequenceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_server_audit(TSqlParser.Alter_server_auditContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_server_audit_specification(TSqlParser.Alter_server_audit_specificationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_server_configuration(TSqlParser.Alter_server_configurationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_server_role(TSqlParser.Alter_server_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_server_role_pdw(TSqlParser.Alter_server_role_pdwContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_service(TSqlParser.Alter_serviceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_service_master_key(TSqlParser.Alter_service_master_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_symmetric_key(TSqlParser.Alter_symmetric_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_user(TSqlParser.Alter_userContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_user_azure_sql(TSqlParser.Alter_user_azure_sqlContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_workload_group(TSqlParser.Alter_workload_groupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_queue(TSqlParser.Alter_queueContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_index(TSqlParser.Alter_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_table(TSqlParser.Alter_tableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_TABLE.name());
        List<TSqlParser.Table_nameContext> tableNameContexts = ctx.table_name();
        if (CollectionUtils.isEmpty(tableNameContexts)) {
            return null;
        }
        for (TSqlParser.Table_nameContext tableNameContext : tableNameContexts) {
            visitTableName(tableNameContext);
        }
        return null;
    }

    @Override
    public Void visitAlter_database(TSqlParser.Alter_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_endpoint(TSqlParser.Alter_endpointContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_table_index_options(TSqlParser.Alter_table_index_optionsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }

    @Override
    public Void visitAlter_table_index_option(TSqlParser.Alter_table_index_optionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER.name());
        return null;
    }


    @Override
    public Void visitDrop_aggregate(TSqlParser.Drop_aggregateContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_application_role(TSqlParser.Drop_application_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_assembly(TSqlParser.Drop_assemblyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_asymmetric_key(TSqlParser.Drop_asymmetric_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_availability_group(TSqlParser.Drop_availability_groupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_certificate(TSqlParser.Drop_certificateContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_column_encryption_key(TSqlParser.Drop_column_encryption_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_column_master_key(TSqlParser.Drop_column_master_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_contract(TSqlParser.Drop_contractContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_credential(TSqlParser.Drop_credentialContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_cryptograhic_provider(TSqlParser.Drop_cryptograhic_providerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_database_audit_specification(TSqlParser.Drop_database_audit_specificationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_database_encryption_key(TSqlParser.Drop_database_encryption_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_database_scoped_credential(TSqlParser.Drop_database_scoped_credentialContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_default(TSqlParser.Drop_defaultContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_endpoint(TSqlParser.Drop_endpointContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_external_data_source(TSqlParser.Drop_external_data_sourceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_external_file_format(TSqlParser.Drop_external_file_formatContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_external_library(TSqlParser.Drop_external_libraryContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_external_resource_pool(TSqlParser.Drop_external_resource_poolContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_external_table(TSqlParser.Drop_external_tableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_event_notifications(TSqlParser.Drop_event_notificationsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_event_session(TSqlParser.Drop_event_sessionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_fulltext_catalog(TSqlParser.Drop_fulltext_catalogContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_fulltext_index(TSqlParser.Drop_fulltext_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_fulltext_stoplist(TSqlParser.Drop_fulltext_stoplistContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_master_key(TSqlParser.Drop_master_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_message_type(TSqlParser.Drop_message_typeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_partition_function(TSqlParser.Drop_partition_functionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_partition_scheme(TSqlParser.Drop_partition_schemeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_queue(TSqlParser.Drop_queueContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_remote_service_binding(TSqlParser.Drop_remote_service_bindingContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_resource_pool(TSqlParser.Drop_resource_poolContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_route(TSqlParser.Drop_routeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_rule(TSqlParser.Drop_ruleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_search_property_list(TSqlParser.Drop_search_property_listContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_security_policy(TSqlParser.Drop_security_policyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_sequence(TSqlParser.Drop_sequenceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_server_audit(TSqlParser.Drop_server_auditContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_server_audit_specification(TSqlParser.Drop_server_audit_specificationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_server_role(TSqlParser.Drop_server_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_service(TSqlParser.Drop_serviceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_signature(TSqlParser.Drop_signatureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_statistics_name_azure_dw_and_pdw(TSqlParser.Drop_statistics_name_azure_dw_and_pdwContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_symmetric_key(TSqlParser.Drop_symmetric_keyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_synonym(TSqlParser.Drop_synonymContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_workload_group(TSqlParser.Drop_workload_groupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_xml_schema_collection(TSqlParser.Drop_xml_schema_collectionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_broker_priority(TSqlParser.Drop_broker_priorityContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_relational_or_xml_or_spatial_index(TSqlParser.Drop_relational_or_xml_or_spatial_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_backward_compatible_index(TSqlParser.Drop_backward_compatible_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_dml_trigger(TSqlParser.Drop_dml_triggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_ddl_trigger(TSqlParser.Drop_ddl_triggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_statistics(TSqlParser.Drop_statisticsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitDrop_type(TSqlParser.Drop_typeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP.name());
        return null;
    }

    @Override
    public Void visitCreate_or_alter_procedure(TSqlParser.Create_or_alter_procedureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_PROCEDURE.name());
        TSqlParser.Func_proc_name_schemaContext funcProcNameSchemaContext = ctx.func_proc_name_schema();
        if (Objects.nonNull(funcProcNameSchemaContext)) {
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext, IdentifierTypeEnum.PROCEDURE);
        }
        return super.visitCreate_or_alter_procedure(ctx);
    }


    @Override
    public Void visitCreate_or_alter_function(TSqlParser.Create_or_alter_functionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FUNCTION.name());
        TSqlParser.Func_proc_name_schemaContext funcProcNameSchemaContext = ctx.func_proc_name_schema();
        if (Objects.nonNull(funcProcNameSchemaContext)) {
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext, IdentifierTypeEnum.FUNCTION);
        }
        return super.visitCreate_or_alter_function(ctx);
    }

    private void visitSchemaFunctionOrProcedureName(TSqlParser.Func_proc_name_schemaContext ctx, IdentifierTypeEnum identifierType) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() == stop.getTokenIndex()) {
            currentStatement.addIdentifier(start.getText(), identifierType.name(), start);
        } else {

            currentStatement.addIdentifier(start.getText(), IdentifierTypeEnum.SCHEMA.name(), start);

            currentStatement.addIdentifier(stop.getText(), identifierType.name(), stop);
        }
    }

    @Override
    public Void visitCreate_or_alter_trigger(TSqlParser.Create_or_alter_triggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TRIGGER.name());
        TSqlParser.Create_or_alter_dml_triggerContext orAlterDmlTrigger = ctx.create_or_alter_dml_trigger();
        TSqlParser.Create_or_alter_ddl_triggerContext orAlterDdlTrigger = ctx.create_or_alter_ddl_trigger();
        if (Objects.nonNull(orAlterDmlTrigger)) {
            TSqlParser.Simple_nameContext simpleNameContext = orAlterDmlTrigger.simple_name();
            if (Objects.nonNull(simpleNameContext)) {
                visitSimpleName(simpleNameContext, IdentifierTypeEnum.TRIGGER);
            }
        } else if (Objects.nonNull(orAlterDdlTrigger)) {
            TSqlParser.Simple_nameContext simpleNameContext = orAlterDdlTrigger.simple_name();
            if (Objects.nonNull(simpleNameContext)) {
                visitSimpleName(simpleNameContext, IdentifierTypeEnum.TRIGGER);
            }
        }
        return super.visitCreate_or_alter_trigger(ctx);
    }


    @Override
    public Void visitCreate_view(TSqlParser.Create_viewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_VIEW.name());
        TSqlParser.Simple_nameContext simpleNameContext = ctx.simple_name();
        if (Objects.nonNull(simpleNameContext)) {
            visitSimpleName(simpleNameContext, IdentifierTypeEnum.VIEW);
        }
        return super.visitCreate_view(ctx);
    }

    private void visitSimpleName(TSqlParser.Simple_nameContext ctx, IdentifierTypeEnum identifierType) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() == stop.getTokenIndex()) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(start.getText()), identifierType.name(), start);
        } else {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(start.getText()), IdentifierTypeEnum.SCHEMA.name(), start);
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(stop.getText()), identifierType.name(), stop);
        }
    }


    @Override
    public Void visitCreate_db_role(TSqlParser.Create_db_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ROLE.name());
        TSqlParser.Id_Context idContext = ctx.id_(0);
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.ROLE.name(), idContext.getStart());
        }
        return super.visitCreate_db_role(ctx);
    }


    @Override
    public Void visitCreate_login_azure_sql(TSqlParser.Create_login_azure_sqlContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        TSqlParser.Id_Context idContext = ctx.id_();
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.USER.name(), idContext.getStart());
        }
        return super.visitCreate_login_azure_sql(ctx);
    }


    @Override
    public Void visitCreate_user(TSqlParser.Create_userContext ctx) {
        TSqlParser.Id_Context idContext = ctx.id_(0);
        if (Objects.nonNull(idContext)) {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.USER.name(), idContext.getStart());
        }
        return super.visitCreate_user(ctx);
    }

    @Override
    public Void visitCreate_schema(TSqlParser.Create_schemaContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_SCHEMA.name());
        TSqlParser.Id_Context idContext = ctx.id_(0);
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.SCHEMA.name(), idContext.getStart());
        }
        return super.visitCreate_schema(ctx);
    }


    @Override
    public Void visitCreate_database(TSqlParser.Create_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_DATABASE.name());
        TSqlParser.Id_Context idContext = ctx.id_(0);
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.DATABASE.name(), idContext.getStart());
        }
        return super.visitCreate_database(ctx);
    }

    @Override
    public Void visitCreate_index(TSqlParser.Create_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_INDEX.name());
        TSqlParser.Id_Context idContext = ctx.id_(0);
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.INDEX.name(), idContext.getStart());
        }
        TSqlParser.Table_nameContext tableNameContext = ctx.table_name();
        if (Objects.nonNull(tableNameContext)) {
            visitTableName(tableNameContext);
        }
        return super.visitCreate_index(ctx);
    }

    private void visitTableName(TSqlParser.Table_nameContext ctx) {
        Try.run(() -> {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            List<TSqlParser.Id_Context> idContexts = ctx.id_();
            int size = idContexts.size();
            Token databaseToken = null, schemaToken = null, tableToken;
            String databaseName = null, schemaName = null, tableName;
            if (size == 3) {
                databaseToken = idContexts.get(0).getStart();
                schemaToken = idContexts.get(1).getStart();
            } else if (size == 2) {
                schemaToken = idContexts.get(0).getStart();
            }
            tableToken = idContexts.get(size - 1).getStart();
            if (Objects.nonNull(databaseToken)) {
                databaseName = SqlServerStringUtil.removeQuote(databaseToken.getText());

                currentStatement.addIdentifier(databaseName, IdentifierTypeEnum.DATABASE.name(), databaseToken);
            }

            if (Objects.nonNull(schemaToken)) {
                schemaName = SqlServerStringUtil.removeQuote(schemaToken.getText());
                Identifier identifier = new Identifier();
                identifier.setIdentifierDatabase(databaseName);
                identifier.setIdentifierName(schemaName);
                identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                identifier.setFirstToken(schemaToken);
                currentStatement.addIdentifier(identifier);
            }

            tableName = SqlServerStringUtil.removeQuote(tableToken.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierDatabase(databaseName);
            identifier.setIdentifierSchema(schemaName);
            identifier.setIdentifierName(tableName);
            identifier.setIdentifierType(IdentifierTypeEnum.TABLE.name());
            identifier.setFirstToken(tableToken);
            currentStatement.addIdentifier(identifier);
        }).onFailure(e -> {
            log.error(" sqlserver visitTableName error ", e);
        });

    }

    @Override
    public Void visitCreate_table(TSqlParser.Create_tableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        TSqlParser.Table_nameContext tableNameContext = ctx.table_name();
        if (Objects.nonNull(tableNameContext)) {
            visitTableName(tableNameContext);
        }
        return super.visitCreate_table(ctx);
    }

    @Override
    public Void visitDrop_database(TSqlParser.Drop_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_DATABASE.name());
        TSqlParser.Id_Context idContext = ctx.id_(0);
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.DATABASE.name(), idContext.getStart());
        }
        return super.visitDrop_database(ctx);
    }

    @Override
    public Void visitDrop_login(TSqlParser.Drop_loginContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        TSqlParser.Id_Context idContext = ctx.id_();
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.USER.name(), idContext.getStart());
        }
        return super.visitDrop_login(ctx);
    }


    @Override
    public Void visitDrop_db_role(TSqlParser.Drop_db_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_ROLE.name());
        TSqlParser.Id_Context idContext = ctx.id_();
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.ROLE.name(), idContext.getStart());
        }
        return super.visitDrop_db_role(ctx);
    }

    @Override
    public Void visitDrop_schema(TSqlParser.Drop_schemaContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_SCHEMA.name());
        TSqlParser.Id_Context idContext = ctx.id_();
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.SCHEMA.name(), idContext.getStart());
        }
        return super.visitDrop_schema(ctx);
    }


    @Override
    public Void visitDrop_user(TSqlParser.Drop_userContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        TSqlParser.Id_Context idContext = ctx.id_();
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.USER.name(), idContext.getStart());
        }
        return super.visitDrop_user(ctx);
    }


    @Override
    public Void visitDrop_index(TSqlParser.Drop_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_INDEX.name());
        TSqlParser.Drop_relational_or_xml_or_spatial_indexContext dropRelationalOrXmlOrSpatialIndexContext = ctx.drop_relational_or_xml_or_spatial_index(0);
        if (Objects.nonNull(dropRelationalOrXmlOrSpatialIndexContext)) {
            TSqlParser.Id_Context idContext = dropRelationalOrXmlOrSpatialIndexContext.id_();
            if (Objects.nonNull(idContext)) {
                currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.INDEX.name(), idContext.getStart());
            }
        }
        return super.visitDrop_index(ctx);
    }


    @Override
    public Void visitDrop_procedure(TSqlParser.Drop_procedureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_PROCEDURE.name());
        TSqlParser.Func_proc_name_schemaContext funcProcNameSchemaContext = ctx.func_proc_name_schema(0);
        if (Objects.nonNull(funcProcNameSchemaContext)) {
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext, IdentifierTypeEnum.PROCEDURE);
        }
        return super.visitDrop_procedure(ctx);
    }

    @Override
    public Void visitDrop_trigger(TSqlParser.Drop_triggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TRIGGER.name());
        TSqlParser.Drop_ddl_triggerContext dropDdlTriggerContext = ctx.drop_ddl_trigger();
        TSqlParser.Drop_dml_triggerContext dropDmlTriggerContext = ctx.drop_dml_trigger();
        if (Objects.nonNull(dropDdlTriggerContext)) {
            TSqlParser.Simple_nameContext simpleNameContext = dropDdlTriggerContext.simple_name(0);
            if (Objects.nonNull(simpleNameContext)) {
                visitSimpleName(simpleNameContext, IdentifierTypeEnum.TRIGGER);
            }
        } else if (Objects.nonNull(dropDmlTriggerContext)) {
            TSqlParser.Simple_nameContext simpleNameContext = dropDmlTriggerContext.simple_name(0);
            if (Objects.nonNull(simpleNameContext)) {
                visitSimpleName(simpleNameContext, IdentifierTypeEnum.TRIGGER);
            }
        }
        return super.visitDrop_trigger(ctx);
    }


    @Override
    public Void visitDrop_function(TSqlParser.Drop_functionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());
        TSqlParser.Func_proc_name_schemaContext funcProcNameSchemaContext = ctx.func_proc_name_schema(0);
        if (Objects.nonNull(funcProcNameSchemaContext)) {
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext, IdentifierTypeEnum.FUNCTION);
        }
        return super.visitDrop_function(ctx);
    }


    @Override
    public Void visitDrop_table(TSqlParser.Drop_tableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TABLE.name());
        TSqlParser.Table_nameContext tableNameContext = ctx.table_name(0);
        if (Objects.nonNull(tableNameContext)) {
            visitTableName(tableNameContext);
        }
        return super.visitDrop_table(ctx);
    }

    @Override
    public Void visitDrop_view(TSqlParser.Drop_viewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_VIEW.name());
        TSqlParser.Simple_nameContext simpleNameContext = ctx.simple_name(0);
        if (Objects.nonNull(simpleNameContext)) {
            visitSimpleName(simpleNameContext, IdentifierTypeEnum.VIEW);
        }
        return super.visitDrop_view(ctx);
    }


    @Override
    public Void visitUse_statement(TSqlParser.Use_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.USE_DATABASE.name());
        TSqlParser.Id_Context idContext = ctx.id_();
        if (Objects.nonNull(idContext)) {
            currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(idContext.getText()), IdentifierTypeEnum.DATABASE.name(), idContext.getStart());
        }
        return super.visitUse_statement(ctx);
    }

    @Override
    public Void visitInsert_statement(TSqlParser.Insert_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        TSqlParser.Ddl_objectContext ddlObjectContext = ctx.ddl_object();
        if (Objects.nonNull(ddlObjectContext)) {
            TSqlParser.Full_table_nameContext fullTableNameContext = ddlObjectContext.full_table_name();
            if (Objects.nonNull(fullTableNameContext)) {
                visitFullTableName(fullTableNameContext);
            }
        }
        visitInsertValueMappings(context, ctx);
        return super.visitInsert_statement(ctx);
    }

    static void visitInsertValueMappings(StatementContext context, TSqlParser.Insert_statementContext ctx) {
        Try.run(() -> {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement) || Objects.isNull(ctx)) {
                return;
            }
            TSqlParser.Insert_column_name_listContext columnListContext = ctx.insert_column_name_list();
            TSqlParser.Insert_statement_valueContext valueContext = ctx.insert_statement_value();
            if (Objects.isNull(columnListContext) || Objects.isNull(valueContext)) {
                return;
            }
            TSqlParser.Table_value_constructorContext tableValueConstructorContext = valueContext.table_value_constructor();
            List<TSqlParser.Insert_column_idContext> columnContexts = columnListContext.insert_column_id();
            List<TSqlParser.Expression_list_Context> rowContexts = Objects.nonNull(tableValueConstructorContext)
                    ? tableValueConstructorContext.expression_list_() : List.of();
            List<InsertRowTokenRange> rowTokenRanges = getInsertRowTokenRanges(context, ctx, tableValueConstructorContext);
            if (CollectionUtils.isEmpty(columnContexts) || rowTokenRanges.isEmpty()) {
                return;
            }
            int expressionRowIndex = 0;

            for (int rowIndex = 0; rowIndex < rowTokenRanges.size(); rowIndex++) {
                Token rowFirstToken = rowTokenRanges.get(rowIndex).getFirstToken();
                Token rowLastToken = rowTokenRanges.get(rowIndex).getLastToken();
                List<TSqlParser.ExpressionContext> valueContexts = List.of();
                if (rowContexts.size() > expressionRowIndex) {
                    TSqlParser.Expression_list_Context rowContext = rowContexts.get(expressionRowIndex);
                    if (Objects.nonNull(rowContext) && rowContext.getStart().getTokenIndex() > rowFirstToken.getTokenIndex()
                            && rowContext.getStop().getTokenIndex() < rowLastToken.getTokenIndex()) {
                        valueContexts = rowContext.expression();
                        expressionRowIndex++;
                    }
                }
                int columnSize = Math.min(columnContexts.size(), valueContexts.size());
                for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                    TSqlParser.Insert_column_idContext columnContext = columnContexts.get(columnIndex);
                    TSqlParser.ExpressionContext expressionContext = valueContexts.get(columnIndex);
                    if (Objects.isNull(columnContext) || Objects.isNull(expressionContext)) {
                        continue;
                    }
                    currentStatement.addInsertValueMapping(columnContext.getStart(), columnContext.getStop(),
                            expressionContext.getStart(), expressionContext.getStop(), rowFirstToken, rowLastToken,
                            rowIndex, columnIndex);
                }
                for (int columnIndex = columnSize; columnIndex < columnContexts.size(); columnIndex++) {
                    TSqlParser.Insert_column_idContext columnContext = columnContexts.get(columnIndex);
                    if (Objects.isNull(columnContext)) {
                        continue;
                    }
                    currentStatement.addUnmappedInsertColumn(columnContext.getStart(), columnContext.getStop(),
                            rowFirstToken, rowLastToken, rowIndex, columnIndex);
                }
                for (int valueIndex = columnSize; valueIndex < valueContexts.size(); valueIndex++) {
                    TSqlParser.ExpressionContext expressionContext = valueContexts.get(valueIndex);
                    if (Objects.isNull(expressionContext)) {
                        continue;
                    }
                    currentStatement.addUnmappedInsertValue(expressionContext.getStart(), expressionContext.getStop(),
                            rowFirstToken, rowLastToken, rowIndex, valueIndex);
                }
            }
        }).onFailure(e -> log.error("sqlserver visitInsertValueMappings error", e));
    }

    private static List<InsertRowTokenRange> getInsertRowTokenRanges(StatementContext context,
                                                                     TSqlParser.Insert_statementContext ctx,
                                                                     TSqlParser.Table_value_constructorContext tableValueConstructorContext) {
        List<InsertRowTokenRange> rowTokenRanges = List.of();
        if (Objects.nonNull(tableValueConstructorContext)) {
            rowTokenRanges = InsertValueTokenUtil.buildRowTokenRanges(
                    tableValueConstructorContext.LR_BRACKET(), tableValueConstructorContext.RR_BRACKET());
        }
        if (!rowTokenRanges.isEmpty()) {
            return rowTokenRanges;
        }
        return InsertValueTokenUtil.searchValuesRowTokenRanges(context, ctx.getStart(), ctx.getStop());
    }

    private void visitFullTableName(TSqlParser.Full_table_nameContext ctx) {
        Try.run(() -> {
            String text = ctx.getText();
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            if (StringUtils.isNotBlank(text)) {
                String[] split = text.split("\\.");
                if (split.length == 3) {
                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[0]),
                            IdentifierTypeEnum.DATABASE.name(), ctx.getStart());

                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[1]),
                            IdentifierTypeEnum.SCHEMA.name(),
                            context.getCommonTokenStream().get(ctx.getStart().getTokenIndex() + 1));
                } else if (split.length == 2) {

                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[0]),
                            IdentifierTypeEnum.SCHEMA.name(), ctx.getStart());
                }

                currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[split.length - 1]),
                        IdentifierTypeEnum.TABLE.name(), ctx.getStop());
            }
        }).onFailure(e -> {
            log.error("visit FullName error", e);
        });

    }


    @Override
    public Void visitSelect_statement_standalone(TSqlParser.Select_statement_standaloneContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        return super.visitSelect_statement_standalone(ctx);
    }

    @Override
    public Void visitTable_source_item(TSqlParser.Table_source_itemContext ctx) {
        TSqlParser.Full_table_nameContext fullTableNameContext = ctx.full_table_name();
        TSqlParser.As_table_aliasContext tableAlias = ctx.as_table_alias();
        if (Objects.isNull(fullTableNameContext)) {
            return null;
        }
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        Try.run(() -> {
            String text = fullTableNameContext.getText();
            if (StringUtils.isNotBlank(text)) {
                String[] split = text.split("\\.");
                if (split.length == 3) {

                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[0]),
                            IdentifierTypeEnum.DATABASE.name(), ctx.getStart());
                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[1]),
                            IdentifierTypeEnum.SCHEMA.name(),
                            context.getCommonTokenStream().get(ctx.getStart().getTokenIndex() + 1));
                } else if (split.length == 2) {

                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[0]),
                            IdentifierTypeEnum.SCHEMA.name(), ctx.getStart());
                }
                if (Objects.isNull(tableAlias)) {

                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[split.length - 1]),
                            IdentifierTypeEnum.TABLE.name(), ctx.getStop());
                } else {

                    currentStatement.addIdentifier(SqlServerStringUtil.removeQuote(split[split.length - 1]),
                            IdentifierTypeEnum.TABLE.name(), tableAlias.getText(), fullTableNameContext.getStop(), tableAlias.getStart());
                }
            }
        }).onFailure(e -> log.error("visit table_source_item error", e));

        return super.visitTable_source_item(ctx);
    }

    @Override
    public Void visitUpdate_statement(TSqlParser.Update_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UPDATE.name());
        TSqlParser.Ddl_objectContext ddlObjectContext = ctx.ddl_object();
        if (Objects.nonNull(ddlObjectContext)) {
            TSqlParser.Full_table_nameContext fullTableNameContext = ddlObjectContext.full_table_name();
            if (Objects.nonNull(fullTableNameContext)) {
                visitFullTableName(fullTableNameContext);
            }
        }
        return super.visitUpdate_statement(ctx);
    }

    @Override
    public Void visitDelete_statement(TSqlParser.Delete_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        TSqlParser.Delete_statement_fromContext deleteStatementFromContext = ctx.delete_statement_from();
        if (Objects.nonNull(deleteStatementFromContext)) {
            visit(deleteStatementFromContext);
        }
        return super.visitDelete_statement(ctx);
    }

    @Override
    public Void visitDelete_statement_from(TSqlParser.Delete_statement_fromContext ctx) {
        TSqlParser.Ddl_objectContext ddlObjectContext = ctx.ddl_object();
        if (Objects.nonNull(ddlObjectContext)) {
            TSqlParser.Full_table_nameContext fullTableNameContext = ddlObjectContext.full_table_name();
            if (Objects.nonNull(fullTableNameContext)) {
                visitFullTableName(fullTableNameContext);
            }
        }
        return super.visitDelete_statement_from(ctx);
    }

    @Override
    public Void visitColumn_definition(TSqlParser.Column_definitionContext ctx) {
        Try.of(() -> {
            TSqlParser.Id_Context idContext = ctx.id_();
            if (Objects.isNull(idContext)) {
                return null;
            }
            String columnName = context.getText(idContext.getSourceInterval());
            if (StringUtils.isBlank(columnName)) {
                return null;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            String[] split = columnName.split("\\.");
            columnName = split[split.length - 1];
            TSqlParser.Data_typeContext dataTypeContext = ctx.data_type();
            if (Objects.nonNull(dataTypeContext)) {
                String dataTypeText = dataTypeContext.getText();
                if (StringUtils.isNotBlank(dataTypeText)) {
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(SqlServerStringUtil.removeQuote(columnName));
                    identifier.setIdentifierDataType(dataTypeText);
                    identifier.setIdentifierType(IdentifierTypeEnum.COLUMN.name());
                    currentStatement.addIdentifier(identifier);
                }
            }
            return null;
        }).onFailure(e -> log.error("visit column_definition error", e));

        return null;
    }


    @Override
    public Void visitExecute_body(TSqlParser.Execute_bodyContext ctx) {
        Try.run(() -> {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            TSqlParser.Func_proc_name_server_database_schemaContext funcProcNameServerDatabaseSchemaContext = ctx.func_proc_name_server_database_schema();
            if (Objects.nonNull(funcProcNameServerDatabaseSchemaContext)) {
                List<Token> parserRuleTokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(context.getCommonTokenStream(),
                        funcProcNameServerDatabaseSchemaContext);
                int size = parserRuleTokensOnDefault.size();
                Token database = null, schema = null, procedure = null;
                String databaseText = null, schemaText = null, procedureText = null;
                if (size == 5) {
                    database = parserRuleTokensOnDefault.get(0);
                    schema = parserRuleTokensOnDefault.get(2);
                    procedure = parserRuleTokensOnDefault.get(4);
                } else if (size == 3) {
                    schema = parserRuleTokensOnDefault.get(0);
                    procedure = parserRuleTokensOnDefault.get(2);
                } else if (size == 1) {
                    procedure = parserRuleTokensOnDefault.get(0);
                }

                if (Objects.nonNull(database)) {
                    databaseText = SqlServerStringUtil.removeQuote(database.getText());

                    currentStatement.addIdentifier(databaseText, IdentifierTypeEnum.DATABASE.name(), database);
                }
                if (Objects.nonNull(schema)) {
                    schemaText = SqlServerStringUtil.removeQuote(schema.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(schemaText);
                    identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                    identifier.setFirstToken(schema);
                    identifier.setIdentifierDatabase(databaseText);

                    currentStatement.addIdentifier(identifier);
                }

                if (Objects.nonNull(procedure)) {
                    procedureText = SqlServerStringUtil.removeQuote(procedure.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(procedureText);
                    identifier.setIdentifierDatabase(databaseText);
                    identifier.setIdentifierSchema(schemaText);
                    identifier.setIdentifierType(IdentifierTypeEnum.PROCEDURE.name());
                    identifier.setFirstToken(procedure);
                    currentStatement.addIdentifier(identifier);
                }

            }
        }).onFailure(e -> log.error("visit execute_body error", e));

        return null;
    }

    @Override
    public Void visitSCALAR_FUNCTION(TSqlParser.SCALAR_FUNCTIONContext ctx) {
        TSqlParser.Scalar_function_nameContext scalarFunctionNameContext = ctx.scalar_function_name();
        if (Objects.isNull(scalarFunctionNameContext)) {
            return null;
        }
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        Try.run(() -> {
            List<Token> parserRuleTokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(context.getCommonTokenStream(), scalarFunctionNameContext);
            if (CollectionUtils.isNotEmpty(parserRuleTokensOnDefault)) {
                int size = parserRuleTokensOnDefault.size();
                Token database = null, schema = null, function = null;
                String databaseText = null, schemaText = null, functionText = null;
                if (size == 5) {
                    database = parserRuleTokensOnDefault.get(0);
                    schema = parserRuleTokensOnDefault.get(2);
                    function = parserRuleTokensOnDefault.get(4);
                } else if (size == 3) {
                    schema = parserRuleTokensOnDefault.get(0);
                    function = parserRuleTokensOnDefault.get(2);
                } else if (size == 1) {
                    function = parserRuleTokensOnDefault.get(0);
                }

                if (Objects.nonNull(database)) {
                    databaseText = SqlServerStringUtil.removeQuote(database.getText());

                    currentStatement.addIdentifier(databaseText, IdentifierTypeEnum.DATABASE.name(), database);
                }
                if (Objects.nonNull(schema)) {
                    schemaText = SqlServerStringUtil.removeQuote(schema.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(schemaText);
                    identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                    identifier.setFirstToken(schema);
                    identifier.setIdentifierDatabase(databaseText);
                    currentStatement.addIdentifier(identifier);
                }

                if (Objects.nonNull(function)) {
                    functionText = SqlServerStringUtil.removeQuote(function.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(functionText);
                    identifier.setIdentifierDatabase(databaseText);
                    identifier.setIdentifierSchema(schemaText);
                    identifier.setIdentifierType(IdentifierTypeEnum.UDF_FUNCTION.name());
                    identifier.setFirstToken(function);
                    currentStatement.addIdentifier(identifier);
                }
            }
        }).onFailure(e -> log.error("visit SCALAR_FUNCTION error", e));

        return super.visitSCALAR_FUNCTION(ctx);
    }

    @Override
    public Void visitQuery_specification(TSqlParser.Query_specificationContext ctx) {
        return super.visitQuery_specification(ctx);
    }

    @Override
    public Void visitSelect_list(TSqlParser.Select_listContext ctx) {
        return super.visitSelect_list(ctx);
    }

    @Override
    public Void visitSelect_list_elem(TSqlParser.Select_list_elemContext ctx) {
        return super.visitSelect_list_elem(ctx);
    }
}

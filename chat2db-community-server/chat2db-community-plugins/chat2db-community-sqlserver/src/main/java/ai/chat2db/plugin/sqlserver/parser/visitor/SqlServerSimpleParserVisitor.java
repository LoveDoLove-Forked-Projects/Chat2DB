package ai.chat2db.plugin.sqlserver.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParser;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParserBaseVisitor;
import ai.chat2db.plugin.sqlserver.parser.util.SqlServerStringUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;


@Slf4j
public class SqlServerSimpleParserVisitor extends TSqlParserBaseVisitor<Void> {

    private final StatementContext context;

    public SqlServerSimpleParserVisitor(StatementContext context) {
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
    public Void visitDelete_statement_from(TSqlParser.Delete_statement_fromContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
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
        currentStatement.setType(SqlTypeEnum.ALTER.name());
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
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext);
        }
        return null;
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
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext);
        }
        return null;
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
                visitSimpleName(simpleNameContext);
            }
        } else if (Objects.nonNull(orAlterDdlTrigger)) {
            TSqlParser.Simple_nameContext simpleNameContext = orAlterDdlTrigger.simple_name();
            if (Objects.nonNull(simpleNameContext)) {
                visitSimpleName(simpleNameContext);
            }
        }
        return null;
    }

    private void visitSchemaFunctionOrProcedureName(TSqlParser.Func_proc_name_schemaContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return ;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() != stop.getTokenIndex()) {
            Identifier identifier = new Identifier();
            String schemaText = SqlServerStringUtil.removeQuote(start.getText());
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        } else {
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());

            currentStatement.addIdentifier(identifier);
        }
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
            visitSimpleName(simpleNameContext);
        }
        return null;
    }

    private void visitSimpleName(TSqlParser.Simple_nameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return ;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() != stop.getTokenIndex()) {
            Identifier identifier = new Identifier();
            String schemaText = SqlServerStringUtil.removeQuote(start.getText());
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            identifier.setIdentifierSchema(schemaText);
            currentStatement.addIdentifier(identifier);
        } else {
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }
    }

    @Override
    public Void visitCreate_db_role(TSqlParser.Create_db_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ROLE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitCreate_login_azure_sql(TSqlParser.Create_login_azure_sqlContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitCreate_schema(TSqlParser.Create_schemaContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_SCHEMA.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitCreate_database(TSqlParser.Create_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_DATABASE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
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
        return null;
    }

    private void visitTableName(TSqlParser.Table_nameContext ctx) {
        Try.run(() -> {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return ;
            }
            List<TSqlParser.Id_Context> idContexts = ctx.id_();
            int size = idContexts.size();
            Token databaseToken = null, schemaToken = null;
            String databaseName = null, schemaName = null;
            if (size == 3) {
                databaseToken = idContexts.get(0).getStart();
                schemaToken = idContexts.get(1).getStart();
            } else if (size == 2) {
                schemaToken = idContexts.get(0).getStart();
            }

            if (Objects.nonNull(databaseToken)) {
                databaseName = SqlServerStringUtil.removeQuote(databaseToken.getText());

            }

            if (Objects.nonNull(schemaToken)) {
                schemaName = SqlServerStringUtil.removeQuote(schemaToken.getText());
            }
            Identifier identifier = new Identifier();
            identifier.setIdentifierDatabase(databaseName);
            identifier.setIdentifierSchema(schemaName);
            currentStatement.addIdentifier(identifier);
        }).onFailure(e -> {
            log.error("sqlserver visitTableName error", e);
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
        return null;
    }

    @Override
    public Void visitDrop_database(TSqlParser.Drop_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_DATABASE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }


    @Override
    public Void visitDrop_login(TSqlParser.Drop_loginContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }


    @Override
    public Void visitDrop_db_role(TSqlParser.Drop_db_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_ROLE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitDrop_schema(TSqlParser.Drop_schemaContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_SCHEMA.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }


    @Override
    public Void visitDrop_user(TSqlParser.Drop_userContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }


    @Override
    public Void visitDrop_index(TSqlParser.Drop_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_INDEX.name());
        return null;
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
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext);
        }
        return null;
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
                visitSimpleName(simpleNameContext);
            }
        } else if (Objects.nonNull(dropDmlTriggerContext)) {
            TSqlParser.Simple_nameContext simpleNameContext = dropDmlTriggerContext.simple_name(0);
            if (Objects.nonNull(simpleNameContext)) {
                visitSimpleName(simpleNameContext);
            }
        }
        return null;
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
            visitSchemaFunctionOrProcedureName(funcProcNameSchemaContext);
        }
        return null;
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
        return null;
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
            visitSimpleName(simpleNameContext);
        }
        return null;
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
            String databsaeText = SqlServerStringUtil.removeQuote(idContext.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierDatabase(databsaeText);
            identifier.setIdentifierType(IdentifierTypeEnum.DATABASE.name());
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }

    @Override
    public Void visitInsert_statement(TSqlParser.Insert_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        SqlServerParserVisitor.visitInsertValueMappings(context, ctx);
        return null;
    }


    @Override
    public Void visitMerge_statement(TSqlParser.Merge_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.MERGE.name());
        return null;
    }

    @Override
    public Void visitSelect_statement_standalone(TSqlParser.Select_statement_standaloneContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }

        TSqlParser.Select_statementContext selectStatementContext = ctx.select_statement();
        if (Objects.nonNull(selectStatementContext)) {
            TSqlParser.Query_expressionContext queryExpressionContext = selectStatementContext.query_expression();
            if (Objects.nonNull(queryExpressionContext)) {
                TSqlParser.Query_specificationContext querySpecificationContext = queryExpressionContext.query_specification();
                if (Objects.nonNull(querySpecificationContext)) {
                    TerminalNode into = querySpecificationContext.INTO();
                    if (Objects.nonNull(into)) {
                        currentStatement.setType(SqlTypeEnum.SELECT_INTO.name());
                    }
                }
            }
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        return null;
    }

    @Override
    public Void visitUpdate_statement(TSqlParser.Update_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UPDATE.name());
        return null;
    }

    @Override
    public Void visitDelete_statement(TSqlParser.Delete_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        return null;
    }
}

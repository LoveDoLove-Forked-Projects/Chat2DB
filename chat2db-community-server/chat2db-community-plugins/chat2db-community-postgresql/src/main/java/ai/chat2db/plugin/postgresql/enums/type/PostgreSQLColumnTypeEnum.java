package ai.chat2db.plugin.postgresql.enums.type;

import ai.chat2db.spi.IColumnBuilder;
import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.ColumnType;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static ai.chat2db.plugin.postgresql.constant.PostgreSQLColumnTypeEnumConstants.*;
public enum PostgreSQLColumnTypeEnum implements IColumnBuilder {

    BIGSERIAL("BIGSERIAL", false, false, true, false, false, false, true, true, false, false),
    BIT("BIT", true, false, true, false, false, false, true, true, false, false),
    BOOL("BOOL", false, false, true, false, false, false, true, true, false, false),
    BOOLEAN("BOOLEAN", false, false, true, false, false, false, true, true, false, false),
    BOX("BOX", false, false, true, false, false, false, true, true, false, false),
    BYTEA("BYTEA", false, false, true, false, false, false, true, true, false, false),
    CHAR("CHAR", true, false, true, false, false, true, true, true, false, false),
    BPCHAR("BPCHAR", true, false, true, false, false, true, true, true, false, false),
    CHARACTER("CHARACTER", true, false, true, false, false, true, true, true, false, false),
    CIDR("CIDR", false, false, true, false, false, false, true, true, false, false),
    CIRCLE("CIRCLE", false, false, true, false, false, false, true, true, false, false),
    DATE("DATE", false, false, true, false, false, false, true, true, false, false),
    DECIMAL("DECIMAL", true, true, true, false, false, false, true, true, false, false),
    FLOAT4("FLOAT4", false, false, true, false, false, false, true, true, false, false),
    FLOAT8("FLOAT8", false, false, true, false, false, false, true, true, false, false),
    INET("INET", false, false, true, false, false, false, true, true, false, false),
    INT2("INT2", false, false, true, false, false, false, true, true, false, false),
    SMALLINT("SMALLINT", false, false, true, false, false, false, true, true, false, false),
    INT4("INT4", false, false, true, false, false, false, true, true, false, false),
    INTEGER("INTEGER", false, false, true, false, false, false, true, true, false, false),
    INT8("INT8", false, false, true, false, false, false, true, true, false, false),
    BIGINT("BIGINT", false, false, true, false, false, false, true, true, false, false),
    INTERVAL("INTERVAL", false, false, true, false, false, false, true, true, false, false),
    JSON("JSON", false, false, true, false, false, false, true, true, false, false),
    JSONB("JSONB", false, false, true, false, false, false, true, true, false, false),
    JSONPATH("JSONPATH", false, false, true, false, false, false, true, true, false, false),
    LINE("LINE", false, false, true, false, false, false, true, true, false, false),
    LSEG("LSEG", false, false, true, false, false, false, true, true, false, false),
    MACADDR("MACADDR", false, false, true, false, false, false, true, true, false, false),
    MONEY("MONEY", false, false, true, false, false, false, true, true, false, false),
    NUMERIC("NUMERIC", true, true, true, false, false, false, true, true, false, false),
    PATH("PATH", false, false, true, false, false, false, true, true, false, false),
    POINT("POINT", false, false, true, false, false, false, true, true, false, false),
    POLYGON("POLYGON", false, false, true, false, false, false, true, true, false, false),
    SERIAL("SERIAL", false, false, true, false, false, false, true, true, false, false),
    SERIAL2("SERIAL2", false, false, true, false, false, false, true, true, false, false),
    SERIAL4("SERIAL4", false, false, true, false, false, false, true, true, false, false),
    SERIAL8("SERIAL8", false, false, true, false, false, false, true, true, false, false),
    SMALLSERIAL("SMALLSERIAL", false, false, true, false, false, false, true, true, false, false),
    TEXT("TEXT", false, false, true, false, false, true, true, true, false, false),
    TIME("TIME", true, false, true, false, false, false, true, true, false, false),
    TIMESTAMP("TIMESTAMP", true, false, true, false, false, false, true, true, false, false),
    TIMESTAMPTZ("TIMESTAMPTZ", true, false, true, false, false, false, true, true, false, false),
    TIMETZ("TIMETZ", true, false, true, false, false, false, true, true, false, false),
    TSQUERY("TSQUERY", false, false, true, false, false, false, true, true, false, false),
    TSVECTOR("TSVECTOR", false, false, true, false, false, false, true, true, false, false),
    TXID_SNAPSHOT("TXID_SNAPSHOT", false, false, true, false, false, false, true, true, false, false),
    UUID("UUID", false, false, true, false, false, false, true, true, false, false),
    VARBIT("VARBIT", true, false, true, false, false, false, true, true, false, false),
    BITVARYING("BIT VARYING", true, false, true, false, false, false, true, true, false, false),
    VARCHAR("VARCHAR", true, false, true, false, false, true, true, true, false, false),
    CHARACTERVARYING("CHARACTER VARYING", true, false, true, false, false, true, true, true, false, false),
    XML("XML", false, false, true, false, false, false, true, true, false, false),

    ;





    private static Map<String, PostgreSQLColumnTypeEnum> COLUMN_TYPE_MAP = Maps.newHashMap();

    static {
        for (PostgreSQLColumnTypeEnum value : PostgreSQLColumnTypeEnum.values()) {
            COLUMN_TYPE_MAP.put(value.getColumnType().getTypeName(), value);
        }
    }

    private ColumnType columnType;


    PostgreSQLColumnTypeEnum(String dataTypeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent, boolean supportValue) {
        this.columnType = new ColumnType(dataTypeName, supportLength, supportScale, supportNullable, supportAutoIncrement, supportCharset, supportCollation, supportComments, supportDefaultValue, supportExtent, supportValue, false);
    }

    public static PostgreSQLColumnTypeEnum getByType(String dataType) {
        return COLUMN_TYPE_MAP.get(SqlUtils.removeDigits(dataType.toUpperCase()));
    }

    public static List<ColumnType> getTypes() {
        return Arrays.stream(PostgreSQLColumnTypeEnum.values()).map(columnTypeEnum ->
                columnTypeEnum.getColumnType()
        ).toList();
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    @Override
    public String buildCreateColumnSql(TableColumn column) {
        PostgreSQLColumnTypeEnum type = COLUMN_TYPE_MAP.get(column.getColumnType().toUpperCase());
        if (type == null) {
            return buildDefaultColumn(column, false);
        }
        StringBuilder script = new StringBuilder();

        script.append("\"").append(column.getName()).append("\"").append(" ");

        script.append(buildDataType(column, type)).append(" ");


        script.append(buildCollation(column, type)).append(" ");

        script.append(buildNullable(column, type)).append(" ");

        script.append(buildDefaultValue(column, type)).append(" ");

        return script.toString();
    }

    @Override
    public String buildAICreateColumnSql(TableColumn column) {
        PostgreSQLColumnTypeEnum type = COLUMN_TYPE_MAP.get(column.getColumnType().toUpperCase());
        if (type == null) {
            return buildDefaultColumn(column, false);
        }
        StringBuilder script = new StringBuilder();

        script.append("\"").append(column.getName()).append("\"").append(" ");

        script.append(buildDataType(column, type)).append(" ");


        script.append(buildCollation(column, type)).append(" ");

        script.append(buildNullable(column, type)).append(" ");

        script.append(buildDefaultValue(column, type)).append(" ");

        script.append(buildAICreateColumnCommentSql(column)).append(" ");

        return script.toString();
    }


    private String buildCollation(TableColumn column, PostgreSQLColumnTypeEnum type) {
        if (!type.getColumnType().isSupportCollation() || StringUtils.isEmpty(column.getCollationName())) {
            return "";
        }
        return StringUtils.join("\"", column.getCollationName(), "\"");
    }

    @Override
    public String buildModifyColumn(TableColumn column) {
        if (EditStatusEnum.DELETE.name().equals(column.getEditStatus())) {
            return StringUtils.join(SQL_DROP_COLUMN, column.getName() + "\"");
        } else if (EditStatusEnum.ADD.name().equals(column.getEditStatus())) {
            return StringUtils.join("ADD COLUMN ", buildCreateColumnSql(column));
        } else if (EditStatusEnum.MODIFY.name().equals(column.getEditStatus())) {
            TableColumn oldColumn = column.getOldColumn();
            StringBuilder script = new StringBuilder();
            if (oldColumn != null) {
                boolean sameType = StringUtils.equalsIgnoreCase(oldColumn.getColumnType(), column.getColumnType());
                Integer oldColumnSize = oldColumn.getColumnSize();
                Integer oldDecimalDigits = oldColumn.getDecimalDigits();
                Integer newColumnSize = column.getColumnSize();
                Integer newDecimalDigits = column.getDecimalDigits();

                boolean sizeChanged = oldColumnSize != null && newColumnSize != null && !oldColumnSize.equals(newColumnSize);
                boolean scaleChanged = oldDecimalDigits != null && newDecimalDigits != null && !oldDecimalDigits.equals(newDecimalDigits);
                if (!sameType || sizeChanged || scaleChanged) {
                    String newDataTypeClause = buildDataType(column, this);
                    script.append(SQL_ALTER_COLUMN)
                            .append(column.getName())
                            .append("\" TYPE ")
                            .append(newDataTypeClause);
                    script.append(" USING \"")
                            .append(column.getName())
                            .append("\"::")
                            .append(newDataTypeClause)
                            .append(",\n");
                }
            } else {
                String newDataTypeClause = buildDataType(column, this);
                script.append(SQL_ALTER_COLUMN)
                        .append(column.getName())
                        .append("\" TYPE ")
                        .append(newDataTypeClause)
                        .append(" USING \"")
                        .append(column.getName())
                        .append("\"::")
                        .append(newDataTypeClause)
                        .append(",\n");
            }

            Integer newNullable = column.getNullable();
            String columnName = column.getName();
            boolean shouldDropNotNull = newNullable != null && newNullable == 1;


            if (oldColumn != null) {
                Integer oldNullable = oldColumn.getNullable();
                if (oldNullable != null && newNullable != null && !oldNullable.equals(newNullable)) {
                    script.append("\tALTER COLUMN \"").append(columnName).append("\" ")
                            .append(shouldDropNotNull ? "DROP" : "SET").append(" NOT NULL ,\n");
                }
            } else {
                if (newNullable != null) {
                    script.append("\tALTER COLUMN \"").append(columnName).append("\" ")
                            .append(shouldDropNotNull ? "DROP" : "SET").append(" NOT NULL ,\n");
                }
            }
            String defaultValue = buildDefaultValue(column, this);
            boolean shouldAppendDefault = false;
            if (oldColumn != null) {
                String oldDefault = oldColumn.getDefaultValue();
                String newDefault = column.getDefaultValue();
                if (!StringUtils.equals(oldDefault, newDefault)) {
                    shouldAppendDefault = StringUtils.isNotBlank(defaultValue);
                }
            } else {
                shouldAppendDefault = StringUtils.isNotBlank(defaultValue);
            }

            if (shouldAppendDefault) {
                script.append(SQL_ALTER_COLUMN)
                        .append(column.getName())
                        .append("\" SET ")
                        .append(defaultValue)
                        .append(",\n");
            }
            if (script.length() > 2) {
                int length = script.length();
                if (script.substring(length - 2).equals(",\n")) {
                    script.setLength(length - 2);
                }
            }
            return script.toString();
        } else {
            return "";
        }
    }

    public String buildComment(TableColumn column, PostgreSQLColumnTypeEnum type) {
        if (!this.columnType.isSupportComments() || column.getComment() == null
                || EditStatusEnum.DELETE.name().equals(column.getEditStatus())) {
            return "";
        }
        if (column.getOldColumn() == null || !StringUtils.equals(column.getOldColumn().getComment(), column.getComment())) {
            return StringUtils.join(SQL_COMMENT_COLUMN, " \"", column.getTableName(),
                    "\".\"", column.getName(), "\" IS '", column.getComment(), "';");
        }
        return "";
    }

    private String buildDefaultValue(TableColumn column, PostgreSQLColumnTypeEnum type) {
        if (!type.getColumnType().isSupportDefaultValue() || StringUtils.isEmpty(column.getDefaultValue())) {
            return "";
        }

        if ("EMPTY_STRING".equalsIgnoreCase(column.getDefaultValue().trim())) {
            return StringUtils.join("DEFAULT ''");
        }

        if ("NULL".equalsIgnoreCase(column.getDefaultValue().trim())) {
            return StringUtils.join("DEFAULT NULL");
        }

        if (Arrays.asList(CHAR, VARCHAR).contains(type)) {
            return StringUtils.join("DEFAULT '", column.getDefaultValue(), "'");
        }

        if (Arrays.asList(TIMESTAMP, TIME, TIMETZ, TIMESTAMPTZ, DATE).contains(type)) {
            if ("CURRENT_TIMESTAMP".equalsIgnoreCase(column.getDefaultValue().trim())) {
                return StringUtils.join("DEFAULT ", column.getDefaultValue());
            }
            return StringUtils.join("DEFAULT '", column.getDefaultValue(), "'");
        }

        return StringUtils.join("DEFAULT ", column.getDefaultValue());
    }

    private String buildNullable(TableColumn column, PostgreSQLColumnTypeEnum type) {
        if (!type.getColumnType().isSupportNullable()) {
            return "";
        }
        if (column.getNullable() != null && 1 == column.getNullable()) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }

    private String buildDataType(TableColumn column, PostgreSQLColumnTypeEnum type) {
        String columnType = type.columnType.getTypeName();
        if (Arrays.asList(VARCHAR, CHAR).contains(type)) {
            if (column.getColumnSize() == null) {
                return columnType;
            }
            return StringUtils.join(columnType, "(", column.getColumnSize(), ")");
        }

        if (Arrays.asList(VARBIT, BIT).contains(type)) {
            if (column.getColumnSize() == null) {
                return columnType;
            }
            return StringUtils.join(columnType, "(", column.getColumnSize(), ")");
        }

        if (Arrays.asList(TIME, TIMETZ, TIMESTAMPTZ, TIMESTAMP).contains(type)) {
            if (column.getColumnSize() == null || column.getColumnSize() == 0) {
                return columnType;
            } else {
                return StringUtils.join(columnType, "(", column.getColumnSize(), ")");
            }
        }

        if (Arrays.asList(DECIMAL, NUMERIC).contains(type)) {
            if (column.getColumnSize() == null && column.getDecimalDigits() == null) {
                return columnType;
            }
            if (column.getColumnSize() != null && column.getDecimalDigits() == null) {
                return StringUtils.join(columnType, "(", column.getColumnSize() + ")");
            } else {
                return StringUtils.join(columnType, "(", column.getColumnSize() + "," + column.getDecimalDigits() + ")");
            }
        }
        return columnType;
    }

}

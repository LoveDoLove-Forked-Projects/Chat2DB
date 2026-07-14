package ai.chat2db.plugin.clickhouse.enums.type;

import ai.chat2db.community.domain.api.enums.plugin.EditStatusEnum;
import ai.chat2db.community.domain.api.model.metadata.IndexType;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.model.metadata.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static ai.chat2db.plugin.clickhouse.constant.ClickHouseIndexTypeEnumConstants.*;
public enum ClickHouseIndexTypeEnum {

    PRIMARY("Primary", "PRIMARY KEY"),
    MINMAX("MINMAX", "INDEX"),
    SET("SET", "INDEX"),
    BLOOM_FILTER("BLOOM_FILTER", "INDEX"),
    TOKENBF_V1("TOKENBF_V1", "INDEX"),
    NGRAMBF_V1("NGRAMBF_V1", "INDEX"),
    INVERTED("INVERTED", "INDEX"),
    HYPOTHESIS("HYPOTHESIS", "INDEX"),
    ANNOY("ANNOY", "INDEX"),
    USEARCH("USEARCH", "INDEX"),

    ;



    private String name;
    private String keyword;
    private IndexType indexType;

    ClickHouseIndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType = new IndexType(name);
    }

    public static ClickHouseIndexTypeEnum getByType(String type) {
        for (ClickHouseIndexTypeEnum value : ClickHouseIndexTypeEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public static List<IndexType> getIndexTypes() {
        return Arrays.asList(ClickHouseIndexTypeEnum.values()).stream().map(ClickHouseIndexTypeEnum::getIndexType).collect(java.util.stream.Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public String buildIndexScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();

        script.append(keyword).append(" ");
        script.append(buildIndexName(tableIndex)).append(" ");
        script.append(buildIndexColumn(tableIndex)).append(" ");
        script.append(buildIndexType(tableIndex)).append(" ");
        return script.toString();
    }

    private String buildIndexType(TableIndex tableIndex) {
        if (this.equals(PRIMARY)) {
            return "";
        } else {
            return "TYPE " + name ;
        }
    }

    private String buildIndexColumn(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("(");
        for (TableIndexColumn column : tableIndex.getColumnList()) {
            if (StringUtils.isNotBlank(column.getColumnName())) {
                script.append("`").append(column.getColumnName()).append("`");
                script.append(",");
            }
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();
    }

    private String buildIndexName(TableIndex tableIndex) {
        if (this.equals(PRIMARY)) {
            return "";
        } else {
            return "`" + tableIndex.getName() + "`";
        }
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        if (this.equals(PRIMARY)) {
            return "";
        }
        if (EditStatusEnum.DELETE.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(SQL_DROP_INDEX, tableIndex.getOldName(), "`");
        }
        if (EditStatusEnum.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(SQL_DROP_INDEX, tableIndex.getOldName(),
                    "`,\n ADD ", buildIndexScript(tableIndex));
        }
        if (EditStatusEnum.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join("ADD ", buildIndexScript(tableIndex));
        }
        return "";
    }

}

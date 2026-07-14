package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.spi.constant.SQLConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Builds dialect-specific column definition SQL.
 */
public interface IColumnBuilder {

    /**
     * Builds the SQL fragment used to create a column.
     *
     * @param column column metadata to render.
     * @return SQL fragment for the column definition.
     */
    String buildCreateColumnSql(TableColumn column);


    /**
     * Builds an AI-friendly column definition fragment.
     *
     * @param column column metadata to render.
     * @return AI-oriented column definition text, or an empty string when unsupported.
     */
    default String buildAICreateColumnSql(TableColumn column) {
        return "";
    }


    /**
     * Builds an AI-friendly column comment fragment.
     *
     * @param column column metadata that may contain a comment.
     * @return comment fragment, or an empty string when the column has no comment.
     */
    default String buildAICreateColumnCommentSql(TableColumn column) {
        String comment = column.getComment();
        if (StringUtils.isBlank(comment)) {
            return "";
        }
        return " -- " + comment;
    }


    /**
     * Builds the SQL fragment used to modify an existing column.
     *
     * @param tableColumn new column metadata to apply.
     * @return SQL fragment for the column modification.
     */
    String buildModifyColumn(TableColumn tableColumn);


    /**
     * Builds a default column definition from name, type, and optional comment metadata.
     *
     * @param tableColumn column metadata to render.
     * @param comment whether the column comment should be included.
     * @return default column definition fragment.
     */
    default String buildDefaultColumn(TableColumn tableColumn, boolean comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(tableColumn.getName()).append(" ").append(tableColumn.getColumnType());
        if (comment) {
            sb.append(SQLConstants.COLUMN_COMMENT_SQL_PREFIX).append(tableColumn.getComment())
                    .append(SQLConstants.SINGLE_QUOTE);
        }
        return sb.toString();
    }
}

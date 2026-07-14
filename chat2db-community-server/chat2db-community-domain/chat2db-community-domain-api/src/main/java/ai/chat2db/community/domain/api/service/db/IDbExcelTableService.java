package ai.chat2db.community.domain.api.service.db;

import java.util.Map;

import ai.chat2db.community.domain.api.model.excel.ExcelCheckResponse;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;

public interface IDbExcelTableService {

    /**
     * Checks an Excel file and returns import configuration metadata.
     *
     * @param filePath local Excel file path.
     * @return Excel import check response.
     */
    ExcelCheckResponse check(String filePath);

    /**
     * Initializes an Excel-backed datasource from a checked file.
     *
     * @param filePath local Excel file path.
     * @param id datasource identifier.
     * @param config Excel import configuration.
     */
    void init(String filePath, Long id, ExcelCheckResponse config);

    /**
     * Converts Excel date-time cells in a row into string values.
     *
     * @param data row data keyed by column index.
     * @return row data with date-time values normalized to strings.
     */
    Map<Integer, Object> dateTime2Str(Map<Integer, Object> data);

    /**
     * Executes a SQL query against an Excel-backed datasource.
     *
     * @param sql SQL text to execute.
     * @param id datasource identifier.
     * @return query execution response.
     */
    ExecuteResponse query(String sql, Long id);

    /**
     * Creates a datasource record for an uploaded Excel file.
     *
     * @param originalFilename uploaded file name.
     * @param filePath local Excel file path.
     * @return created datasource identifier.
     */
    Long createExcelDataSource(String originalFilename, String filePath);
}

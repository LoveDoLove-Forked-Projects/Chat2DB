package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.db.ImportPreCheck;
import ai.chat2db.community.domain.api.model.db.ImportPreCheckIssue;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.task.CsvOptions;
import ai.chat2db.community.domain.api.model.task.ExcelOptions;
import ai.chat2db.community.domain.api.model.task.ImportColumnMapping;
import ai.chat2db.community.domain.api.model.task.ImportValueFormat;
import ai.chat2db.community.domain.api.model.task.JsonOptions;
import ai.chat2db.community.domain.api.service.db.IDbImportPreCheckService;
import ai.chat2db.community.domain.core.impl.task.imports.excel.CsvImportValueNormalizer;
import ai.chat2db.community.domain.core.impl.task.imports.reader.SourceColumnName;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.spi.model.request.TableMetadataRequest;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pre-flight checks for the import wizard.
 *
 * <p>The full file is scanned once for the row count. Conflicts and type mismatches only inspect the
 * first sample rows: a full-file validation would repeat the import, and the wizard labels these
 * numbers as samples. The conflict query is read-only and parameterized; an unsupported target or a
 * failed query marks the check as unavailable instead of failing the pre-check.
 */
@Service
public class DbImportPreCheckServiceImpl implements IDbImportPreCheckService {

    private static final int SAMPLE_ROW_LIMIT = 100;

    private static final int MAX_TYPE_ISSUES = 20;

    private final ImportPreviewFileParser fileParser;

    public DbImportPreCheckServiceImpl(ImportPreviewFileParser fileParser) {
        this.fileParser = fileParser;
    }

    @Override
    public ImportPreCheck analyze(Long dataSourceId, String databaseName, String schemaName, String tableName,
            File file, CsvOptions csvOptions, ExcelOptions excelOptions, JsonOptions jsonOptions,
            List<ImportColumnMapping> mappings) {
        ImportPreviewFileParser.ParsedRows parsed = fileParser.parse(file, SAMPLE_ROW_LIMIT,
                csvOptions, excelOptions, jsonOptions);
        if (parsed.header().isEmpty()) {
            throw new BusinessException("import.preview.emptyFile");
        }

        List<String> sourceNames = sourceNames(parsed.header());
        requireUniqueSourceColumns(sourceNames);
        TableMetadataRequest target = TrustedMetadataRequestResolver.table(dataSourceId, databaseName,
                schemaName, tableName);
        List<TableColumn> targetColumns = Chat2DBContext.getDbMetaData().columns(
                Chat2DBContext.getConnection(), target);

        Map<String, Integer> sourceIndexes = new HashMap<>();
        for (int index = 0; index < sourceNames.size(); index++) {
            sourceIndexes.put(sourceNames.get(index).toUpperCase(Locale.ROOT), index);
        }
        Map<String, TableColumn> targetsByName = new LinkedHashMap<>();
        for (TableColumn column : targetColumns) {
            targetsByName.put(column.getName().toUpperCase(Locale.ROOT), column);
        }

        List<MappedColumn> mappedColumns = mappedColumns(mappings, sourceIndexes, targetsByName);
        List<ImportPreCheckIssue> typeMismatches = typeMismatches(parsed.data(), mappedColumns,
                valueOptions(csvOptions, excelOptions, jsonOptions));

        List<TableColumn> keyColumns = targetColumns.stream()
                .filter(column -> Boolean.TRUE.equals(column.getPrimaryKey()))
                .sorted(Comparator.comparingInt(column -> column.getPrimaryKeyOrder() <= 0
                        ? Integer.MAX_VALUE : column.getPrimaryKeyOrder()))
                .toList();
        ConflictResult conflicts = countConflicts(target, keyColumns, mappedColumns, parsed.data());

        return ImportPreCheck.builder()
                .sampledRows(parsed.data().size())
                .conflictCheckable(conflicts.checkable())
                .keyColumns(keyColumns.stream().map(TableColumn::getName).toList())
                .conflictingSampleRows(conflicts.rows())
                .typeMismatches(typeMismatches)
                .build();
    }

    private static List<String> sourceNames(Map<Integer, String> header) {
        List<String> names = new ArrayList<>();
        for (int index = 0; index < header.size(); index++) {
            names.add(StringUtils.defaultIfBlank(header.get(index), SourceColumnName.of(index)));
        }
        return names;
    }

    private static void requireUniqueSourceColumns(List<String> sourceColumns) {
        HashSet<String> names = new HashSet<>();
        for (String sourceColumn : sourceColumns) {
            if (!names.add(sourceColumn.toUpperCase(Locale.ROOT))) {
                throw new BusinessException("import.preview.duplicateSourceColumns", new Object[]{sourceColumn});
            }
        }
    }

    private static List<MappedColumn> mappedColumns(List<ImportColumnMapping> mappings,
            Map<String, Integer> sourceIndexes, Map<String, TableColumn> targetsByName) {
        if (mappings == null || mappings.isEmpty()) {
            return List.of();
        }
        List<MappedColumn> mapped = new ArrayList<>();
        for (ImportColumnMapping mapping : mappings) {
            if (mapping == null || StringUtils.isBlank(mapping.getSourceColumn())
                    || StringUtils.isBlank(mapping.getTargetColumn())) {
                continue;
            }
            Integer sourceIndex = sourceIndexes.get(mapping.getSourceColumn().toUpperCase(Locale.ROOT));
            TableColumn targetColumn = targetsByName.get(mapping.getTargetColumn().toUpperCase(Locale.ROOT));
            if (sourceIndex != null && targetColumn != null) {
                mapped.add(new MappedColumn(sourceIndex, mapping.getSourceColumn(), targetColumn));
            }
        }
        return mapped;
    }

    private static List<ImportPreCheckIssue> typeMismatches(List<Map<Integer, String>> rows,
            List<MappedColumn> mappedColumns, ImportValueFormat valueOptions) {
        long firstDataRow = firstDataRow(valueOptions);
        List<ImportPreCheckIssue> issues = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows.size() && issues.size() < MAX_TYPE_ISSUES; rowIndex++) {
            Map<Integer, String> row = rows.get(rowIndex);
            long rowNumber = firstDataRow + rowIndex;
            for (MappedColumn mapped : mappedColumns) {
                if (issues.size() >= MAX_TYPE_ISSUES) {
                    return issues;
                }
                String value = row.get(mapped.sourceIndex());
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                try {
                    // normalize() applies the format rules; jdbcParameter() performs the type
                    // conversion the importer would perform, so an unparseable number is caught here.
                    String normalized = CsvImportValueNormalizer.normalize(value, mapped.target(),
                            valueOptions, rowNumber);
                    CsvImportValueNormalizer.jdbcParameter(normalized, mapped.target());
                } catch (RuntimeException e) {
                    issues.add(ImportPreCheckIssue.builder()
                            .rowNumber(rowNumber)
                            .sourceColumn(mapped.sourceName())
                            .targetColumn(mapped.target().getName())
                            .value(value)
                            .reasonCode(reasonCode(e))
                            .reason(StringUtils.defaultIfBlank(e.getMessage(), e.getClass().getSimpleName()))
                            .build());
                }
            }
        }
        return issues;
    }

    private static String reasonCode(Throwable failure) {
        for (Throwable current = failure; current != null; current = current.getCause()) {
            if (current instanceof NumberFormatException) {
                return "NUMBER";
            }
            if (current instanceof java.time.format.DateTimeParseException) {
                return "DATETIME";
            }
        }
        return failure instanceof BusinessException ? "FORMAT" : "VALUE";
    }

    /**
     * Absolute file row number of the first sample row. The importer and the error artifact report
     * file rows, so the pre-check uses the same numbering instead of a bare data-row index.
     */
    private static long firstDataRow(ImportValueFormat valueOptions) {
        if (valueOptions instanceof CsvOptions csv) {
            return csv.getDataStartRow();
        }
        if (valueOptions instanceof ExcelOptions excel) {
            return excel.getDataStartRow();
        }
        return 1L;
    }

    private static ImportValueFormat valueOptions(CsvOptions csvOptions, ExcelOptions excelOptions,
            JsonOptions jsonOptions) {
        if (excelOptions != null) {
            return excelOptions.validate();
        }
        if (jsonOptions != null) {
            return jsonOptions.validate();
        }
        return (csvOptions == null ? CsvOptions.defaults() : csvOptions).validate();
    }

    /**
     * Counts sample rows whose mapped primary key already exists. The query is built from trusted
     * metadata and bound parameters; a target without a primary key or a failed query only makes the
     * check unavailable.
     */
    private static ConflictResult countConflicts(TableMetadataRequest target, List<TableColumn> keyColumns,
            List<MappedColumn> mappedColumns, List<Map<Integer, String>> rows) {
        if (keyColumns.isEmpty() || mappedColumns.isEmpty()) {
            return ConflictResult.unavailable();
        }
        Map<String, MappedColumn> mappedByTarget = new HashMap<>();
        for (MappedColumn mapped : mappedColumns) {
            mappedByTarget.put(mapped.target().getName().toUpperCase(Locale.ROOT), mapped);
        }
        List<MappedColumn> mappedKeys = new ArrayList<>();
        for (TableColumn key : keyColumns) {
            MappedColumn mapped = mappedByTarget.get(key.getName().toUpperCase(Locale.ROOT));
            if (mapped == null) {
                return ConflictResult.unavailable();
            }
            mappedKeys.add(mapped);
        }

        List<List<Object>> tuples = new ArrayList<>();
        for (Map<Integer, String> row : rows) {
            List<Object> values = new ArrayList<>(mappedKeys.size());
            boolean complete = true;
            for (int index = 0; index < mappedKeys.size(); index++) {
                String value = row.get(mappedKeys.get(index).sourceIndex());
                if (StringUtils.isBlank(value)) {
                    complete = false;
                    break;
                }
                try {
                    values.add(CsvImportValueNormalizer.jdbcParameter(value, keyColumns.get(index)));
                } catch (RuntimeException e) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                tuples.add(values);
            }
        }
        if (tuples.isEmpty()) {
            return new ConflictResult(0, true);
        }

        Connection connection = Chat2DBContext.getConnection();
        try {
            String quote = identifierQuote(connection);
            String table = qualifiedTable(target, quote);
            String where = whereClause(mappedKeys, tuples.size(), quote);
            String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + where;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int parameter = 1;
                for (List<Object> tuple : tuples) {
                    for (Object value : tuple) {
                        statement.setObject(parameter++, value);
                    }
                }
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? new ConflictResult(result.getInt(1), true) : new ConflictResult(0, true);
                }
            }
        } catch (SQLException | RuntimeException e) {
            return ConflictResult.unavailable();
        }
    }

    private static String whereClause(List<MappedColumn> mappedKeys, int tupleCount, String quote) {
        String one = "(" + mappedKeys.stream()
                .map(mapped -> identify(mapped.target().getName(), quote) + " = ?")
                .reduce((left, right) -> left + " AND " + right)
                .orElse("1 = 1") + ")";
        return String.join(" OR ", java.util.Collections.nCopies(tupleCount, one));
    }

    private static String qualifiedTable(TableMetadataRequest target, String quote) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(target.getDatabaseName())) {
            parts.add(identify(target.getDatabaseName(), quote));
        }
        if (StringUtils.isNotBlank(target.getSchemaName())) {
            parts.add(identify(target.getSchemaName(), quote));
        }
        parts.add(identify(target.getTableName(), quote));
        return String.join(".", parts);
    }

    private static String identify(String name, String quote) {
        return quote.isEmpty() ? name : quote + name.replace(quote, quote + quote) + quote;
    }

    private static String identifierQuote(Connection connection) throws SQLException {
        String quote = connection.getMetaData() == null ? null : connection.getMetaData().getIdentifierQuoteString();
        return quote == null ? "" : quote.trim();
    }

    private record MappedColumn(int sourceIndex, String sourceName, TableColumn target) {
    }

    private record ConflictResult(int rows, boolean checkable) {
        private static ConflictResult unavailable() {
            return new ConflictResult(0, false);
        }
    }
}

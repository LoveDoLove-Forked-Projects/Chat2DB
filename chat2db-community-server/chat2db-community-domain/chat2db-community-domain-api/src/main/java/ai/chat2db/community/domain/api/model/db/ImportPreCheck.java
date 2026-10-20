package ai.chat2db.community.domain.api.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Pre-flight facts the import wizard shows before a task starts.
 *
 * <p>Conflicts and type mismatches are computed from the first {@link #getSampledRows()} rows only,
 * because a full-file validation would duplicate the import itself; the UI labels them as samples.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportPreCheck {

    /** Data rows examined for conflicts and type mismatches. */
    private int sampledRows;

    /** True when the target has a primary key, so existing rows can be detected. */
    private boolean conflictCheckable;

    /** Primary-key columns used for the conflict check, in key order. */
    private List<String> keyColumns;

    /** Sampled rows whose mapped primary key already exists in the target table. */
    private int conflictingSampleRows;

    /** Mapped sample cells that the importer's own value rules reject. */
    private List<ImportPreCheckIssue> typeMismatches;
}

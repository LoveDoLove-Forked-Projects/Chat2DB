package ai.chat2db.community.domain.api.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One sample cell the importer would reject for its mapped target column. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportPreCheckIssue {

    /** One-based position inside the sampled rows, not the physical file row number. */
    private long rowNumber;

    private String sourceColumn;

    private String targetColumn;

    private String value;

    /** Stable classification of the failure: NUMBER, DATETIME, FORMAT or VALUE. */
    private String reasonCode;

    /** Raw message for logs; the UI prefers the translated {@link #reasonCode}. */
    private String reason;
}

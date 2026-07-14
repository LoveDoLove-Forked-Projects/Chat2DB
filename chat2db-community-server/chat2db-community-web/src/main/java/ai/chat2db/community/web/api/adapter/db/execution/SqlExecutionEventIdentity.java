package ai.chat2db.community.web.api.adapter.db.execution;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SqlExecutionEventIdentity {

    private final Integer statementSequence;

    private final Integer resultSequence;
}

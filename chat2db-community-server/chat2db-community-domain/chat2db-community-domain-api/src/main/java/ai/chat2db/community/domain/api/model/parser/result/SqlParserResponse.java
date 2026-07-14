package ai.chat2db.community.domain.api.model.parser.result;

import ai.chat2db.community.domain.api.model.parser.message.SyntaxErrorMessage;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlParserResponse {

    private List<Statement> statements;
    private List<SyntaxErrorMessage> syntaxErrors;

}

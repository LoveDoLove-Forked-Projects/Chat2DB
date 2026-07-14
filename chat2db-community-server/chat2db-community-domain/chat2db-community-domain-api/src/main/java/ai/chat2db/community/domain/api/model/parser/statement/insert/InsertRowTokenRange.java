package ai.chat2db.community.domain.api.model.parser.statement.insert;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.antlr.v4.runtime.Token;

@Data
@AllArgsConstructor
public class InsertRowTokenRange {
    private Token firstToken;
    private Token lastToken;
}

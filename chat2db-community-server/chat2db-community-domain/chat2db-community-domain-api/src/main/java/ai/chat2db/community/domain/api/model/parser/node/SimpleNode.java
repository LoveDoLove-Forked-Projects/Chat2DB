package ai.chat2db.community.domain.api.model.parser.node;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleNode {
    protected Token firstToken;
    protected Token lastToken;

    public SimpleNode(Token firstToken) {
        this.firstToken = firstToken;
    }


}


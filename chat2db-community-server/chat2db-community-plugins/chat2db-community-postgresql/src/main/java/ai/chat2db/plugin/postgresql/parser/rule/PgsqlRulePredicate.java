package ai.chat2db.plugin.postgresql.parser.rule;

import ai.chat2db.spi.parser.AbstractRulePredicate;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;

@NoArgsConstructor
public abstract class PgsqlRulePredicate extends AbstractRulePredicate {


    @Override
    public boolean needUpgradeBlock(List<Token> tokens, int currentIndex) {
        return false;
    }

    @Override
    public boolean matches(TokenStream tokenStream) {
        return false;
    }
}

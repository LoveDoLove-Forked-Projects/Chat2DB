package ai.chat2db.spi.parser;

import ai.chat2db.spi.IRuleManager;
import ai.chat2db.spi.IRulePredicate;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public abstract class AbstractRuleManager implements IRuleManager {


    @Override
    public boolean matchRules(List<Token> tokens, int currentTokenIndex) {
        Map<String, IRulePredicate> rules = getRules();
        if (MapUtils.isEmpty(rules)) {
            return true;
        }
        Token token = tokens.get(currentTokenIndex);
        String ruleKey = token.getText().toUpperCase();
        IRulePredicate rulePredicate = rules.get(ruleKey);
        if (rulePredicate == null) {
            return true;
        }
        return rulePredicate.matches(tokens, currentTokenIndex);
    }

    @Override
    public boolean matchRules(TokenStream tokenStream) {
        Map<String, IRulePredicate> rules = getRules();
        if (MapUtils.isEmpty(rules)) {
            return true;
        }
        Token token = tokenStream.LT(1);
        String ruleKey = token.getText().toUpperCase();
        IRulePredicate rulePredicate = rules.get(ruleKey);
        if (rulePredicate == null) {
            return true;
        }
        return rulePredicate.matches(tokenStream);
    }


    @Override
    public boolean upgradeBlockRules(List<Token> tokens, int currentTokenIndex) {
        Map<String, IRulePredicate> rules = getRules();
        if (MapUtils.isEmpty(rules)) {
            return false;
        }
        Token token = tokens.get(currentTokenIndex);
        String ruleKey = token.getText().toUpperCase();
        IRulePredicate rulePredicate = rules.get(ruleKey);
        if (rulePredicate == null) {
            return false;
        }
        return rulePredicate.needUpgradeBlock(tokens, currentTokenIndex);
    }
}

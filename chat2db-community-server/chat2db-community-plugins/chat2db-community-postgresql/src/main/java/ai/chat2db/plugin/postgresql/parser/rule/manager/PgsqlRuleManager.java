package ai.chat2db.plugin.postgresql.parser.rule.manager;

import ai.chat2db.spi.parser.AbstractRuleManager;
import ai.chat2db.spi.IRulePredicate;
import ai.chat2db.plugin.postgresql.parser.rule.predicate.*;

import java.util.HashMap;
import java.util.Map;

public class PgsqlRuleManager extends AbstractRuleManager  {


    private static final Map<String, IRulePredicate> RULE_PREDICATES = new HashMap<>();

    @Override
    public Map<String, IRulePredicate> getRules() {
        return RULE_PREDICATES;
    }


}

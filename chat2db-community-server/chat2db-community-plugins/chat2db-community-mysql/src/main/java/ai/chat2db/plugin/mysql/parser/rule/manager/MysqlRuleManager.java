package ai.chat2db.plugin.mysql.parser.rule.manager;

import ai.chat2db.plugin.mysql.parser.rule.predicate.*;
import ai.chat2db.spi.parser.AbstractRuleManager;
import ai.chat2db.spi.IRulePredicate;

import java.util.HashMap;
import java.util.Map;

public class MysqlRuleManager extends AbstractRuleManager  {


    private static final Map<String, IRulePredicate> RULE_PREDICATES = new HashMap<>();

    static {
        RULE_PREDICATES.put("IF", new MysqlIfBlockPredicate(20));
        RULE_PREDICATES.put("BEGIN", new MysqlBeginBlockPredicate(1));
        RULE_PREDICATES.put("TRIGGER", new MysqlCreateTriggerBlockPredicate(50));
        RULE_PREDICATES.put("FUNCTION", new MysqlCreateFunctionBlockPredicate(50));
        RULE_PREDICATES.put("PROCEDURE", new MysqlCreateProcedureBlockPredicate(50));
    }

    @Override
    public Map<String, IRulePredicate> getRules() {
        return RULE_PREDICATES;
    }


}

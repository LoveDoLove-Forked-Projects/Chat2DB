package ai.chat2db.plugin.oracle.parser.rule.manager;

import ai.chat2db.plugin.oracle.parser.rule.predicate.*;
import ai.chat2db.spi.parser.AbstractRuleManager;
import ai.chat2db.spi.IRulePredicate;

import java.util.HashMap;
import java.util.Map;

public class OracleRuleManager extends AbstractRuleManager {


    private static final Map<String, IRulePredicate> RULE_PREDICATES = new HashMap<>();

    static {
        RULE_PREDICATES.put("PACKAGE", new OracleCreatePackagePredicate(1));
        RULE_PREDICATES.put("IF", new OracleIfBlockPredicate(20));
        RULE_PREDICATES.put("FUNCTION",new OracleCreateFunctionPredicate(100));
        RULE_PREDICATES.put("PROCEDURE", new OracleCreateProcedurePredicate(100));
        RULE_PREDICATES.put(("BEGIN"),new OracleBeginBlockPredicate(1));
    }


    @Override
    public Map<String, IRulePredicate> getRules() {
        return RULE_PREDICATES;
    }


}

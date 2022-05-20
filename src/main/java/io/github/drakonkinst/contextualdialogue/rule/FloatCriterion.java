package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

public interface FloatCriterion extends Criterion {
    boolean compare(final float value);

    default boolean evaluate(String key, String table, Map<String, ContextTable> contexts) {
        ContextTable matchingTable = SpeechQuery.getMatchingTableFromMap(key, table, contexts);
        String displayKey = ContextTable.getDisplayKey(key, table);
        if(matchingTable == null || !matchingTable.contains(key)) {
            MyLogger.finest("FAIL: No matching context for " + displayKey);
            return false;
        }
        float valueToCompare = matchingTable.get(key);
        boolean passed = compare(valueToCompare);

        if(passed) {
            MyLogger.finest("PASS: Key " + displayKey + " = " + valueToCompare + " is " + this);
        } else {
            MyLogger.finest("FAIL: Key " + displayKey + " = " + valueToCompare + " is not " + this);
        }
        return passed;
    }
}

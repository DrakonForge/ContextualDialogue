package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Map;

public interface ListCriterion extends Criterion {
    boolean compare(final IntSet set);

    default boolean evaluate(String key, String table, Map<String, ContextTable> contexts) {
        ContextTable matchingTable = SpeechQuery.getMatchingTableFromMap(key, table, contexts, FactType.LIST);
        String displayKey = ContextTable.getDisplayKey(key, table);
        if(matchingTable == null || !matchingTable.contains(key)) {
            MyLogger.finest("FAIL: No matching context for " + displayKey);
            return false;
        }
        IntSet set = matchingTable.getAsList(key);
        boolean passed = compare(set);

        if(passed) {
            MyLogger.finest("PASS: Key " + displayKey + " = " + set + " is " + this);
        } else {
            MyLogger.finest("FAIL: Key " + displayKey + " = " + set + " is not " + this);
        }
        return passed;
    }
}

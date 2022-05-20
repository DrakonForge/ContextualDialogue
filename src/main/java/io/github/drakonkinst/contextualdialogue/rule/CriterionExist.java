package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

public class CriterionExist implements Criterion {
    private static final CriterionExist EXISTS = new CriterionExist(false);
    private static final CriterionExist NOT_EXISTS = new CriterionExist(true);

    private final boolean inverted;

    public static CriterionExist exists(final boolean inverted) {
        if(inverted) {
            return NOT_EXISTS;
        }
        return EXISTS;
    }

    private CriterionExist(final boolean inverted) {
        this.inverted = inverted;
    }

    public boolean evaluate(String key, String table, Map<String, ContextTable> contexts) {
        ContextTable matchingTable = SpeechQuery.getMatchingTableFromMap(key, table, contexts);
        String displayKey = ContextTable.getDisplayKey(key, table);
        boolean passed = (matchingTable != null && matchingTable.contains(key)) != inverted;
        if(passed) {
            MyLogger.finest("PASS: Key " + displayKey + " succeeded exists=" + (!inverted));
        } else {
            MyLogger.finest("FAIL: Key " + displayKey + " failed exists=" + (!inverted));
        }
        return passed;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    public String toString() {
        if(inverted) {
            return "does not exist";
        }
        return "exists";
    }
}

package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

/**
 * A context action that inverts a boolean context.
 */
public class InvertAction extends ContextAction {
    public InvertAction(String tableName, String fieldName) {
        super(tableName, fieldName);
    }

    @Override
    public void perform(Map<String, ContextTable> contexts) {
        ContextTable table = SpeechQuery.getMatchingOrFirstAvailable(fieldName, tableName, contexts);
        if(table == null) {
            MyLogger.warning("Failed to find a table for table=" + tableName + ", field=" + fieldName);
            return;
        }

        if(!table.isBoolean(fieldName)) {
            MyLogger.warning("Invert operation only works on booleans!");
            return;
        }

        table.set(fieldName, !table.getAsBoolean(fieldName));
    }
}

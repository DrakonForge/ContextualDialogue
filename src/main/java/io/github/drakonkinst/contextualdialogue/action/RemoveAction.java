package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

/**
 * A context action that removes a context.
 */
public class RemoveAction extends ContextAction {
    public RemoveAction(String tableName, String fieldName) {
        super(tableName, fieldName);
    }

    @Override
    public void perform(Map<String, ContextTable> contexts) {
        ContextTable table = SpeechQuery.getMatchingOrFirstAvailable(fieldName, tableName, contexts);
        if(table == null) {
            MyLogger.warning("Failed to find a table for table=" + tableName + ", field=" + fieldName);
            return;
        }

        table.remove(fieldName);
    }
}

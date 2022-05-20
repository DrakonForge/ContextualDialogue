package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Map;

/**
 * A context action that sets a context to a specific value.
 */
public class SetListAction extends ContextAction {
    private final IntSet value;

    public SetListAction(String tableName, String fieldName, IntSet value) {
        super(tableName, fieldName);
        this.value = value;
    }

    @Override
    public void perform(Map<String, ContextTable> contexts) {
        ContextTable table = SpeechQuery.getMatchingOrFirstAvailable(fieldName, tableName, contexts);
        if(table == null) {
            MyLogger.warning("Failed to find a table for table=" + tableName + ", field=" + fieldName);
            return;
        }

        FactType valueType = table.getType(fieldName);
        if(valueType == FactType.NULL || valueType == FactType.LIST) {
            table.set(fieldName, new IntOpenHashSet(value));
        } else {
            MyLogger.warning("Type mismatch: Cannot set context of type " + valueType.name() + " to list for \"" + fieldName + "\"");
        }
    }
}

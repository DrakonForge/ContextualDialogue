package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

/**
 * A context action that sets a context to a specific value.
 */
public class SetStaticAction extends ContextAction {
    private final float value;
    private final FactType type;

    public SetStaticAction(String tableName, String fieldName, float value, FactType type) {
        super(tableName, fieldName);
        this.value = value;

        if(type == FactType.NULL) {
            throw new IllegalArgumentException("Cannot set to null type");
        }
        this.type = type;
    }

    @Override
    public void perform(Map<String, ContextTable> contexts) {
        ContextTable table = SpeechQuery.getMatchingOrFirstAvailable(fieldName, tableName, contexts);
        if(table == null) {
            MyLogger.warning("Failed to find a table for table=" + tableName + ", field=" + fieldName);
            return;
        }

        FactType valueType = table.getType(fieldName);
        if(valueType == FactType.NULL || valueType == type) {
            table.set(fieldName, value, type);
        } else {
            MyLogger.warning("Type mismatch: Cannot set context of type " + valueType.name() + " to " + type.name() + " for \"" + fieldName + "\"");
        }
    }
}

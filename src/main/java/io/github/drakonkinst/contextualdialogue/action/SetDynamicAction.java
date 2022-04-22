package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

/**
 * A context action that sets a context equal to another context.
 */
public class SetDynamicAction extends ContextAction {
    private final String otherTableName;
    private final String otherFieldName;

    public SetDynamicAction(String tableName, String fieldName, String otherTableName, String otherFieldName) {
        super(tableName, fieldName);
        this.otherTableName = otherTableName;
        this.otherFieldName = otherFieldName;
    }

    @Override
    public void perform(Map<String, ContextTable> contexts) {
        ContextTable table = SpeechQuery.getMatchingOrFirstAvailable(fieldName, tableName, contexts);
        if(table == null) {
            MyLogger.warning("Failed to find a table for table=" + tableName + ", field=" + fieldName);
            return;
        }

        ContextTable otherTable = SpeechQuery.getMatchingTableFromMap(otherFieldName, otherTableName, contexts);
        if(otherTable == null) {
            MyLogger.warning("Failed to find a table for \"" + otherFieldName + "\"");
            return;
        }

        FactType valueType = otherTable.getType(otherFieldName);
        if(valueType == FactType.NULL) {
            MyLogger.warning("Cannot set value to null");
            return;
        }
        if(!table.contains(fieldName) || table.getType(fieldName) == valueType) {
            table.set(fieldName, otherTable.get(fieldName), valueType);
        } else {
            MyLogger.warning("Value mismatch from \"" + fieldName + "\" to \"" + otherFieldName + "\"");
        }
    }
}

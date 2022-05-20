package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

/**
 * A context action that performs addition or multiplication on a numeric context.
 */
public class ArithmeticAction extends ContextAction {
    private final float modifier;   // The value to add or multiply by
    private final boolean isAdd;    // Whether the operation is ADD or MULTIPLY

    public ArithmeticAction(final String tableName, final String fieldName, final float modifier, final boolean isAdd) {
        super(tableName, fieldName);
        this.modifier = modifier;
        this.isAdd = isAdd;
    }

    /**
     * Initializes the context field to 0.0 if it does not exist.
     * Then performs addition or multiplication on the value by
     * some constant value.
     * Fails if the field exists but is not numeric.
     *
     * @param contexts The available context tables.
     */
    @Override
    public void perform(Map<String, ContextTable> contexts) {
        ContextTable table = SpeechQuery.getMatchingOrFirstAvailable(fieldName, tableName, contexts);

        if(table == null) {
            MyLogger.warning("Failed to find a table for table=" + tableName + ", field=" + fieldName);
            return;
        }

        if(!table.contains(fieldName)) {
            table.set(fieldName, 0.0f);
        }

        if(!table.isNumber(fieldName)) {
            MyLogger.warning("Arithmetic operations only work on numerical fields!");
            return;
        }

        float value = table.getAsNumber(fieldName);
        float newValue;
        if(isAdd) {
            newValue = value + modifier;
        } else {
            newValue = value * modifier;
        }
        table.set(fieldName, newValue);
    }
}

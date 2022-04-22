package io.github.drakonkinst.contextualdialogue.action;

/**
 * Represents an action that manipulates a specific context field.
 */
public abstract class ContextAction implements Action {
    protected final String tableName;   // May be null to select the matching or first available table.
    protected final String fieldName;

    public ContextAction(String tableName, String fieldName) {
        this.tableName = tableName;
        this.fieldName = fieldName;
    }
}

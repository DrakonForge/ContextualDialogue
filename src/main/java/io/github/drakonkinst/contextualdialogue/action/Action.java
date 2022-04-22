package io.github.drakonkinst.contextualdialogue.action;

import io.github.drakonkinst.contextualdialogue.context.ContextTable;

import java.io.Serializable;
import java.util.Map;

public interface Action extends Serializable {
    /**
     * Performs an action. Actions fail silently, and
     * should not throw errors even if it is unable
     * to perform the action.
     *
     * @param contexts The available context tables.
     */
    void perform(Map<String, ContextTable> contexts);
}

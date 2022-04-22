package io.github.drakonkinst.contextualdialogue.rule;

import it.unimi.dsi.fastutil.ints.IntSet;

public class CriterionEmpty implements ListCriterion {
    private static final CriterionEmpty EMPTY = new CriterionEmpty(false);
    private static final CriterionEmpty NOT_EMPTY = new CriterionEmpty(true);

    private final boolean inverted;

    public static CriterionEmpty empty(final boolean inverted) {
        if(inverted) {
            return EMPTY;
        }
        return NOT_EMPTY;
    }

    private CriterionEmpty(final boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String toString() {
        if(inverted) {
            return "is not empty";
        }
        return "exists";
    }

    @Override
    public boolean compare(IntSet set) {
        return inverted != set.isEmpty();
    }
}

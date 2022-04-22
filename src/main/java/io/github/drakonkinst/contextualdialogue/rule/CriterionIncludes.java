package io.github.drakonkinst.contextualdialogue.rule;

import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;

public class CriterionIncludes implements ListCriterion {
    private final int[] values;
    private final boolean inverted;

    public static CriterionIncludes of(final int[] values, final boolean inverted) {
        return new CriterionIncludes(values, inverted);
    }

    private CriterionIncludes(final int[] values, final boolean inverted) {
        this.values = values;
        this.inverted = inverted;
    }

    public boolean compare(final IntSet set) {
        boolean included = false;
        for(int value : values) {
            if(set.contains(value)) {
                included = true;
                break;
            }
        }
        return inverted != included;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String toString() {
        String valStr;
        if(values.length == 1) {
            valStr = values[0] + "";
        } else {
            valStr = Arrays.toString(values);
        }

        if(inverted) {
            return "does not include " + valStr;
        }
        return "includes " + valStr;
    }
}
package io.github.drakonkinst.contextualdialogue.rule;

import java.util.Arrays;

public class CriterionAlternate implements FloatCriterion {
    public static CriterionAlternate of(final int[] options, final boolean inverse) {
        return new CriterionAlternate(options, inverse);
    }

    private final int[] options;
    private final boolean inverted;

    private CriterionAlternate(final int[] options, final boolean inverted) {
        this.options = options;
        this.inverted = inverted;
    }

    @Override
    public boolean compare(final float value) {
        for(int option : options) {
            if(option == value) {
                return !inverted;
            }
        }
        return inverted;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String toString() {
        if(inverted) {
            return "not one of " + Arrays.toString(options);
        }
        return "one of " + Arrays.toString(options);
    }
}

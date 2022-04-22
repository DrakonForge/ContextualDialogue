package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.util.StringCache;

public class CriterionStatic implements FloatCriterion {
    public static final float EPSILON = 0.000001f;

    public static CriterionStatic equals(final boolean flag) {
        return equals(flag, false);
    }

    public static CriterionStatic equals(final String value) {
        return equals(value, false);
    }

    public static CriterionStatic equals(final float value) {
        return equals(value, false);
    }

    public static CriterionStatic equals(final boolean flag, final boolean inverse) {
        return new CriterionStatic(ContextTable.fromBoolean(flag), inverse);
    }

    public static CriterionStatic equals(final String value, final boolean inverse) {
        return new CriterionStatic(StringCache.cacheString(value), inverse);
    }

    public static CriterionStatic equals(final float value, final boolean inverse) {
        return new CriterionStatic(value, inverse);
    }

    public static CriterionStatic between(final float minValue, final float maxValue, final boolean inverse) {
        return new CriterionStatic(minValue, maxValue, inverse);
    }

    public static CriterionStatic min(final float minValue, final boolean inverse) {
        return new CriterionStatic(minValue, Float.MAX_VALUE, inverse);
    }

    public static CriterionStatic max(final float maxValue, final boolean inverse) {
        return new CriterionStatic(-Float.MAX_VALUE, maxValue, inverse);
    }

    private final float minValue;
    private final float maxValue;
    private final boolean inverted;

    private CriterionStatic(final float value, final boolean inverted) {
        this.minValue = value;
        this.maxValue = value;
        this.inverted = inverted;
    }

    private CriterionStatic(final float minValue, final float maxValue, final boolean inverted) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.inverted = inverted;
    }

    @Override
    public boolean compare(final float value) {
        return inverted != (minValue <= value + EPSILON && value - EPSILON <= maxValue);
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String toString() {
        // This is way fancier than it deserves to be lol
        if(minValue == maxValue) {
            if(inverted) {
                return "!= " + getDisplay(minValue);
            }
            return "= " + getDisplay(minValue);
        }

        if(inverted) {
            return "not between " + getDisplay(minValue) + " and " + getDisplay(maxValue);
        }
        return "between " + getDisplay(minValue) + " and " + getDisplay(maxValue);
    }

    private static String getDisplay(float value) {
        if(value == -Float.MAX_VALUE) {
            return "-Infinity";
        }
        if(value == Float.MAX_VALUE) {
            return "Infinity";
        }
        if(value % 1 == 0) {
            return "" + ((int) value);
        }
        return "" + value;
    }
}


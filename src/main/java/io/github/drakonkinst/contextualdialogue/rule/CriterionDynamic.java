package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.Map;

public class CriterionDynamic implements Criterion {
    public enum ComparisonType {
        EQUALS,
        LESS_THAN,  // inverted -> GREATER_EQUAL
        LESS_EQUAL  // inverted -> GREATER_THAN
    }

    public static CriterionDynamic equals(final String otherTable, final String otherKey, final boolean inverted) {
        return new CriterionDynamic(otherTable, otherKey, ComparisonType.EQUALS, inverted);
    }

    public static CriterionDynamic lessThan(final String otherTable, final String otherKey, final boolean inverted) {
        return new CriterionDynamic(otherTable, otherKey, ComparisonType.LESS_THAN, inverted);
    }

    public static CriterionDynamic lessEqual(final String otherTable, final String otherKey, final boolean inverted) {
        return new CriterionDynamic(otherTable, otherKey, ComparisonType.LESS_EQUAL, inverted);
    }

    public static CriterionDynamic greaterThan(final String otherTable, final String otherKey, final boolean inverted) {
        return new CriterionDynamic(otherTable, otherKey, ComparisonType.LESS_EQUAL, !inverted);
    }

    public static CriterionDynamic greaterEqual(final String otherTable, final String otherKey, final boolean inverted) {
        return new CriterionDynamic(otherTable, otherKey, ComparisonType.LESS_THAN, !inverted);
    }

    // First key/table is stored in the CriterionTuple
    private final String otherTable;
    private final String otherKey;
    private final ComparisonType comparisonType;
    private final boolean inverted;

    private CriterionDynamic(final String otherTable,
                             final String otherKey,
                             final ComparisonType comparisonType,
                             final boolean inverted) {
        this.otherTable = otherTable;
        this.otherKey = otherKey;
        this.comparisonType = comparisonType;
        this.inverted = inverted;
    }

    public boolean compare(final float value1, final float value2) {
        final boolean equals = Math.abs(value1 - value2) <= CriterionStatic.EPSILON;

        if(comparisonType == ComparisonType.EQUALS) {
            return inverted != equals;
        }

        final boolean lessEqual = value1 <= value2 + CriterionStatic.EPSILON;

        if(comparisonType == ComparisonType.LESS_EQUAL) {
            return inverted != (equals || lessEqual);
        }

        if(comparisonType == ComparisonType.LESS_THAN) {
            return inverted != (!equals && lessEqual);
        }

        throw new IllegalStateException("Invalid comparison type");
    }

    public boolean evaluate(String key, String table, Map<String, ContextTable> contexts) {
        ContextTable matching = SpeechQuery.getMatchingTableFromMap(key, table, contexts);
        ContextTable otherMatching = SpeechQuery.getMatchingTableFromMap(otherKey, otherTable, contexts);
        String displayKey = ContextTable.getDisplayKey(key, table);
        String otherDisplayKey = ContextTable.getDisplayKey(otherKey, otherTable);

        if(matching == null || !matching.contains(key)) {
            MyLogger.finest("FAIL: No matching context for " + displayKey);
            return false;
        }

        if(otherMatching == null || !otherMatching.contains(otherKey)) {
            MyLogger.finest("FAIL: No matching context for " + otherDisplayKey);
            return false;
        }

        float value1 = matching.get(key);
        float value2 = otherMatching.get(key);
        boolean passed = compare(value1, value2);

        if(passed) {
            MyLogger.finest("PASS: Dynamic comparison succeeded for " + displayKey + " = " + value1 + ", " + otherDisplayKey + " = " + value2);
        } else {
            MyLogger.finest("FAIL: Dynamic comparison failed for " + displayKey + " = " + value1 + ", " + otherDisplayKey + " = " + value2);
        }
        return passed;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String toString() {
        final String operation;
        if(inverted) {
            if(comparisonType == ComparisonType.EQUALS) {
                operation = "!=";
            } else if(comparisonType == ComparisonType.LESS_EQUAL) {
                operation = ">";
            } else {
                operation = ">=";
            }
        } else {
            if(comparisonType == ComparisonType.EQUALS) {
                operation = "=";
            } else if(comparisonType == ComparisonType.LESS_EQUAL) {
                operation = "<=";
            } else {
                operation = "<";
            }
        }

        return operation + " " + otherTable + "." + otherKey;
    }
}

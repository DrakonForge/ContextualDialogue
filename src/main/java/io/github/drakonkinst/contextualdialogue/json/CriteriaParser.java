package io.github.drakonkinst.contextualdialogue.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.github.drakonkinst.commonutil.JsonUtils;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.rule.Criterion;
import io.github.drakonkinst.contextualdialogue.rule.CriterionDummy;
import io.github.drakonkinst.contextualdialogue.rule.CriterionDynamic;
import io.github.drakonkinst.contextualdialogue.rule.CriterionEmpty;
import io.github.drakonkinst.contextualdialogue.rule.CriterionExist;
import io.github.drakonkinst.contextualdialogue.rule.CriterionFail;
import io.github.drakonkinst.contextualdialogue.rule.CriterionIncludes;
import io.github.drakonkinst.contextualdialogue.rule.CriterionAlternate;
import io.github.drakonkinst.contextualdialogue.rule.CriterionStatic;
import io.github.drakonkinst.contextualdialogue.rule.CriterionTuple;
import io.github.drakonkinst.contextualdialogue.rule.Rule;
import io.github.drakonkinst.contextualdialogue.util.StringCache;

import java.util.List;
import java.util.Set;

/**
 * Provides methods to parse criteria in Contextual Dialogue.
 */
public final class CriteriaParser {
    public static final String KEY_DUMMY = "dummy";
    public static final String KEY_FAIL = "fail";

    private static final String TYPE_EQUALS = "equals";
    private static final String TYPE_DUMMY = "dummy";
    private static final String TYPE_FAIL = "fail";
    private static final String TYPE_RANGE = "range";
    private static final String TYPE_MIN = "min";
    private static final String TYPE_MAX = "max";
    private static final String TYPE_EXISTS = "exists";
    private static final String TYPE_INCLUDES = "includes";
    private static final String TYPE_EMPTY = "empty";
    private static final String TYPE_EQUALS_DYNAMIC = "equals_dynamic";
    private static final String TYPE_LESS_THAN_DYNAMIC = "less_than_dynamic";
    private static final String TYPE_LESS_EQUAL_DYNAMIC = "less_equal_dynamic";
    private static final String TYPE_GREATER_THAN_DYNAMIC = "greater_than_dynamic";
    private static final String TYPE_GREATER_EQUAL_DYNAMIC = "greater_equal_dynamic";
    private static final Set<String> TYPES_DYNAMIC = Set.of(
            TYPE_EQUALS_DYNAMIC,
            TYPE_LESS_THAN_DYNAMIC,
            TYPE_LESS_EQUAL_DYNAMIC,
            TYPE_GREATER_THAN_DYNAMIC,
            TYPE_GREATER_EQUAL_DYNAMIC);

    private CriteriaParser() {}

    // Parses a list of criteria JSON into a Rule object
    public static Rule parseRule(final JsonArray array, final List<CriterionTuple> presetRules) {
        // Calculate number of criteria
        int size = presetRules.size();
        if(array != null) {
            size += array.size();
        }

        // Return empty rule if there are no criteria
        if(size <= 0) {
            return Rule.EMPTY;
        }

        final Rule.Builder builder = Rule.builder(size);

        // Add all preset rules
        for(final CriterionTuple criterion : presetRules) {
            builder.add(criterion);
        }

        if(array != null) {
            for(final JsonElement element : array) {
                // Parse criterion
                if(!element.isJsonObject()) {
                    throw new JsonParseException("Error: Rule array should only contain criterion objects");
                }

                CriterionTuple tuple = parseCriterion(element.getAsJsonObject());
                builder.add(tuple);
            }
        }

        return builder.build();
    }

    // Parse criterion JSON
    private static CriterionTuple parseCriterion(final JsonObject criterionObj) {
        final String table = JsonUtils.getNullableString(criterionObj.get("table"));
        final String type = criterionObj.get("type").getAsString();
        final String field = validateField(type, criterionObj.get("field"));
        final JsonElement nullableValue = criterionObj.get("value");
        final JsonElement nullableInverse = criterionObj.get("inverse");

        boolean inverse = false;
        if(nullableInverse != null) {
            inverse = nullableInverse.getAsBoolean();
        }

        final Criterion criterion = buildCriterion(criterionObj, type, nullableValue, inverse);
        return new CriterionTuple(field, table, criterion);
    }

    private static String validateField(String type, JsonElement nullableField) {
        if(nullableField != null) {
            String field = nullableField.getAsString();
            if(field.equals(KEY_DUMMY)) {
                throw new JsonParseException("Error: dummy is a reserved field, leave dummy-type fields blank");
            }
            if(field.equals(KEY_FAIL)) {
                throw new JsonParseException("Error: fail is a reserved field, leave fail-type fields blank");
            }
            return field;
        } else {
            if(type.equals(TYPE_DUMMY)) {
                return KEY_DUMMY;
            }
            if(type.equals(TYPE_FAIL)) {
                return KEY_FAIL;
            }
            throw new JsonParseException("Error: Missing field name");
        }
    }

    // Builds a Criterion object depending on type
    public static Criterion buildCriterion(final JsonObject criterionObj,
                                           final String type,
                                           final JsonElement nullableValue,
                                           final boolean inverse) {
        if(TYPES_DYNAMIC.contains(type)) {
            return buildDynamicCriterion(criterionObj, type, inverse);
        }

        return switch (type) {
            case TYPE_EQUALS -> buildEqualsCriterion(nullableValue, inverse);
            case TYPE_MIN -> CriterionStatic.min(nullableValue.getAsFloat(), inverse);
            case TYPE_MAX -> CriterionStatic.max(nullableValue.getAsFloat(), inverse);
            case TYPE_RANGE -> buildRangeCriterion(nullableValue, inverse);
            case TYPE_EXISTS -> CriterionExist.exists(inverse);
            case TYPE_DUMMY -> CriterionDummy.of(nullableValue.getAsInt());
            case TYPE_FAIL -> CriterionFail.withChance(nullableValue.getAsFloat());
            case TYPE_INCLUDES -> buildIncludesCriterion(nullableValue.getAsJsonArray(), inverse);
            case TYPE_EMPTY -> CriterionEmpty.empty(inverse);
            default -> throw new JsonParseException("Error: Invalid criterion type \"" + type + "\"");
        };
    }

    private static Criterion buildEqualsCriterion(final JsonElement nullableValue, final boolean inverse) {
        // Multiple case
        if(nullableValue.isJsonArray()) {
            final JsonArray array = nullableValue.getAsJsonArray();
            final int[] result = new int[array.size()];
            for(int i = 0; i < array.size(); ++i) {
                final JsonPrimitive primitiveEl = array.get(i).getAsJsonPrimitive();
                if(primitiveEl.isString()) {
                    result[i] = StringCache.cacheString(primitiveEl.getAsString());
                } else if(primitiveEl.isNumber()) {
                    result[i] = primitiveEl.getAsInt();
                } else if(primitiveEl.getAsBoolean()) {
                    result[i] = ContextTable.fromBoolean(primitiveEl.getAsBoolean());
                } else {
                    throw new JsonParseException("Error: List can only include integers, booleans, or strings, found \"" + primitiveEl + "\"");
                }
            }
            return CriterionAlternate.of(result, inverse);
        }

        final JsonPrimitive value = nullableValue.getAsJsonPrimitive();
        if(value.isBoolean()) {
            return CriterionStatic.equals(value.getAsBoolean(), inverse);
        }
        if(value.isNumber()) {
            // Only int is supported for equals case
            return CriterionStatic.equals(value.getAsInt(), inverse);
        }
        if(value.isString()) {
            return CriterionStatic.equals(value.getAsString(), inverse);
        }
        throw new JsonParseException("Error: Unknown primitive value type");
    }

    private static CriterionStatic buildRangeCriterion(final JsonElement nullableValue, final boolean inverse) {
        final JsonArray rangeArr = nullableValue.getAsJsonArray();
        if(rangeArr.size() != 2) {
            throw new JsonParseException("Error: Range array must have exactly two entries");
        }
        final float min = rangeArr.get(0).getAsFloat();
        final float max = rangeArr.get(1).getAsFloat();
        return CriterionStatic.between(min, max, inverse);
    }

    private static CriterionDynamic buildDynamicCriterion(final JsonObject criterionObj,
                                                          final String type,
                                                          final boolean inverse) {
        final String otherKey = criterionObj.get("other_field").getAsString();
        final String otherTable = JsonUtils.getNullableString(criterionObj.get("other_table"));

        return switch (type) {
            case TYPE_EQUALS_DYNAMIC -> CriterionDynamic.equals(otherTable, otherKey, inverse);
            case TYPE_LESS_THAN_DYNAMIC -> CriterionDynamic.lessThan(otherTable, otherKey, inverse);
            case TYPE_LESS_EQUAL_DYNAMIC -> CriterionDynamic.lessEqual(otherTable, otherKey, inverse);
            case TYPE_GREATER_THAN_DYNAMIC -> CriterionDynamic.greaterThan(otherTable, otherKey, inverse);
            case TYPE_GREATER_EQUAL_DYNAMIC -> CriterionDynamic.greaterEqual(otherTable, otherKey, inverse);
            default -> throw new JsonParseException("Error: Invalid type \"" + type + "\"");
        };
    }

    private static CriterionIncludes buildIncludesCriterion(final JsonArray array, final boolean inverse) {
        int[] values = new int[array.size()];

        for(int i = 0; i < array.size(); ++i) {
            JsonPrimitive el = array.get(i).getAsJsonPrimitive();
            if(el.isString()) {
                values[i] = StringCache.cacheString(el.getAsString());
            } else if(el.isNumber()) {
                values[i] = el.getAsInt();
            } else {
                throw new JsonParseException("Error: Expected string or integer, found " + el);
            }
        }

        return CriterionIncludes.of(values, inverse);
    }
}
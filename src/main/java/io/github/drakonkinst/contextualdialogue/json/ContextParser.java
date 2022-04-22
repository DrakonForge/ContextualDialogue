package io.github.drakonkinst.contextualdialogue.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.util.StringCache;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.HashMap;
import java.util.Map;

public final class ContextParser {
    private ContextParser() {}

    public static Map<String, ContextTable> parseContexts(JsonObject obj) {
        Map<String, ContextTable> map = new HashMap<>();
        for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String contextName = entry.getKey();
            JsonObject contextObj = entry.getValue().getAsJsonObject();
            ContextTable context = parseContext(contextObj);
            map.put(contextName, context);
        }
        return map;
    }

    public static IntSet fromJsonArray(JsonArray arr) {
        IntSet set = new IntOpenHashSet(arr.size());
        for(JsonElement arrEl : arr) {
            JsonPrimitive arrPrimitive = arrEl.getAsJsonPrimitive();
            if(arrPrimitive.isString()) {
                set.add(StringCache.cacheString(arrPrimitive.getAsString()));
            } else if(arrPrimitive.isNumber()) {
                float num = arrPrimitive.getAsFloat();
                if(num == Math.round(num)) {
                    set.add((int) num);
                } else {
                    throw new JsonParseException("List context can only contain integers or strings");
                }
            } else {
                throw new JsonParseException("List context can only contain integers or strings");
            }
        }
        return set;
    }

    private static ContextTable parseContext(JsonObject obj) {
        ContextTable table = new ContextTable();
        for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            JsonElement el = entry.getValue();

            if(el.isJsonPrimitive()) {
                JsonPrimitive primitive = el.getAsJsonPrimitive();
                if(primitive.isNumber()) {
                    table.set(key, primitive.getAsFloat());
                } else if(primitive.isString()) {
                    table.set(key, primitive.getAsString());
                } else if(primitive.isBoolean()) {
                    table.set(key, primitive.getAsBoolean());
                } else {
                    throw new JsonParseException("Unknown primitive " + primitive);
                }
            } else if(el.isJsonArray()) {
                JsonArray arr = el.getAsJsonArray();
                IntSet set = fromJsonArray(arr);
                table.set(key, set);
            } else {
                throw new JsonParseException("Value " + el + " is not supported as context");
            }
        }
        return table;
    }
}

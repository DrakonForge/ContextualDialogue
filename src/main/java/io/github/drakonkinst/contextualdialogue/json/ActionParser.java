package io.github.drakonkinst.contextualdialogue.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.github.drakonkinst.contextualdialogue.action.Action;
import io.github.drakonkinst.contextualdialogue.action.ArithmeticAction;
import io.github.drakonkinst.contextualdialogue.action.InvertAction;
import io.github.drakonkinst.contextualdialogue.action.RemoveAction;
import io.github.drakonkinst.contextualdialogue.action.SetDynamicAction;
import io.github.drakonkinst.contextualdialogue.action.SetListAction;
import io.github.drakonkinst.contextualdialogue.action.SetStaticAction;
import io.github.drakonkinst.contextualdialogue.commonutil.JsonUtils;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.util.StringCache;

public final class ActionParser {
    private ActionParser() {}

    public static Action[] parseActions(JsonArray arr) {
        Action[] actions = new Action[arr.size()];
        for(int i = 0; i < arr.size(); ++i) {
            actions[i] = parseAction(arr.get(i).getAsJsonObject());
        }
        return actions;
    }

    private static Action parseAction(JsonObject obj) {
        String op = obj.get("op").getAsString();
        JsonElement contextEl = obj.get("context");
        JsonElement valueEl = obj.get("value");
        String table = null;
        String key = null;
        if(contextEl != null) {
            JsonObject contextObj = contextEl.getAsJsonObject();
            key = contextObj.get("context").getAsString();
            table = JsonUtils.getNullableString(contextObj.get("table"));
        }
        switch(op) {
            case "add" -> {
                return new ArithmeticAction(table, key, valueEl.getAsFloat(), true);
            }
            case "mult" -> {
                return new ArithmeticAction(table, key, valueEl.getAsFloat(), false);
            }
            case "remove" -> {
                return new RemoveAction(table, key);
            }
            case "invert" -> {
                return new InvertAction(table, key);
            }
            case "set_list" -> {
                return new SetListAction(table, key, ContextParser.fromJsonArray(valueEl.getAsJsonArray()));
            }
            case "set_static" -> {
                JsonPrimitive valPrimitive = valueEl.getAsJsonPrimitive();
                if(valPrimitive.isString()) {
                    return new SetStaticAction(table, key,
                            StringCache.cacheString(valPrimitive.getAsString()), FactType.STRING);
                } else if(valPrimitive.isNumber()) {
                    return new SetStaticAction(table, key,
                            valPrimitive.getAsFloat(), FactType.NUMBER);
                } else if(valPrimitive.isBoolean()) {
                    return new SetStaticAction(table, key,
                            ContextTable.fromBoolean(valPrimitive.getAsBoolean()), FactType.BOOLEAN);
                }
                throw new JsonParseException("Unknown primitive " + valPrimitive);
            }
            case "set_dynamic" -> {
                JsonObject valueObj = valueEl.getAsJsonObject();
                String otherKey = valueObj.get("context").getAsString();
                String otherTable = JsonUtils.getNullableString(valueObj.get("table"));
                return new SetDynamicAction(table, key, otherTable, otherKey);
            }
            default -> {
                throw new JsonParseException("Unknown operation " + op);
            }
        }
    }
}

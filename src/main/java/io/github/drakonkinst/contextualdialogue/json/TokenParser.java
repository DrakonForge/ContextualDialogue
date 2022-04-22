package io.github.drakonkinst.contextualdialogue.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenBoolean;
import io.github.drakonkinst.contextualdialogue.token.TokenContext;
import io.github.drakonkinst.contextualdialogue.token.TokenFloat;
import io.github.drakonkinst.contextualdialogue.token.TokenFunction;
import io.github.drakonkinst.contextualdialogue.token.TokenInt;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import io.github.drakonkinst.contextualdialogue.token.TokenString;
import io.github.drakonkinst.contextualdialogue.token.TokenSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class TokenParser {
    private TokenParser() {}

    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.[0-9]+");
    private static final String SYMBOL_START = "@";

    public static Token parseToken(JsonElement element) {
        if(element.isJsonPrimitive()) {
            JsonPrimitive el = element.getAsJsonPrimitive();
            return parsePrimitive(el);
        } else if(element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            return parseList(arr);
        } else if(element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            return parseObj(obj);
        }
        throw new JsonParseException("Token not recognized");
    }

    private static Token parsePrimitive(JsonPrimitive el) {
        if(el.isBoolean()) {
            return new TokenBoolean(el.getAsBoolean());
        }
        if(el.isString()) {
            String str = el.getAsString();
            if(str.startsWith(SYMBOL_START)) {
                return new TokenSymbol(str.substring(1));
            }
            return new TokenString(el.getAsString());
        }
        if(isFloat(el)) {
            return new TokenFloat(el.getAsFloat());
        }
        if(el.isNumber()) {
            return new TokenInt(el.getAsInt());
        }
        throw new JsonParseException("Primitive token not recognized");
    }

    private static Token parseObj(JsonObject obj) {
        if(obj.has("function")) {
            return parseFunction(obj);
        }
        if(obj.has("context")) {
            return parseContext(obj);
        }
        throw new JsonParseException("Object token not recognized");
    }

    private static TokenFunction parseFunction(JsonObject obj) {
        String functionName = obj.get("function").getAsString();

        List<Token> argTokens = new ArrayList<>();
        if(obj.has("args")) {
            JsonArray argsArr = obj.getAsJsonArray("args");
            for(JsonElement el : argsArr) {
                argTokens.add(parseToken(el));
            }
        }
        return new TokenFunction(functionName, argTokens);
    }

    private static TokenContext parseContext(JsonObject obj) {
        String contextName = obj.get("context").getAsString();
        String tableName = null;

        if(obj.has("table")) {
            tableName = obj.get("table").getAsString();
        }
        return new TokenContext(tableName, contextName);
    }

    private static TokenList parseList(JsonArray arr) {
        List<Token> tokenList = new ArrayList<>();
        for(JsonElement el : arr) {
            tokenList.add(parseToken(el));
        }
        return new TokenList(tokenList);
    }

    private static boolean isFloat(JsonElement element) {
        String num = element.getAsString();
        return FLOAT_PATTERN.matcher(num).matches();
    }
}

package io.github.drakonkinst.contextualdialogue.commonutil;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class JsonUtils {
    public static final String JSON_EXTENSION = ".json";

    public static String removeExtension(final String jsonFile) {
        return jsonFile.substring(0, jsonFile.length() - JSON_EXTENSION.length());
    }

    public static JsonElement readInternalFile(final String filePath) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(FileUtils.getResourceStream(filePath), StandardCharsets.UTF_8)));
    }

    public static JsonElement readExternalFile(final String filePath) throws FileNotFoundException {
        return JsonParser.parseReader(new BufferedReader(new FileReader(filePath)));
    }

    public static JsonElement readFile(final InputStream is) {
        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
    }

    public static String getNullableString(JsonElement element) {
        if(element != null) {
            return element.getAsString();
        }
        return null;
    }
}

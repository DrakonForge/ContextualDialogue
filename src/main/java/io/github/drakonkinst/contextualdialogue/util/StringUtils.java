package io.github.drakonkinst.contextualdialogue.util;

public final class StringUtils {
    private StringUtils() {}
    
    public static String snakeToCamel(String s) {
        String[] parts = s.split("_");
        String camelCaseString = parts[0];
        for (int i = 1; i < parts.length; ++i) {
            camelCaseString = camelCaseString + toProperCase(parts[i]);
        }
        return camelCaseString;
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1);
    }
}

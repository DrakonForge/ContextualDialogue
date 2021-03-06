package io.github.drakonkinst.contextualdialogue.util;


import io.github.drakonkinst.commonutil.MyLogger;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

// String-symbol bidirectional cache
public final class StringCache {
    public static final int NULL = 0;
    public static final int INITIAL_ID = 9000;

    private static final Object2IntMap<String> CACHE = new Object2IntOpenHashMap<>();
    private static final Int2ObjectMap<String> LOOKUP = new Int2ObjectOpenHashMap<>();
    private static int nextId = INITIAL_ID - 1;
    private static int cacheId = 0;

    private StringCache() {}

    public static int cacheString(final String str) {
        if(str == null) {
            return NULL;
        }
        if(CACHE.containsKey(str)) {
            return CACHE.getInt(str);
        }
        CACHE.put(str, ++nextId);
        LOOKUP.put(nextId, str);
        return nextId;
    }

    public static String lookup(final int id) {
        if(id == NULL) {
            MyLogger.warning("StringCache failed to string value for symbol " + id);
            return null;
        }
        return LOOKUP.get(id);
    }

    // Dangerous!
    public static void resetCache() {
        CACHE.clear();
        LOOKUP.clear();
        ++cacheId;
    }

    public static int getSize() {
        return CACHE.size();
    }

    public static int getCacheId() {
        return cacheId;
    }
}

package io.github.drakonkinst.contextualdialogue.context;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.util.StringCache;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the context for an arbitrary object.
 * Context is stored as key-value pairs.
 */
public class ContextTable implements Serializable {
    private static final int TRUE = 1;
    private static final int FALSE = 0;
    private static final float DEFAULT_VALUE = 0.0f;

    public static int fromBoolean(final boolean flag) {
        if(flag) {
            return TRUE;
        }
        return FALSE;
    }

    public static String getDisplayKey(String key, String table) {
        if(table == null) {
            return key;
        }
        return table + '.' + key;
    }

    /**
     * Caches all given Strings into an IntSet.
     *
     * @param collection A collection of Strings.
     * @return An IntSet containing the cached Strings.
     */
    public static IntSet setOf(final Collection<String> collection) {
        final IntSet intSet = new IntOpenHashSet(collection.size());
        for(final String str : collection) {
            intSet.add(StringCache.cacheString(str));
        }
        return intSet;
    }

    /**
     * Caches all given Strings into an IntSet.
     *
     * @param array An array of Strings.
     * @return An IntSet containing the cached Strings.
     */
    public static IntSet setOf(final String... array) {
        final IntSet intSet = new IntOpenHashSet(array.length);
        for(final String str : array) {
            intSet.add(StringCache.cacheString(str));
        }
        return intSet;
    }

    private final Map<String, FactTuple> dictionary = new HashMap<>();
    private final int cacheId;
    private Map<String, IntSet> lists = null; // Null to allow for lazy instantiation

    public ContextTable() {
        cacheId = StringCache.getCacheId();
    }

    /**
     * Returns the value stored in this key as a raw float,
     * regardless of the type.
     * Throws IllegalArgumentException if the value is null
     * or is a list.
     *
     * @param key The key to search.
     * @return The raw float value at the key.
     */
    public float get(final String key) {
        final FactTuple factTuple = dictionary.get(key);
        if(factTuple == null) {
            throw new IllegalArgumentException("Error: \"" + key + "\" does not exist!");
        }
        if(factTuple.type == FactType.LIST) {
            throw new IllegalArgumentException("Error: \"" + key + "\" is a list, value cannot be used!");
        }
        return factTuple.value;
    }

    /**
     * Returns the value stored in this key as a float.
     * Throws IllegalArgumentException if the value is
     * not a float.
     *
     * @param key The key to search.
     * @return The float value at the key.
     */
    public float getAsNumber(final String key) {
        final FactTuple factTuple = dictionary.get(key);
        if(factTuple == null) {
            throw new IllegalArgumentException("Error: \"" + key + "\" does not exist!");
        }
        if(factTuple.type != FactType.NUMBER) {
            throw new IllegalArgumentException("Error: \"" + key + "\" is not a float!");
        }
        return factTuple.value;
    }

    /**
     * Returns the value stored in this key as a string.
     * Throws IllegalArgumentException if the value is
     * not a string.
     *
     * @param key The key to search.
     * @return The string value at the key.
     */
    public String getAsString(final String key) {
        final FactTuple factTuple = dictionary.get(key);
        if(factTuple == null) {
            throw new IllegalArgumentException("Error: \"" + key + "\" does not exist!");
        }
        if(factTuple.type != FactType.STRING) {
            throw new IllegalArgumentException("Error: \"" + key + "\" is not a string!");
        }
        final String lookupString = StringCache.lookup((int) factTuple.value);

        if(lookupString == null) {
            throw new IllegalStateException("Error: \"" + key + "\" does not exist in StringCache, may be corrupted!");
        }
        return lookupString;
    }

    /**
     * Returns the value stored in this key as a boolean.
     * Throws IllegalArgumentException if the value is
     * not a boolean.
     *
     * @param key The key to search.
     * @return The boolean value at the key.
     */
    public boolean getAsBoolean(final String key) {
        final FactTuple factTuple = dictionary.get(key);
        if(factTuple == null) {
            throw new IllegalArgumentException("Error: \"" + key + "\" does not exist!");
        }
        if(factTuple.type != FactType.BOOLEAN) {
            throw new IllegalArgumentException("Error: \"" + key + "\" is not a boolean!");
        }
        return factTuple.value == ContextTable.TRUE;
    }

    /**
     * Returns the value stored in this key as a list.
     * Throws IllegalArgumentException if the value is
     * not a list.
     *
     * @param key The key to search.
     * @return The list value at the key.
     */
    public IntSet getAsList(final String key) {
        if(lists == null) {
            throw new IllegalStateException("Error: This context table contains no lists!");
        }
        final FactTuple tuple = dictionary.get(key);
        if(tuple.type == null) {
            throw new IllegalArgumentException("Error: \"" + key + "\" does not exist!");
        }
        if(tuple.type != FactType.LIST) {
            throw new IllegalArgumentException("Error: \"" + key + "\" is not a list!");
        }

        final IntSet list = lists.get(key);
        if(list == null) {
            throw new IllegalArgumentException("Error: \"" + key + "\" exists and is the correct type, but list data is corrupted!");
        }

        return list;
    }

    // Returns true if the value at the key is not null and is a number
    public boolean isNumber(final String key) {
        return contains(key, FactType.NUMBER);
    }

    // Returns true if the value at the key is not null and is a string
    public boolean isString(final String key) {
        return contains(key, FactType.STRING);
    }

    // Returns true if the value at the key is not null and is a boolean
    public boolean isBoolean(final String key) {
        return contains(key, FactType.BOOLEAN);
    }

    // Returns true if the value at the key is not null and is a boolean
    public boolean isList(final String key) {
        return contains(key, FactType.LIST);
    }

    // Returns the type of the value at the key
    public FactType getType(final String key) {
        final FactTuple factTuple = dictionary.get(key);
        if(factTuple == null) {
            return FactType.NULL;
        }
        return factTuple.type;
    }

    public boolean contains(final String key) {
        return dictionary.containsKey(key);
    }

    public boolean contains(final String key, final FactType dataType) {
        final FactTuple factTuple = dictionary.get(key);
        return factTuple != null && factTuple.type == dataType;
    }

    // Returns itself for method chaining
    public ContextTable set(final String key, final float value, final FactType type) {
        if(type == FactType.LIST || type == FactType.NULL) {
            throw new IllegalArgumentException("Cannot set item to type " + type.name() + " using generic set method!");
        }
        dictionary.put(key, new FactTuple(type, value));
        MyLogger.finest("Set " + key + " = " + value);
        return this;
    }

    public ContextTable set(final String key, final Collection<String> list) {
        final IntSet intSet = setOf(list);
        return set(key, intSet);
    }

    public ContextTable set(final String key, final IntSet list) {
        // Lazy instantiation of lists variable
        if(lists == null) {
            lists = new HashMap<>();
        }

        dictionary.put(key, new FactTuple(FactType.LIST, DEFAULT_VALUE));
        lists.put(key, list);
        MyLogger.finest("Set " + key + " = " + list.toString());
        return this;
    }

    public ContextTable set(final String key, final float value) {
        set(key, value, FactType.NUMBER);
        return this;
    }

    public ContextTable set(final String key, final String value) {
        set(key, StringCache.cacheString(value), FactType.STRING);
        return this;
    }

    public ContextTable set(final String key, final boolean flag) {
        set(key, fromBoolean(flag), FactType.BOOLEAN);
        return this;
    }

    public ContextTable remove(final String key) {
        final FactTuple tuple = dictionary.get(key);
        if(tuple.type == FactType.LIST) {
            lists.remove(key);
        }
        dictionary.remove(key);
        return this;
    }

    public boolean isOutdated() {
        return cacheId != StringCache.getCacheId();
    }

    @Override
    public String toString() {
        final int LENGTH = 15;
        final StringBuilder result = new StringBuilder("ContextTable:\n");
        for(final Map.Entry<String, FactTuple> entry : dictionary.entrySet()) {
            final String key = entry.getKey();
            final FactTuple tuple = entry.getValue();
            result.append(" ".repeat(Math.max(0, LENGTH - key.length())));
            result.append(key);

            result.append(" = ");
            if(tuple.type == FactType.STRING) {
                result.append(getAsString(key));
            } else if(tuple.type == FactType.BOOLEAN) {
                result.append(getAsBoolean(key));
            } else if(tuple.type == FactType.NUMBER) {
                result.append(getAsNumber(key));
            } else if(tuple.type == FactType.NULL) {
                result.append("NULL");
            } else if(tuple.type == FactType.LIST) {
                // Make a best guess as to what's a string or not
                final IntSet set = lists.get(key);
                result.append("[ ");
                int i = 0;
                for(int item : set) {
                    if(item >= StringCache.INITIAL_ID) {
                        result.append(StringCache.lookup(item));
                    } else {
                        result.append(item);
                    }
                    if(++i < set.size()) {
                        result.append(", ");
                    }
                }
                result.append(" ]");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private record FactTuple(FactType type, float value) {}
}

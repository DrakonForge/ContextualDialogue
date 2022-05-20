package io.github.drakonkinst.contextualdialogue.speech;

import io.github.drakonkinst.commonutil.FastMath;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.function.FunctionLookup;
import io.github.drakonkinst.contextualdialogue.function.FunctionSig;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeechQuery {
    private static final String[] DEFAULT_TABLES = { "event", "listener", "speaker", "location", "structure", "region", "world" };
    private static final int MAX_ATTEMPTS = 5;
    private static final IntSet EMPTY_SET = new IntOpenHashSet();

    public static ContextTable getMatchingTableFromMap(String key, String tableName, Map<String, ContextTable> contexts, FactType factType) {
        if(tableName == null) {
            return findMatchingDefaultTable(key, contexts, factType);
        }
        return contexts.get(tableName);
    }

    public static ContextTable getMatchingTableFromMap(String key, String tableName, Map<String, ContextTable> contexts) {
        if(tableName == null) {
            return findMatchingDefaultTable(key, contexts);
        }
        return contexts.get(tableName);
    }

    public static ContextTable getMatchingOrFirstAvailable(String key, String tableName, Map<String, ContextTable> contexts) {
        if(tableName == null) {
            ContextTable result = findMatchingDefaultTable(key, contexts);
            if(result == null) {
                return findFirstAvailableTable(contexts);
            }
            return result;
        }
        return contexts.get(tableName);
    }

    private static ContextTable findMatchingDefaultTable(String key, Map<String, ContextTable> contexts, FactType factType) {
        for(String tableName : DEFAULT_TABLES) {
            ContextTable table = contexts.get(tableName);
            if(table != null && table.contains(key, factType)) {
                return table;
            }
        }
        return null;
    }

    private static ContextTable findMatchingDefaultTable(String key, Map<String, ContextTable> contexts) {
        for(String tableName : DEFAULT_TABLES) {
            ContextTable table = contexts.get(tableName);
            if(table != null && table.contains(key)) {
                return table;
            }
        }
        return null;
    }

    private static ContextTable findFirstAvailableTable(Map<String, ContextTable> contexts) {
        for(String tableName : DEFAULT_TABLES) {
            ContextTable table = contexts.get(tableName);
            if(table != null) {
                return table;
            }
        }
        return null;
    }

    private final Map<String, ContextTable> contexts;
    private final FunctionLookup functionLookup;

    // Previous list choices
    private final Map<TokenList, IntSet> usedChoiceMap = new HashMap<>();
    private final List<String> prevChoices = new ArrayList<>();
    private final IntList prevChosenIndices = new IntArrayList();

    public SpeechQuery(Map<String, ContextTable> contexts) {
        this.contexts = contexts;
        this.functionLookup = SpeechbankDatabase.getInstance().getFunctionLookup();
    }

    public SpeechQuery(Map<String, ContextTable> contexts, FunctionLookup functionLookup) {
        this.contexts = contexts;
        this.functionLookup = functionLookup;
    }

    public int getValidChoice(TokenList token) {
        IntSet usedChoices = getUsedChoices(token);
        List<Token> tokens = token.getTokens();
        boolean isExhausted = usedChoices.size() >= tokens.size();

        int index;
        int attempts = 0;

        // Repeat until a unique choice is found, or there are no more unique choices
        do {
            index = FastMath.randInt(tokens.size());
        } while(!isExhausted && ++attempts <= MAX_ATTEMPTS && usedChoices.contains(index));

        return index;
    }

    private IntSet getUsedChoices(TokenList listToken) {
        IntSet usedChoices = usedChoiceMap.get(listToken);
        if(usedChoices == null) {
            return EMPTY_SET;
        }
        return usedChoices;
    }

    public void makeChoice(TokenList listToken, String choice, int index) {
        prevChoices.add(choice);
        prevChosenIndices.add(index);

        if(!usedChoiceMap.containsKey(listToken)) {
            usedChoiceMap.put(listToken, new IntOpenHashSet());
        }
        usedChoiceMap.get(listToken).add(index);
    }

    public String getPrevChoice(int choiceIndex) throws SpeechException {
        if(choiceIndex < 1 || choiceIndex > prevChoices.size()) {
            throw new SpeechException("Attempted to query choice at index " + choiceIndex + " but only " + prevChoices.size() + " have been made so far");
        }
        return prevChoices.get(choiceIndex - 1);
    }

    public int getPrevChoiceIndex(int choiceIndex) throws SpeechException {
        if(choiceIndex < 1 || choiceIndex > prevChoices.size()) {
            throw new SpeechException("Attempted to query choice at index " + choiceIndex + " but only " + prevChoices.size() + " have been made so far");
        }
        return prevChosenIndices.getInt(choiceIndex - 1);
    }

    public FactType getContextType(String tableName, String key) {
        ContextTable table = getMatchingTableFromMap(key, tableName, contexts);
        if(table == null) {
            return FactType.NULL;
        }
        return table.getType(key);
    }

    public String getContextAsString(String tableName, String key) throws SpeechException {
        ContextTable table = getMatchingTableFromMap(key, tableName, contexts, FactType.STRING);
        if(table == null) {
            throw new SpeechException("No matching context table found for table \"" + tableName + "\" and key \"" + key + "\" of type STRING");
        }
        return table.getAsString(key);
    }

    public float getContextAsFloat(String tableName, String key) throws SpeechException {
        ContextTable table = getMatchingTableFromMap(key, tableName, contexts, FactType.NUMBER);
        if(table == null) {
            throw new SpeechException("No matching context table found for table \"" + tableName + "\" and key \"" + key + "\" of type NUMBER");
        }
        return table.getAsNumber(key);
    }

    public boolean getContextAsBoolean(String tableName, String key) throws SpeechException {
        ContextTable table = getMatchingTableFromMap(key, tableName, contexts, FactType.BOOLEAN);
        if(table == null) {
            throw new SpeechException("No matching context table found for table \"" + tableName + "\" and key \"" + key + "\" of type BOOLEAN");
        }
        return table.getAsBoolean(key);
    }

    public IntSet getContextAsList(String tableName, String key) throws SpeechException {
        ContextTable table = getMatchingTableFromMap(key, tableName, contexts, FactType.LIST);
        if(table == null) {
            throw new SpeechException("No matching context table found for table \"" + tableName + "\" and key \"" + key + "\" of type LIST");
        }
        return table.getAsList(key);
    }

    public FunctionSig getFunctionSig(String functionName) throws SpeechException {
        FunctionSig sig = functionLookup.getFunctionSig(functionName);
        if(sig == null) {
            throw new SpeechException("No function signature for \"" + functionName + "\"");
        }
        return sig;
    }

    public Map<String, ContextTable> getContexts() {
        return contexts;
    }
}

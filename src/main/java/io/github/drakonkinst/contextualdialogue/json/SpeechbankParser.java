package io.github.drakonkinst.contextualdialogue.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.drakonkinst.contextualdialogue.SymbolChecker;
import io.github.drakonkinst.contextualdialogue.Tokenizer;
import io.github.drakonkinst.contextualdialogue.action.Action;
import io.github.drakonkinst.contextualdialogue.commonutil.FileUtils;
import io.github.drakonkinst.contextualdialogue.commonutil.JsonUtils;
import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.exception.SymbolException;
import io.github.drakonkinst.contextualdialogue.exception.TokenizeException;
import io.github.drakonkinst.contextualdialogue.rule.CriterionTuple;
import io.github.drakonkinst.contextualdialogue.rule.Rule;
import io.github.drakonkinst.contextualdialogue.speech.Speechbank;
import io.github.drakonkinst.contextualdialogue.speech.SpeechbankEntry;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenGroup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class SpeechbankParser {
    private SpeechbankParser() {}

    private static final Result NULL_RESULT =
            new Result(null, Collections.emptyList(), Collections.emptyMap());
    private static final String PRESET_NAME = "preset";
    private static final String PRESET_FILE = PRESET_NAME + ".json";

    private record Result(Speechbank speechbank, List<NamedEntry> namedEntries, Map<String, Token> symbols) {}
    private record QueueItem(JsonObject object, String groupName, String parent) {}
    private record NamedEntry(String category, String name, SpeechbankEntry entry) {}

    // Load entire speech database
    public static Map<String, Speechbank> loadDatabase(String speechbankPath, boolean isInternalFile) {
        Map<String, Result> results = new HashMap<>();
        Queue<QueueItem> loadQueue = new ArrayDeque<>();

        // Read preset file first in the root folder
        readPresetFile(speechbankPath, isInternalFile, results, loadQueue);

        // Read all other speechbanks
        readSpeechbanksInDirectory(speechbankPath, isInternalFile, results, loadQueue);

        // Resolve speechbank dependencies
        resolveLoadQueue(loadQueue, results);

        return extractSpeechbanks(results);
    }

    private static void readPresetFile(String speechbankPath,
                                       boolean isInternalFile,
                                       Map<String, Result> results,
                                       Queue<QueueItem> loadQueue) {
        readSpeechbankFile(speechbankPath, PRESET_FILE, isInternalFile, results, loadQueue);
        if(!results.containsKey(PRESET_NAME)) {
            MyLogger.warning("Warning: Speechbank preset.json should be included in the root folder");
            results.put(PRESET_NAME, NULL_RESULT);
        }
    }

    private static void readSpeechbanksInDirectory(String speechbankPath,
                                                   boolean isInternalFile,
                                                   Map<String, Result> results,
                                                   Queue<QueueItem> loadQueue) {
        // Read directory
        DirectoryStream<Path> speechbankFiles;
        if(isInternalFile) {
            speechbankFiles = FileUtils.getDirectoryStream(speechbankPath);
        } else {
            try {
                speechbankFiles = Files.newDirectoryStream(Paths.get(speechbankPath));
            } catch(IOException e) {
                MyLogger.severe("Error: Failed to read speechbank from \"" + speechbankPath + "\"", e);
                return;
            }
        }

        // Check if files can be read
        if(speechbankFiles == null) {
            throw new IllegalStateException("Unable to register speechbanks since path \"" + speechbankPath + "\" is invalid");
        }

        // Read all files
        for(Path speechbankFile : speechbankFiles) {
            String fileName = speechbankFile.getFileName().toString();
            if(fileName.equals(PRESET_FILE)) {
                // Preset file should already be loaded, skip
                continue;
            }

            if(Files.isDirectory(speechbankFile)) {
                // Read nested directory
                readSpeechbanksInDirectory(Paths.get(speechbankPath, fileName).toString(), isInternalFile, results, loadQueue);
            } else if(fileName.endsWith((JsonUtils.JSON_EXTENSION))) {
                // Read speechbank
                MyLogger.finer("Parsing " + fileName + " from " + speechbankPath);
                readSpeechbankFile(speechbankPath, fileName, isInternalFile, results, loadQueue);
            }
        }
    }

    private static void readSpeechbankFile(String dir,
                                           String fileName,
                                           boolean isInternalFile,
                                           Map<String, Result> results,
                                           Queue<QueueItem> loadQueue) {
        String groupName = JsonUtils.removeExtension(fileName);
        if(results.containsKey(groupName)) {
            MyLogger.severe("Error: Duplicate speechbank found for group \"" + groupName + "\", skipping");
            return;
        }

        JsonObject speechbankObj;
        String path = Paths.get(dir, fileName).toString();
        try {
            if(isInternalFile) {
                speechbankObj = JsonUtils.readInternalFile(path).getAsJsonObject();
            } else {
                speechbankObj = JsonUtils.readExternalFile(path).getAsJsonObject();
            }
        } catch(FileNotFoundException e) {
            MyLogger.severe("Error: File \"" + path + "\" not found");
            return;
        }

        Result parentResult = NULL_RESULT;
        if(speechbankObj.has("parent")) {
            String parentName = speechbankObj.get("parent").getAsString();

            // Check if parent exists yet
            if(results.containsKey(parentName)) {
                // Parent is already loaded, retrieve it
                parentResult = results.get(parentName);
            } else {
                // Parent is not yet loaded, add to loadQueue
                MyLogger.finer("Adding " + groupName + " to queue");
                loadQueue.add(new QueueItem(speechbankObj, groupName, parentName));
                return;
            }
        } else if(!groupName.equals(PRESET_NAME)) {
            parentResult = results.get(PRESET_NAME);
        }

        // Add speechbank to map
        attemptParseSpeechbank(groupName, speechbankObj, parentResult, results);
    }

    private static void resolveLoadQueue(Queue<QueueItem> loadQueue, Map<String, Result> results) {
        int checkAfter = loadQueue.size();
        boolean queueChanged = false;

        while(!loadQueue.isEmpty()) {
            QueueItem item = loadQueue.poll();
            Result parentResult = results.get(item.parent());

            if(parentResult != null) {
                // Parent is now loaded, proceed with parsing speechbank
                attemptParseSpeechbank(item.groupName(), item.object(), parentResult, results);
                queueChanged = true;
            } else {
                // Parent still not loaded, add to queue
                loadQueue.add(item);
            }

            // Check queue size after going through entire queue
            // If size is unchanged, either item's parent does not
            // exist or there is a circular reference
            if(--checkAfter <= 0) {
                if(!queueChanged) {
                    // List is unchanged, gather list of all failed speechbanks
                    StringBuilder remainingItems = new StringBuilder();
                    while(!loadQueue.isEmpty()) {
                        remainingItems.append(loadQueue.poll().groupName());
                        if(!loadQueue.isEmpty()) {
                            remainingItems.append(", ");
                        }
                    }
                    throw new IllegalStateException(
                            "Error: Failed to load speechbanks due to invalid parent (is it missing or circular reference?): " + remainingItems);
                }
                checkAfter = loadQueue.size();
                queueChanged = false;
            }
        }
    }

    private static Map<String, Speechbank> extractSpeechbanks(Map<String, Result> results) {
        Map<String, Speechbank> speechbankMap = new HashMap<>();
        for(Map.Entry<String, Result> entry : results.entrySet()) {
            speechbankMap.put(entry.getKey(), entry.getValue().speechbank());
        }
        return speechbankMap;
    }

    private static void attemptParseSpeechbank(String groupName, JsonObject speechbankObj, Result parentResult, Map<String, Result> results) {
        Result result;
        try {
            result = parseSpeechbank(speechbankObj, parentResult);
        } catch(Exception e) {
            MyLogger.severe("Error: Unable to parse speechbank for " + groupName, e);
            return;
        }
        MyLogger.finer("Adding speechbank " + groupName);
        results.put(groupName, result);
    }

    private static Result parseSpeechbank(JsonObject obj, Result parent) {
        // Read parent
        String parentName = null;
        JsonElement parentElement = obj.get("parent");
        if(parentElement != null) {
            parentName = parentElement.getAsString();
        }

        Map<String, Token> symbols = parseSymbols(obj.get("symbols"), parent.symbols());
        Map<String, SpeechbankEntry[]> categoryToSpeechMap = new HashMap<>();

        // Extract parent named entries
        List<NamedEntry> parentNamedEntries = parent.namedEntries();
        List<NamedEntry> namedEntries;
        if(!parentNamedEntries.isEmpty()) {
            namedEntries = new ArrayList<>(parentNamedEntries);
        } else {
            namedEntries = new ArrayList<>();
        }

        JsonElement speechbankEl = obj.get("speechbank");
        if(speechbankEl != null) {
            JsonObject speechbankObj = speechbankEl.getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : speechbankObj.entrySet()) {
                // Parse category
                String categoryName = entry.getKey();
                JsonArray speechEntriesArr = entry.getValue().getAsJsonArray();
                SpeechbankEntry[] speechEntries =
                        parseSpeechEntries(categoryName, speechEntriesArr, symbols, namedEntries);
                categoryToSpeechMap.put(categoryName, speechEntries);
            }
        }

        Speechbank speechbank = new Speechbank(parentName, categoryToSpeechMap);
        return new Result(speechbank, namedEntries, symbols);
    }

    private static Map<String, Token> parseSymbols(JsonElement element, Map<String, Token> parentSymbols) {
        if(element == null) {
            if(parentSymbols.isEmpty()) {
                return Collections.emptyMap();
            }
            return new HashMap<>(parentSymbols);
        }

        JsonArray arr = element.getAsJsonArray();
        Map<String, Token> symbols = new HashMap<>(parentSymbols);

        for(JsonElement arrEl : arr) {
            JsonObject obj = arrEl.getAsJsonObject();
            String symbolName = obj.get("name").getAsString();
            if(symbols.containsKey(symbolName)) {
                throw new JsonParseException("Symbol \"" + symbolName + "\" is already defined");
            }
            Token tokenValue = TokenParser.parseToken(obj.get("exp"));
            symbols.put(symbolName, tokenValue);
        }
        return symbols;
    }

    private static SpeechbankEntry[] parseSpeechEntries(String categoryName,
                                                        JsonArray arr,
                                                        Map<String, Token> symbols,
                                                        List<NamedEntry> namedEntries) {
        SpeechbankEntry[] speechEntries = new SpeechbankEntry[arr.size()];
        for(int i = 0; i < arr.size(); ++i) {
            speechEntries[i] = parseSpeechEntry(categoryName, arr.get(i).getAsJsonObject(), symbols, namedEntries);
        }
        return speechEntries;
    }

    private static SpeechbankEntry parseSpeechEntry(String categoryName,
                                                    JsonObject obj,
                                                    Map<String, Token> parentSymbols,
                                                    List<NamedEntry> namedEntries) {
        // Read symbols
        Map<String, Token> symbols = parseSymbols(obj.get("symbols"), parentSymbols);

        // Read preset rules
        JsonElement presetsEl = obj.get("presets");
        List<CriterionTuple> presetRules = Collections.emptyList();
        if(presetsEl != null) {
            presetRules = parsePresetRules(presetsEl.getAsJsonArray(), namedEntries);
        }

        // Read rule
        JsonElement ruleEl = obj.get("criteria");
        JsonArray criteriaArr = null;
        if(ruleEl != null) {
            criteriaArr = ruleEl.getAsJsonArray();
        }
        Rule rule = CriteriaParser.parseRule(criteriaArr, presetRules);

        // Read lines
        TokenGroup[] speechLines = checkSpeechLinesType(obj.get("lines"), symbols, namedEntries);

        // Todo: Actions
        JsonElement actionsEl = obj.get("actions");
        Action[] actions = null;
        if(actionsEl != null) {
            actions = ActionParser.parseActions(actionsEl.getAsJsonArray());
        }

        SpeechbankEntry entry = new SpeechbankEntry(rule, speechLines, actions);

        // Read name
        checkNamedRule(obj.get("name"), entry, categoryName, namedEntries);

        return entry;
    }

    private static List<CriterionTuple> parsePresetRules(JsonArray arr,
                                                         List<NamedEntry> namedEntries) {
        List<CriterionTuple> presetRules = new ArrayList<>();
        for(JsonElement presetEl : arr) {
            JsonObject presetObj = presetEl.getAsJsonObject();
            String name = presetObj.get("name").getAsString();
            String category = extractCategoryFromPreset(presetObj);
            SpeechbankEntry entry = searchForNamedEntry(name, category, namedEntries);
            if(entry == null) {
                throw new JsonParseException("Error: No matching named entry for name=" + name + ", category=" + category + "");
            }
            Collections.addAll(presetRules, entry.getRule().getCriteria());

        }
        return presetRules;
    }

    private static TokenGroup[] checkSpeechLinesType(JsonElement linesEl,
                                                Map<String, Token> symbols,
                                                List<NamedEntry> namedEntries) {
        if(linesEl != null) {
            if(linesEl.isJsonObject()) {
                // Preset
                JsonObject linesObj = linesEl.getAsJsonObject();
                String name = linesObj.get("name").getAsString();
                String category = extractCategoryFromPreset(linesObj);
                SpeechbankEntry entry = searchForNamedEntry(name, category, namedEntries);
                if(entry == null) {
                    throw new JsonParseException("Error: No matching named entry for name=" + name + ", category=" + category + "");
                }
                if(entry.getSpeechLines() == null) {
                    throw new JsonParseException("Error: Named entry name=" + name + ", category=" + category + " does not have any speech lines!");
                }
                return entry.getSpeechLines();
            } else if(linesEl.isJsonArray()) {
                // Normal lines
                JsonArray linesArr = linesEl.getAsJsonArray();
                return parseSpeechLines(linesArr, symbols);
            } else {
                throw new JsonParseException("Lines must be a preset definition or array");
            }
        }
        return null;
    }

    private static TokenGroup[] parseSpeechLines(JsonArray arr, Map<String, Token> symbols) {
        TokenGroup[] tokens = new TokenGroup[arr.size()];
        for(int i = 0; i < arr.size(); ++i) {
            String speechLine = arr.get(i).getAsString();
            try {
                TokenGroup token = Tokenizer.tokenize(speechLine);
                SymbolChecker.test(token, symbols);
                MyLogger.finest("Tokenization:" + token);
                tokens[i] = token;
            } catch(TokenizeException e) {
                throw new JsonParseException("Tokenization error for \"" + speechLine + "\"\n" + e.getMessage(), e);
            } catch(SymbolException e) {
                throw new JsonParseException("Symbol error for \"" + speechLine + "\"\n" + e.getMessage(), e);
            }
        }
        return tokens;
    }

    private static void checkNamedRule(JsonElement nameEl,
                                       SpeechbankEntry entry,
                                       String categoryName,
                                       List<NamedEntry> namedEntries) {
        if(nameEl != null) {
            String name = nameEl.getAsString();
            if(searchForNamedEntry(name, categoryName, namedEntries) != null) {
                throw new JsonParseException("Entry with name=" + name + ", category=" + categoryName + " already exists");
            }
            MyLogger.finest("Adding named entry name=" + name + ", category=" + categoryName);
            namedEntries.add(new NamedEntry(categoryName, name, entry));
        }
    }

    /* Helpers */

    private static SpeechbankEntry searchForNamedEntry(String name,
                                                       String category,
                                                       List<NamedEntry> namedEntries) {
        for (NamedEntry namedEntry : namedEntries) {
            if (namedEntry.name().equals(name)
                    && (category == null || namedEntry.category().equals(category))) {
                return namedEntry.entry();
            }
        }
        return null;
    }

    private static String extractCategoryFromPreset(JsonObject presetObj) {
        JsonElement categoryEl = presetObj.get("category");
        if(categoryEl == null) {
            return null;
        }
        return categoryEl.getAsString();
    }
}

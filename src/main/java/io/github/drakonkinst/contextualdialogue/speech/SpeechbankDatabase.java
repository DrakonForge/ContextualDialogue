package io.github.drakonkinst.contextualdialogue.speech;

import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.function.FunctionLookup;
import io.github.drakonkinst.contextualdialogue.function.FunctionSig;
import io.github.drakonkinst.contextualdialogue.json.SpeechbankParser;

import java.io.Serializable;
import java.util.Map;

public class SpeechbankDatabase implements Serializable {
    private static SpeechbankDatabase instance = null;

    public static void loadDatabase(String path, boolean isInternalFile, FunctionLookup functionLookup) {
        if(instance != null) {
            throw new IllegalStateException("Database is already initialized");
        }
        instance = new SpeechbankDatabase(SpeechbankParser.loadDatabase(path, isInternalFile, functionLookup), functionLookup);
    }

    public static SpeechbankDatabase getInstance() {
        if(instance == null) {
            throw new IllegalStateException("Cannot get instance before initializing database");
        }
        return instance;
    }

    private final Map<String, Speechbank> groupToSpeechbankMap;
    private final FunctionLookup functionLookup;

    private SpeechbankDatabase(Map<String, Speechbank> groupToSpeechbankMap, FunctionLookup functionLookup) {
        this.groupToSpeechbankMap = groupToSpeechbankMap;
        this.functionLookup = functionLookup;
    }

    public SpeechResult generateLine(String group, String category, SpeechQuery speechQuery) {
        Speechbank speechbank = groupToSpeechbankMap.get(group);
        if(speechbank == null) {
            MyLogger.severe("Speechbank for group \"" + group + "\" does not exist!");
            return null;
        }

        SpeechbankEntry chosenEntry = speechbank.selectEntry(category, speechQuery.getContexts());
        String parent = speechbank.getParent();
        if(chosenEntry == null) {
            if(parent == null) {
                return null;
            } else {
                return generateLine(parent, category, speechQuery);
            }
        }

        chosenEntry.performActions(speechQuery.getContexts());
        SpeechResult generatedLine = chosenEntry.generateLine(speechQuery);
        if(generatedLine == null && parent != null) {
            return generateLine(parent, category, speechQuery);
        }
        return generatedLine;
    }

    public FunctionLookup getFunctionLookup() {
        return functionLookup;
    }

    public Speechbank getSpeechbank(String groupName) {
        return groupToSpeechbankMap.get(groupName);
    }


}

package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.contextualdialogue.commonutil.JsonUtils;
import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.json.ContextParser;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.speech.SpeechResult;
import io.github.drakonkinst.contextualdialogue.speech.SpeechbankDatabase;
import io.github.drakonkinst.contextualdialogue.speech.text.TextToken;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        if(args.length < 7) {
            MyLogger.initialize(Level.SEVERE);
            MyLogger.severe("Error: Must specify at least 7 arguments: speechbankDir, contextPath, group, category, howMany, verbose, printLines");
            return;
        }

        String speechbankDir = args[0];
        String contextPath = args[1];
        String group = args[2];
        String category = args[3];
        int howMany = Integer.parseInt(args[4]);
        boolean verbose = Boolean.parseBoolean(args[5]);
        boolean printLines = Boolean.parseBoolean(args[6]);
        boolean tokenize = Boolean.parseBoolean(args[7]);

        if(verbose) {
            MyLogger.initialize(Level.FINEST);
        } else {
            MyLogger.initialize(Level.INFO);
        }

        long start = System.currentTimeMillis();
        SpeechbankDatabase.loadDatabase(speechbankDir, false);
        long end = System.currentTimeMillis();
        MyLogger.info("Took " + (end - start) + "ms to load speechbanks");

        Map<String, ContextTable> contexts;
        try {
             contexts = ContextParser.parseContexts(JsonUtils.readExternalFile(contextPath).getAsJsonObject());
        } catch (FileNotFoundException e) {
            MyLogger.severe("Path " + contextPath + " does not exist");
            return;
        }
        for(Map.Entry<String, ContextTable> entry : contexts.entrySet()) {
            MyLogger.finest("CONTEXT TABLE " + entry.getKey());
            MyLogger.finest(entry.getValue().toString());
        }

        MyLogger.info();
        SpeechbankDatabase database = SpeechbankDatabase.getInstance();
        start = System.currentTimeMillis();
        for(int i = 0; i < howMany; ++i) {
            SpeechQuery query = new SpeechQuery(contexts, SymbolChecker.functionLookup);
            SpeechResult result = database.generateLine(group, category, query);

            if(printLines) {
                if(tokenize) {
                    MyLogger.info(tokensToString(result.getTextTokens()));
                } else {
                    MyLogger.info(result.getText());
                }
            }
        }
        end = System.currentTimeMillis();
        MyLogger.info();
        MyLogger.info("Took " + (end - start) + "ms to generate " + howMany + " speech lines");
    }

    private static String tokensToString(List<TextToken> tokens) {
        StringBuilder sb = new StringBuilder();
        for(TextToken token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }
}

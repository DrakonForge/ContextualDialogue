package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.commonutil.JsonUtils;
import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.function.FunctionLookup;
import io.github.drakonkinst.contextualdialogue.json.ContextParser;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.speech.SpeechResult;
import io.github.drakonkinst.contextualdialogue.speech.SpeechbankDatabase;
import io.github.drakonkinst.contextualdialogue.speech.text.TextToken;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Main {
    private static final FunctionLookup functionLookup = new FunctionLookup();

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
        boolean showPriority = Boolean.parseBoolean(args[8]);
        boolean showStats = Boolean.parseBoolean(args[9]);

        if(verbose) {
            MyLogger.initialize(Level.FINEST);
        } else {
            MyLogger.initialize(Level.INFO);
        }

        long start = System.currentTimeMillis();
        SpeechbankDatabase.loadDatabase(speechbankDir, false, functionLookup);
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
        Set<String> generated = new HashSet<>();
        int numRepeats = 0;
        for(int i = 0; i < howMany; ++i) {
            SpeechQuery query = new SpeechQuery(contexts);
            SpeechResult result = database.generateLine(group, category, query);

            if(printLines) {
                if(result == null) {
                    MyLogger.info("<failed to generate>");
                } else {
                    String message = "";
                    String text = result.getText();
                    if(showPriority) {
                        message = "(" + result.getPriority() + ") ";
                    }
                    if(tokenize) {
                        message += tokensToString(result.getTextTokens());
                    } else {
                        message += text;
                    }
                    MyLogger.info(message);
                    if(!generated.add(text)) {
                        ++numRepeats;
                    }
                }
            }
        }
        end = System.currentTimeMillis();
        MyLogger.info();
        MyLogger.info("Took " + (end - start) + "ms to generate " + howMany + " speech lines");

        if(showStats) {
            MyLogger.info("Ran into " + numRepeats + " repeats");
            MyLogger.info("This query checks a maximum of " + Statistics.countNumCheckedRules(group, category, contexts) + " rules");
            MyLogger.info("And has approximately " + Statistics.countNumVariations(group, category, contexts) + " possible variations");
        }
    }

    private static String tokensToString(List<TextToken> tokens) {
        StringBuilder sb = new StringBuilder();
        for(TextToken token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }
}

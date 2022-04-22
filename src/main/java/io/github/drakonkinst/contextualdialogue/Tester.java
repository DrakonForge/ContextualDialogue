package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.exception.SymbolException;
import io.github.drakonkinst.contextualdialogue.exception.TokenizeException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenContext;
import io.github.drakonkinst.contextualdialogue.token.TokenInt;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import io.github.drakonkinst.contextualdialogue.token.TokenString;
import io.github.drakonkinst.contextualdialogue.util.NumericalSpeech;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Tester {
    public static void main(String[] args) throws TokenizeException, SymbolException, SpeechException {
        MyLogger.initialize(Level.ALL);

        String complex1 = "Hey @name! Go see the farmer in town, #structure.farmer! @capitalize(@subjective(#structure.farmer.gender))'s got a sale going right now!";
        String complex2 = "I see @count(@numbers) @pluralize(@count(@numbers), \"light\", \"lights\")!";
        ///*
        testLine("Hello #name, I'm #speaker.name.");
        testLine(complex1);
        testLine("Your name in lowercase is @decapitalize(@name)");
        testLine("I see @count([1, 2, 3, 4, 5]) lights!");
        testLine(complex2);
        testLine("Would you like some @fruits, @fruits, or @fruits today? The @fruits are on sale!");
        testLine("@capitalize(@fruits) or @fruits? I'm a fan of @prev(1) personally.");
        testLine("Everyone knows counting goes @numbers, @numbers, @numbers, @numbers, @numbers!");
        testLine("My favorite color is [\"blue\", \"red\", \"yellow\"]");
        testLine("Hello! I'm turning #age today!");
        testLine("Hello there! / General Kenobi!");
        testLine("@concat(#age, #age)");
        //*/

        /*
        int howManyTokenize = 100000;
        testTokenizeLarge(complex1, howManyTokenize);
        testTokenizeLarge(complex2, howManyTokenize);

        int howManyGenerate = 10000;
        testGenerateLarge(complex1, howManyGenerate);
        testGenerateLarge(complex2, howManyGenerate);
        */
    }

    private static Map<String, Token> getTestSymbols() {
        Map<String, Token> symbols = new HashMap<>();
        symbols.put("name", new TokenContext("listener", "name"));
        symbols.put("numbers", new TokenList(List.of(
                new TokenInt(1),
                new TokenInt(2),
                new TokenInt(3),
                new TokenInt(4),
                new TokenInt(5)
        )));
        symbols.put("fruits", new TokenList(List.of(
                new TokenString("apples"),
                new TokenString("grapes"),
                new TokenString("cherries")
        )));
        return symbols;
    }

    private static Map<String, ContextTable> getTestContexts() {
        Map<String, ContextTable> contexts = new HashMap<>();
        contexts.put("listener", new ContextTable()
                .set("name", "Drakonkinst"));
        contexts.put("speaker", new ContextTable()
                .set("name", "Bilbo")
                .set("age", 111));
        contexts.put("structure", new ContextTable()
                .set("farmer", "Farmer Maggot")
                .set("farmer.gender", "male"));
        return contexts;
    }

    private static void testTokenizeLarge(String str, int count) throws TokenizeException, SymbolException {
        Map<String, Token> symbols = getTestSymbols();

        long start = System.currentTimeMillis();
        for(int i = 0; i < count; ++i) {
            Token token = Tokenizer.tokenize(str);
            SymbolChecker.test(token, symbols);
        }
        long end = System.currentTimeMillis();

        MyLogger.info("Took " + (end - start) + "ms to tokenize \"" + str + "\" " + count + " times");
    }

    private static void testGenerateLarge(String str, int count) throws TokenizeException, SymbolException {
        Map<String, Token> symbols = getTestSymbols();
        Map<String, ContextTable> contexts = getTestContexts();
        SpeechQuery query = new SpeechQuery(contexts, SymbolChecker.functionLookup);
        Token token = Tokenizer.tokenize(str);
        SymbolChecker.test(token, symbols);

        long start = System.currentTimeMillis();
        for(int i = 0; i < count; ++i) {
            try {
                token.evaluate(query);
            } catch (SpeechException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();

        MyLogger.info("Took " + (end - start) + "ms to generate \"" + str + "\" " + count + " times");
    }

    private static void testLine(String str) throws TokenizeException, SymbolException {
        Map<String, Token> symbols = getTestSymbols();
        Map<String, ContextTable> contexts = getTestContexts();
        SpeechQuery query = new SpeechQuery(contexts, SymbolChecker.functionLookup);

        MyLogger.info(String.format("%16s: %s", "Original string", '"' + str + '"'));

        Token token = Tokenizer.tokenize(str);
        MyLogger.info(String.format("%16s: %s", "Tokenization", token));

        SymbolChecker.test(token, symbols);
        MyLogger.info(String.format("%16s: %s", "Symbol Filling", token));

        try {
            String speechLine = token.evaluate(query);
            MyLogger.info(String.format("%16s: %s", "Speech Line", '"' + speechLine + '"'));
        } catch (SpeechException e) {
            MyLogger.severe("Failed to generate speech line", e);
        }
        MyLogger.info();
    }
}
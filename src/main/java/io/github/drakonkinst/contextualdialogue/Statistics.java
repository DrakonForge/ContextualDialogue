package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.rule.CriterionFail;
import io.github.drakonkinst.contextualdialogue.rule.Rule;
import io.github.drakonkinst.contextualdialogue.speech.Speechbank;
import io.github.drakonkinst.contextualdialogue.speech.SpeechbankDatabase;
import io.github.drakonkinst.contextualdialogue.speech.SpeechbankEntry;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenGroup;
import io.github.drakonkinst.contextualdialogue.token.TokenList;

import java.util.List;
import java.util.Map;

public final class Statistics {
    private Statistics() {}

    public static long countNumVariations(Token token) {
        long variations = 1;
        if(token instanceof TokenGroup tokenGroup) {
            List<Token> tokens = tokenGroup.getTokens();
            for(Token t : tokens) {
                variations *= countNumVariations(t);
            }
        } else if(token instanceof TokenList tokenList) {
            List<Token> tokens = tokenList.getTokens();
            variations = tokens.size();
        }
        return variations;
    }

    public static long countNumVariations(SpeechbankEntry entry) {
        TokenGroup[] tokens = entry.getSpeechLines();
        long variations = 0;
        for(TokenGroup t : tokens) {
            variations += countNumVariations(t);
        }
        return variations;
    }

    public static long countNumVariations(String groupName, String category, Map<String, ContextTable> contexts) {
        long variations = 0;
        Speechbank speechbank = SpeechbankDatabase.getInstance().getSpeechbank(groupName);
        if(speechbank == null) {
            return 0;
        }
        SpeechbankEntry[] entries = speechbank.getEntriesFor(category);
        for(SpeechbankEntry entry : entries) {
            boolean couldMatch = Speechbank.match(entry.getRule(), contexts, true);
            if(couldMatch) {
                variations += countNumVariations(entry);
            }
        }
        return variations;
    }

    public static long countNumCheckedRules(String groupName, String category, Map<String, ContextTable> contexts) {
        long rulesChecked = 0;
        Speechbank speechbank = SpeechbankDatabase.getInstance().getSpeechbank(groupName);
        if(speechbank == null) {
            return 0;
        }
        SpeechbankEntry[] entries = speechbank.getEntriesFor(category);
        int highestMatchingPriority = -999;
        for(SpeechbankEntry entry : entries) {
            int priority = entry.getRule().getPriority();
            if(priority < highestMatchingPriority) {
                break;
            }
            ++rulesChecked;
            boolean couldMatch = Speechbank.match(entry.getRule(), contexts, false);
            if(couldMatch && !containsFailCriterion(entry.getRule())) {
                // Definitely matches, this can cause an early stop
                if(priority > highestMatchingPriority) {
                    highestMatchingPriority = priority;
                }
            }
        }
        return rulesChecked;
    }

    private static boolean containsFailCriterion(Rule rule) {
        for(int i = 0; i < rule.getSize(); ++i) {
            if(rule.getTupleAt(i).getCriterion() instanceof CriterionFail) {
                return true;
            }
        }
        return false;
    }
}

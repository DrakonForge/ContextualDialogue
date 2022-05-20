package io.github.drakonkinst.contextualdialogue.speech;

import io.github.drakonkinst.contextualdialogue.action.Action;
import io.github.drakonkinst.commonutil.FastMath;
import io.github.drakonkinst.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.rule.Rule;
import io.github.drakonkinst.contextualdialogue.token.TokenGroup;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;

/**
 * A tuple representing an entry in the speechbank database.
 * Contains a list of possible speech lines, along with its
 * associated criteria rule.
 */
public class SpeechbankEntry implements Comparable<SpeechbankEntry>, Serializable {
    private static final int MAX_ATTEMPTS = 3;

    private final Rule rule;
    private final TokenGroup[] speechLines;    // Each token represents a speech line
    private final Action[] actions;
    private final boolean empty;

    public SpeechbankEntry(Rule rule, TokenGroup[] speechLines, Action[] actions, boolean empty) {
        this.rule = rule;
        this.speechLines = speechLines;
        this.actions = actions;
        this.empty = empty;
    }

    public SpeechResult generateLine(SpeechQuery query) {
        int attempts = 0;

        do {
            try {
                TokenGroup speechLine = chooseRandomSpeechLine();
                return new SpeechResult(speechLine.toTextTokens(query), rule.getPriority());
            } catch(SpeechException e) {
                MyLogger.getLogger().log(Level.FINE, "Speech line failed to generate: " + e.getMessage(), e);
            }
        } while(++attempts < MAX_ATTEMPTS);
        return null;
    }

    public void performActions(Map<String, ContextTable> contexts) {
        if(actions == null) {
            return;
        }

        for(Action action : actions) {
            action.perform(contexts);
        }
    }

    private TokenGroup chooseRandomSpeechLine() throws SpeechException {
        if(speechLines == null || speechLines.length <= 0) {
            throw new SpeechException("This entry has no speech lines!");
        }
        return speechLines[FastMath.randInt(speechLines.length)];
    }

    public Rule getRule() {
        return rule;
    }

    public TokenGroup[] getSpeechLines() {
        return speechLines;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public int compareTo(SpeechbankEntry o) {
        return rule.getPriority() - o.getRule().getPriority();
    }
}

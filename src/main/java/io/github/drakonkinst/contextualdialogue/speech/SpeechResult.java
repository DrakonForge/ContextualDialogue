package io.github.drakonkinst.contextualdialogue.speech;

import io.github.drakonkinst.contextualdialogue.speech.text.TextLiteral;
import io.github.drakonkinst.contextualdialogue.speech.text.TextToken;

import java.util.List;

public class SpeechResult {
    private final List<TextToken> textTokens;
    private final int priority;

    public SpeechResult(List<TextToken> textTokens, int priority) {
        this.textTokens = textTokens;
        this.priority = priority;
    }

    public String getText() {
        return toString();
    }

    public List<TextToken> getTextTokens() {
        return textTokens;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TextToken token : textTokens) {
            if (token instanceof TextLiteral literal) {
                sb.append(literal);
            }
        }
        return sb.toString();
    }
}

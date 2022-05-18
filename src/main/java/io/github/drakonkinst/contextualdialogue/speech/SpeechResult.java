package io.github.drakonkinst.contextualdialogue.speech;

import io.github.drakonkinst.contextualdialogue.speech.text.TextLiteral;
import io.github.drakonkinst.contextualdialogue.speech.text.TextToken;

import java.util.List;

public class SpeechResult {
    private final List<TextToken> text;
    private final int priority;

    public SpeechResult(List<TextToken> text, int priority) {
        this.text = text;
        this.priority = priority;
    }

    public List<TextToken> getText() {
        return text;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TextToken token : text) {
            if (token instanceof TextLiteral literal) {
                sb.append(literal);
            }
        }
        return sb.toString();
    }
}

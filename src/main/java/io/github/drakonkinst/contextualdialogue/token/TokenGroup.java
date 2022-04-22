package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.List;

public class TokenGroup implements Token {
    private final List<Token> tokens;
    
    public TokenGroup(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    // Note: You may want to evaluate this differently in order to add a real-time or multi-line
    // component to these Speech Lines. Specifically, the evaluate() behavior for
    // TokenLineEnd and TokenPause are placeholders so that
    public String evaluate(SpeechQuery query) throws SpeechException {
        StringBuilder sb = new StringBuilder();
        for(Token token : tokens) {
            sb.append(token.evaluate(query));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Token token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }
}

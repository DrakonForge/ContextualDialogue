package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

public class TokenString implements Token {
    private final String text;
    
    public TokenString(String text) {
        this.text = text;
    }
    
    public String getValue() {
        return text;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        return text;
    }

    @Override
    public String toString() {
        return '"' + text + '"';
    }
}

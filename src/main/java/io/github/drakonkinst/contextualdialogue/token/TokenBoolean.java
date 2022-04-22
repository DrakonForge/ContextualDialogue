package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

public class TokenBoolean implements Token {
    private final boolean value;
    
    public TokenBoolean(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        throw new SpeechException("Boolean token should not be evaluated!");
    }

    @Override
    public String toString() {
        return "TokenBoolean{" +
                "value=" + value +
                '}';
    }
}

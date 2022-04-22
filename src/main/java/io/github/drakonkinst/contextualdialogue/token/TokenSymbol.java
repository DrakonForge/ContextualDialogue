package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

public class TokenSymbol implements Token {
    private final String name;
    
    public TokenSymbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        throw new SpeechException("Not yet implemented");
    }

    @Override
    public String toString() {
        return "{Symbol " + name + "}";
    }
}

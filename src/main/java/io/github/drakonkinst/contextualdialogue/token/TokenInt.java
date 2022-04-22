package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.util.NumericalSpeech;

public class TokenInt implements Token {
    private final int value;
    
    public TokenInt(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        return NumericalSpeech.integerToWord(value);
    }

    @Override
    public String toString() {
        return "" + value;
    }
}

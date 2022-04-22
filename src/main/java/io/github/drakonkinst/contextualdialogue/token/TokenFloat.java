package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.util.NumericalSpeech;

public class TokenFloat implements Token {
    private final float value;
    
    public TokenFloat(float value) {
        this.value = value;
    }
    
    public float getValue() {
        return value;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        int rounded = (int) value;
        return NumericalSpeech.integerToWord(rounded);
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
}

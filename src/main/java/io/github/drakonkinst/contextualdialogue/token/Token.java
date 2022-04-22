package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

public interface Token {
    String evaluate(SpeechQuery query) throws SpeechException;
}
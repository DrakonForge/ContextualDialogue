package io.github.drakonkinst.contextualdialogue.speech.text;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.token.Token;

public class TextFormat implements TextToken, Token {
    public static TextFormat LINEBREAK = new TextFormat("linebreak");

    protected final String attribute;

    public TextFormat(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        return "";
    }

    @Override
    public String toString() {
        return '{' + attribute + '}';
    }
}

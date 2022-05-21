package io.github.drakonkinst.contextualdialogue.speech.text;

import io.github.drakonkinst.commonutil.MyLogger;

public class TextLiteral implements TextToken {
    private final String text;

    private static boolean onlySpaces(String text) {
        return text.trim().length() == 0;
    }

    public TextLiteral(String text) {
        this.text = text;
        if(onlySpaces(text)) {
            MyLogger.warning("Warning: Text literal with only whitespace should be grouped with other literals");
        }
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return '"' + text + '"';
    }
}

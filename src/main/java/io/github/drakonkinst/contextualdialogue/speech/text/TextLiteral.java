package io.github.drakonkinst.contextualdialogue.speech.text;

public class TextLiteral implements TextToken {
    private final String text;

    public TextLiteral(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return '"' + text + '"';
    }
}

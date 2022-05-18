package io.github.drakonkinst.contextualdialogue.speech.text;

public class TextFormatString extends TextFormat {
    private final String value;

    public TextFormatString(String attribute, String value) {
        super(attribute);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '{' + attribute + "=\"" + value + "\"}";
    }
}

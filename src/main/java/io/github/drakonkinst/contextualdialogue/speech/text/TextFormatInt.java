package io.github.drakonkinst.contextualdialogue.speech.text;

public class TextFormatInt extends TextFormat implements TextFormatNumber {
    private final int value;

    public TextFormatInt(String attribute, int value) {
        super(attribute);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '{' + attribute + '=' + value + '}';
    }

    @Override
    public float getFloatValue() {
        return value;
    }
}

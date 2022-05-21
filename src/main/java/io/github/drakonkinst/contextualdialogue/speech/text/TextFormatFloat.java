package io.github.drakonkinst.contextualdialogue.speech.text;

public class TextFormatFloat extends TextFormat implements TextFormatNumber {
    private final float value;

    public TextFormatFloat(String attribute, float value) {
        super(attribute);
        this.value = value;
    }

    public float getValue() {
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

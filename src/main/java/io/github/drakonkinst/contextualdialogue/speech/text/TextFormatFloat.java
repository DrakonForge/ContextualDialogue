package io.github.drakonkinst.contextualdialogue.speech.text;

public class TextFormatFloat extends TextFormat {
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
}

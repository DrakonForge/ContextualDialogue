package io.github.drakonkinst.contextualdialogue.speech.text;

public class TextFormatBool extends TextFormat {
    public static final TextFormatBool BOLD_ON = new TextFormatBool("bold", true);
    public static final TextFormatBool BOLD_OFF = new TextFormatBool("bold", false);
    public static final TextFormatBool ITALICS_ON = new TextFormatBool("italics", true);
    public static final TextFormatBool ITALICS_OFF = new TextFormatBool("italics", false);

    private final boolean value;

    public TextFormatBool(String attribute, boolean value) {
        super(attribute);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '{' + attribute + '=' + value + '}';
    }
}

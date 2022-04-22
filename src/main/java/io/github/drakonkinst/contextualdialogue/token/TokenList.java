package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;

import java.util.List;

public class TokenList implements Token {
    private final List<Token> tokens;

    public TokenList(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        int choiceIndex = query.getValidChoice(this);
        Token choice = tokens.get(choiceIndex);
        String choiceStr = choice.evaluate(query);
        query.makeChoice(this, choiceStr, choiceIndex);
        return choiceStr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < tokens.size(); ++i) {
            sb.append(tokens.get(i).toString());
            if (i < tokens.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}

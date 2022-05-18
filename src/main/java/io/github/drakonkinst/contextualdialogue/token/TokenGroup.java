package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.speech.text.TextLiteral;
import io.github.drakonkinst.contextualdialogue.speech.text.TextToken;

import java.util.ArrayList;
import java.util.List;

public class TokenGroup implements Token {
    private final List<Token> tokens;
    
    public TokenGroup(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public List<Token> getTokens() {
        return tokens;
    }

    public List<TextToken> toTextTokens(SpeechQuery query) throws SpeechException {
        List<TextToken> text = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(Token token : tokens) {
            if(token instanceof TextToken textToken) {
                if(!sb.isEmpty()) {
                    text.add(new TextLiteral(sb.toString()));
                    sb.setLength(0);
                }
                text.add(textToken);
            } else {
                sb.append(token.evaluate(query));
            }
        }

        if(!sb.isEmpty()) {
            text.add(new TextLiteral(sb.toString()));
        }

        return text;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        StringBuilder sb = new StringBuilder();
        for(Token token : tokens) {
            sb.append(token.evaluate(query));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Token token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }
}

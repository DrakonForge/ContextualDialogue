package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.util.NumericalSpeech;

public class TokenContext implements Token {
    private final String table;
    private final String key;
    
    public TokenContext(String table, String key) {
        this.table = table;
        this.key = key;
    }
    
    public String getTable() {
        return table;
    }
    
    public String getKey() {
        return key;
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        FactType type = query.getContextType(table, key);

        if(type == FactType.STRING) {
            return query.getContextAsString(table, key);
        }

        if(type == FactType.NUMBER) {
            int value = (int) query.getContextAsFloat(table, key);
            return NumericalSpeech.integerToWord(value);
        }

        if(type == FactType.NULL) {
            throw new SpeechException("Context " + table + "." + key + " does not exist in this query");
        }

        throw new SpeechException("Context of type " + type.name() + " should not be evaluated");
    }
    
    @Override
    public String toString() {
        return "{Context " + table + " . " + key + "}";
    }
}

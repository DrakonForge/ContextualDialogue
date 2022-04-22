package io.github.drakonkinst.contextualdialogue.token;

import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Set;

public class TokenTypes {
    public static final TokenTypes LIST_ITEM = new TokenTypes(Set.of(
            TokenContext.class,
            TokenInt.class,
            TokenString.class), Object[].class);
    public static final TokenTypes STRING = new TokenTypes(Set.of(
            TokenContext.class,
            TokenList.class,
            TokenInt.class,
            TokenFloat.class,
            TokenString.class), String.class);
    public static final TokenTypes LIST = new TokenTypes(Set.of(
            TokenContext.class,
            TokenList.class), IntList.class);
    public static final TokenTypes INTEGER = new TokenTypes(Set.of(
            TokenContext.class,
            TokenInt.class), int.class);
    public static final TokenTypes NUMBER = new TokenTypes(Set.of(
            TokenContext.class,
            TokenInt.class,
            TokenFloat.class), float.class);
    public static final TokenTypes BOOLEAN = new TokenTypes(Set.of(
            TokenContext.class,
            TokenBoolean.class), boolean.class);

    private final Set<Class<? extends Token>> types;
    private final Class<?> desiredType;

    private TokenTypes(Set<Class<? extends Token>> types, Class<?> desiredType) {
        this.types = types;
        this.desiredType = desiredType;
    }

    public boolean matchesType(Class<? extends Token> type) {
        return types.contains(type);
    }

    public Class<?> getDesiredType() {
        return desiredType;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Class<? extends Token> type : types) {
            if(str.length() == 0) {
                str.append(type.getName());
            } else {
                str.append(" or ").append(type.getName());
            }
        }
        return str.toString();
    }
}

package io.github.drakonkinst.contextualdialogue.function;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import io.github.drakonkinst.contextualdialogue.token.TokenTypes;
import io.github.drakonkinst.contextualdialogue.token.Token;

public class FunctionSig {
    private List<TokenTypes> argTypes;
    private Class<? extends Token> returnType;
    private Method method;
    private boolean varArgs;
    private boolean usesQuery;

    public FunctionSig(Class<? extends Token> returnType, boolean varArgs, boolean usesQuery, Method method, TokenTypes[] tokenTypes) {
        this.argTypes = Arrays.asList(tokenTypes);
        this.returnType = returnType;
        this.method = method;
        this.varArgs = varArgs;
        this.usesQuery = usesQuery;
    }

    public Method getMethod() {
        return method;
    }

    public List<TokenTypes> getArgTypes() {
        return argTypes;
    }

    public Class<? extends Token> getReturnType() {
        return returnType;
    }

    public boolean hasVarArgs() {
        return varArgs;
    }

    public boolean usesQuery() {
        return usesQuery;
    }
}

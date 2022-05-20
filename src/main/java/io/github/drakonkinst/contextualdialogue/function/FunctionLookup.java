package io.github.drakonkinst.contextualdialogue.function;

import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenBoolean;
import io.github.drakonkinst.contextualdialogue.token.TokenFloat;
import io.github.drakonkinst.contextualdialogue.token.TokenInt;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import io.github.drakonkinst.contextualdialogue.token.TokenString;
import io.github.drakonkinst.contextualdialogue.token.TokenTypes;
import io.github.drakonkinst.contextualdialogue.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FunctionLookup {
    private final Map<String, FunctionSig> functions = new HashMap<>();
    
    public FunctionLookup() {
        initializeFunctions();
    }
    
    // Can be overridden by subclasses
    protected void initializeFunctions() {
        defineFunction("capitalize", TokenString.class, TokenTypes.STRING);
        defineFunction("decapitalize", TokenString.class, TokenTypes.STRING);
        defineFunction("upper", TokenString.class, TokenTypes.STRING);
        defineFunction("lower", TokenString.class, TokenTypes.STRING);
        defineFunction("subjective", TokenString.class, false, true, TokenTypes.STRING);
        defineFunction("objective", TokenString.class, false, true, TokenTypes.STRING);
        defineFunction("possessive", TokenString.class, false, true, TokenTypes.STRING);
        defineFunction("reflexive", TokenString.class, false, true, TokenTypes.STRING);
        defineFunction("list_concat", TokenList.class, true, false, TokenTypes.LIST, TokenTypes.LIST);
        defineFunction("prev", TokenString.class, false, true, TokenTypes.INTEGER);
        defineFunction("prev_match", TokenString.class, false, true, TokenTypes.INTEGER, TokenTypes.LIST);
        defineFunction("pluralize", TokenString.class, TokenTypes.INTEGER, TokenTypes.STRING, TokenTypes.STRING);
        defineFunction("count", TokenInt.class, TokenTypes.LIST);
        defineFunction("concat", TokenString.class, true, false, TokenTypes.STRING, TokenTypes.STRING);
        defineFunction("add", TokenFloat.class, TokenTypes.NUMBER, TokenTypes.NUMBER);
        defineFunction("sub", TokenFloat.class, TokenTypes.NUMBER, TokenTypes.NUMBER);
        defineFunction("mult", TokenFloat.class, TokenTypes.NUMBER, TokenTypes.NUMBER);
        defineFunction("div", TokenFloat.class, TokenTypes.NUMBER, TokenTypes.NUMBER);
        defineFunction("div_int", TokenInt.class, TokenTypes.INTEGER, TokenTypes.INTEGER);
        defineFunction("mod", TokenInt.class, TokenTypes.INTEGER, TokenTypes.INTEGER);
        defineFunction("to_int", TokenInt.class, TokenTypes.NUMBER);
        defineFunction("rand_int", TokenInt.class, TokenTypes.INTEGER);
        defineFunction("num", TokenString.class, TokenTypes.INTEGER);
        defineFunction("ord", TokenString.class, TokenTypes.INTEGER);
        defineFunction("gender", TokenString.class, TokenTypes.STRING, TokenTypes.STRING, TokenTypes.STRING, TokenTypes.STRING);
        defineFunction("if_else", TokenString.class, TokenTypes.BOOLEAN, TokenTypes.STRING, TokenTypes.STRING);
        defineFunction("not", TokenBoolean.class, TokenTypes.BOOLEAN);
        defineFunction("and", TokenBoolean.class, TokenTypes.BOOLEAN, TokenTypes.BOOLEAN);
        defineFunction("or", TokenBoolean.class, TokenTypes.BOOLEAN, TokenTypes.BOOLEAN);
    }

    protected void defineFunction(String name, Class<? extends Token> returnType, TokenTypes... argTypes) {
        defineFunction(Functions.class, name, returnType, false, false, argTypes);
    }
    
    protected void defineFunction(String name, Class<? extends Token> returnType, boolean varArgs, boolean usesQuery, TokenTypes... argTypes) {
        defineFunction(Functions.class, name, returnType, varArgs, usesQuery, argTypes);
    }

    protected void defineFunction(Class<?> functionClass, String name, Class<? extends Token> returnType, TokenTypes... argTypes) {
        defineFunction(functionClass, name, returnType, false, false, argTypes);
    }

    protected void defineFunction(Class<?> functionClass, String name, Class<? extends Token> returnType, boolean varArgs, boolean usesQuery, TokenTypes... argTypes) {
        if(varArgs && usesQuery) {
            throw new IllegalArgumentException("Function cannot pass both varargs and query");
        }

        String methodName = getMethodName(name);
        try {
            int size = argTypes.length;
            if(usesQuery) {
                ++size;
            }
            Class<?>[] argClasses = new Class<?>[size];
            for(int i = 0; i < argTypes.length; ++i) {
                argClasses[i] = argTypes[i].getDesiredType();
            }
            if(usesQuery) {
                argClasses[size - 1] = SpeechQuery.class;
            }
            if(varArgs) {
                //argClasses[size - 1] = Object[].class;
                argClasses[size - 1] = argClasses[size - 1].arrayType();
            }

            Method method = functionClass.getMethod(methodName, argClasses);
            FunctionSig sig = new FunctionSig(returnType, varArgs, usesQuery, method, argTypes);
            functions.put(methodName, sig);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    public FunctionSig getFunctionSig(String name) {
        return functions.get(name);
    }
    
    public boolean hasFunction(String name) {
        return functions.containsKey(name);    
    }
    
    private static String getMethodName(String name) {
        return StringUtils.snakeToCamel(name);
    }
}

package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.exception.SymbolException;
import io.github.drakonkinst.contextualdialogue.function.FunctionLookup;
import io.github.drakonkinst.contextualdialogue.function.FunctionSig;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenFunction;
import io.github.drakonkinst.contextualdialogue.token.TokenGroup;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import io.github.drakonkinst.contextualdialogue.token.TokenSymbol;
import io.github.drakonkinst.contextualdialogue.token.TokenTypes;

import java.util.List;
import java.util.Map;

public class SymbolChecker {
    private SymbolChecker() {}

    // Todo: Replace this
    public static final FunctionLookup functionLookup = new FunctionLookup();

    public static void test(Token token, Map<String, Token> symbols) throws SymbolException {
        fillSymbols(token, symbols);
        check(token, symbols);
        // TODO: have a fillFunction that fills in all tokens with their proper function
        // TODO: Implement caching for certain tokens
    }
    
    private static void check(Token token, Map<String, Token> symbols) throws SymbolException {     
        if(token instanceof TokenList list) {
            checkListTypes(list, symbols);
        } else if(token instanceof TokenGroup tokenGroup) {
            List<Token> tokens = tokenGroup.getTokens();
            for(Token t : tokens) {
                check(t, symbols);
            }
        } else if(token instanceof TokenSymbol symbol) {
            // Must be a predefined symbol if it is not replaced
            // We don't support those yet
            throw new SymbolException("Symbol was not replaced");
        } else if(token instanceof TokenFunction function) {
            FunctionSig sig = functionLookup.getFunctionSig(function.getName());
            if(sig == null) {
                throw new SymbolException("Unrecognized function \"" + function.getName() + "\"");
            }
            
            List<Token> argTokens = function.getArgs();
            List<TokenTypes> argTypes = sig.getArgTypes();
            if(argTokens.size() == argTypes.size()
                    || (sig.hasVarArgs() && argTokens.size() >= argTypes.size() - 1)) {
                // Number of arguments matches
                for(int i = 0; i < argTokens.size(); ++i) {
                    Token argToken = argTokens.get(i);
                    int argIndex = Math.min(argTypes.size() - 1, i);
                    TokenTypes expectedType = argTypes.get(argIndex);
                    
                    if(!expectedType.matchesType(inferType(argToken, symbols))) {
                        throw new SymbolException("Type mismatch for argument " + (i + 1) + " of call to function \"" + function.getName() + "\", expected " + expectedType);
                    }
                } 
            } else {
                throw new SymbolException("Expected " + argTypes.size() + " arguments for function \"" + function.getName() + "\", got " + argTokens.size() + " instead");
            }
        }
    }
    
    private static void fillSymbols(Token token, Map<String, Token> symbols) throws SymbolException {
        if(token instanceof TokenSymbol) {
            throw new SymbolException("Cannot fill root-level token");
        }
        
        if(token instanceof TokenGroup group) {
            fillSymbol(group.getTokens(), symbols);
        } else if(token instanceof TokenList list) {
            fillSymbol(list.getTokens(), symbols);
        } else if(token instanceof TokenFunction function) {
            fillSymbol(function.getArgs(), symbols);
        }
    }
    
    private static void fillSymbol(List<Token> tokenList, Map<String, Token> symbols) throws SymbolException {
        for (int i = 0; i < tokenList.size(); ++i) {
            if (tokenList.get(i) instanceof TokenSymbol symbol) {
                Token symbolValue = symbols.get(symbol.getName());
                if (symbolValue == null) {
                    throw new SymbolException("Unrecognized symbol \"" + symbol.getName() + "\"");
                }
                tokenList.set(i, symbolValue);
            }
            fillSymbols(tokenList.get(i), symbols);
        }
    }

    private static void checkListTypes(TokenList list, Map<String, Token> symbols) throws SymbolException {
        List<Token> tokens = list.getTokens();
        for (Token token : tokens) {
            Class<? extends Token> tokenClass = inferType(token, symbols);
            if (!TokenTypes.LIST_ITEM.matchesType(tokenClass)) {
                throw new SymbolException("Non-argument lists can only contain strings or integers");
            }
        }
    }

    // Assumes symbol or function exists
    private static Class<? extends Token> inferType(Token token, Map<String, Token> symbols) throws SymbolException {
        if (token instanceof TokenSymbol symbol) {
            Token symbolValue = symbols.get(symbol.getName());
            return inferType(symbolValue, symbols);
        } else if (token instanceof TokenFunction function) {
            return functionLookup.getFunctionSig(function.getName()).getReturnType();
        }
        return token.getClass();
    }
}

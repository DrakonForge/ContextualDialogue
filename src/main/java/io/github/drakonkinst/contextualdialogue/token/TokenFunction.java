package io.github.drakonkinst.contextualdialogue.token;

import io.github.drakonkinst.contextualdialogue.context.FactType;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.function.FunctionSig;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.util.NumericalSpeech;
import io.github.drakonkinst.contextualdialogue.util.StringCache;
import io.github.drakonkinst.commonutil.StringUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.lang.reflect.Array;
import java.util.List;

public class TokenFunction implements Token {
    private final String name;
    private final List<Token> args;

    public TokenFunction(String name, List<Token> args) {
        this.name = StringUtils.snakeToCamel(name);
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<Token> getArgs() {
        return args;
    }

    public Object call(SpeechQuery query) throws SpeechException {
        FunctionSig sig = query.getFunctionSig(name);
        if(sig.hasVarArgs()) {
            return callWithVarArgs(sig, query);
        }

        List<TokenTypes> argTypes = sig.getArgTypes();
        int numArgs = argTypes.size();

        if(args.size() != numArgs) {
            throw new SpeechException("Function \"" + name + "\" called with wrong number of arguments");
        }

        if(sig.usesQuery()) {
            numArgs += 1;
        }
        Object[] argValues = new Object[numArgs];

        for(int i = 0; i < numArgs; ++i) {
            if(i == numArgs - 1 && sig.usesQuery()) {
                argValues[i] = query;
            } else {
                argValues[i] = handleArg(args.get(i), argTypes.get(i), query);
            }
        }

        if(sig.hasVarArgs()) {
            int varArgLen = args.size() - numArgs;
            Object[] varArgs = new Object[varArgLen];
            TokenTypes varArgType = argTypes.get(numArgs - 1);
            for(int i = 0; i < varArgLen; ++i) {
                varArgs[i] = handleArg(args.get(i + numArgs), varArgType, query);
            }
            argValues[numArgs - 1] = varArgs;
        }

        try {
            return sig.getMethod().invoke(null, argValues);
        } catch (Exception e) {
            throw new SpeechException("Function call for \"" + name + "\" failed: " + e.getMessage());
        }
    }

    private Object callWithVarArgs(FunctionSig sig, SpeechQuery query) throws SpeechException {
        List<TokenTypes> argTypes = sig.getArgTypes();
        int numArgs = argTypes.size();
        int numNormalArgs = numArgs - 1;
        Object[] argValues = new Object[numArgs];

        if(args.size() < numNormalArgs) {
            throw new SpeechException("Function \"" + name + "\" called with not enough arguments");
        }

        // Normal arguments
        for(int i = 0; i < numNormalArgs; ++i) {
            argValues[i] = handleArg(args.get(i), argTypes.get(i), query);
        }

        TokenTypes varArgType = argTypes.get(numArgs - 1);
        int numVarArgs = args.size() - numNormalArgs;
        // Reflections are witchcraft and I have delved into it
        Object varArgs = Array.newInstance(varArgType.getDesiredType(), numVarArgs);
        for(int i = 0; i < numVarArgs; ++i) {
            Array.set(varArgs, i, handleArg(args.get(i + numNormalArgs), varArgType, query));
        }
        argValues[numArgs - 1] = varArgType.getDesiredType().arrayType().cast(varArgs);

        try {
            return sig.getMethod().invoke(null, argValues);
        } catch (Exception e) {
            throw new SpeechException("Function call for \"" + name + "\" failed: " + e.getMessage());
        }
    }

    @Override
    public String evaluate(SpeechQuery query) throws SpeechException {
        Object result = call(query);
        if(result instanceof String strResult) {
            return strResult;
        } else if(result instanceof Integer intResult) {
            return NumericalSpeech.integerToWord(intResult);
        } else if(result instanceof Float floatResult) {
            int value = floatResult.intValue();
            return NumericalSpeech.integerToWord(value);
        }
        throw new SpeechException("Cannot evaluate this function return type");
    }

    private Object handleArg(Token token, TokenTypes type, SpeechQuery query) throws SpeechException {
        if (type == TokenTypes.STRING) {
            return token.evaluate(query);
        } else if (type == TokenTypes.LIST) {
            if (token instanceof TokenFunction function) {
                Object result = function.call(query);
                if (result instanceof IntList) {
                    return result;
                }
            }
            if (token instanceof TokenContext context) {
                return new IntArrayList(query.getContextAsList(context.getTable(), context.getKey()));
            }
            if (token instanceof TokenList list) {
                IntList tokenList = new IntArrayList();
                List<Token> tokens = list.getTokens();
                for(Token t : tokens) {
                    Object item = handleArg(t, TokenTypes.LIST_ITEM, query);
                    if(item instanceof String str) {
                        tokenList.add(StringCache.cacheString(str));
                    } else if(item instanceof Integer integer) {
                        tokenList.add(integer.intValue());
                    } else {
                        throw new SpeechException("Unsupported token in list item");
                    }
                }
                return tokenList;
            }
        } else if (type == TokenTypes.INTEGER) {
            if(token instanceof TokenFunction function) {
                Object result = function.call(query);
                if(result instanceof Integer) {
                    return result;
                }
            }
            if (token instanceof TokenContext context) {
                return (int) query.getContextAsFloat(context.getTable(), context.getKey());
            }
            if(token instanceof TokenInt integer) {
                return integer.getValue();
            }
        } else if (type == TokenTypes.NUMBER) {
            if(token instanceof TokenFunction function) {
                Object result = function.call(query);
                if(result instanceof Float) {
                    return result;
                }
            }
            if (token instanceof TokenContext context) {
                return query.getContextAsFloat(context.getTable(), context.getKey());
            }
            if (token instanceof TokenInt integer) {
                return (float) integer.getValue();
            }
            if (token instanceof TokenFloat number) {
                return number.getValue();
            }
        } else if (type == TokenTypes.BOOLEAN) {
            if(token instanceof TokenFunction function) {
                Object result = function.call(query);
                if(result instanceof Boolean) {
                    return result;
                }
            }
            if (token instanceof TokenContext context) {
                return query.getContextAsBoolean(context.getTable(), context.getKey());
            }
            if (token instanceof TokenBoolean bool) {
                return bool.getValue();
            }
        } else if (type == TokenTypes.LIST_ITEM) {
            if (token instanceof TokenFunction function) {
                Object result = function.call(query);
                if(result instanceof String || result instanceof Integer) {
                    return result;
                }
            }
            if(token instanceof TokenContext context) {
                String table = context.getTable();
                String key = context.getKey();
                FactType factType = query.getContextType(table, key);
                if(factType == FactType.STRING) {
                    return query.getContextAsString(table, key);
                }
                if(factType == FactType.NUMBER) {
                    return (int) query.getContextAsFloat(table, key);
                }
            }
            if(token instanceof TokenInt integer) {
                return integer.getValue();
            }
            if(token instanceof TokenString string) {
                return string.getValue();
            }
        } else {
            throw new SpeechException("Unsupported function argument type");
        }
        throw new SpeechException("Unsupported token in function argument");
    }

    @Override
    public String toString() {
        return "{Function " + name + " args=" + args.toString() + "}";
    }
}

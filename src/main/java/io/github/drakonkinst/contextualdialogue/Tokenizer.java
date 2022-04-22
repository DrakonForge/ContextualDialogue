package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.contextualdialogue.exception.TokenizeException;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenContext;
import io.github.drakonkinst.contextualdialogue.token.TokenFloat;
import io.github.drakonkinst.contextualdialogue.token.TokenFunction;
import io.github.drakonkinst.contextualdialogue.token.TokenGroup;
import io.github.drakonkinst.contextualdialogue.token.TokenInt;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import io.github.drakonkinst.contextualdialogue.token.TokenString;
import io.github.drakonkinst.contextualdialogue.token.TokenSymbol;
import io.github.drakonkinst.contextualdialogue.commonutil.Ref;

import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {
    private Tokenizer() {}
    
    private static final char SYMBOL_START = '@';
    private static final char CONTEXT_START = '#';
    private static final char LIST_START = '[';
    private static final char LIST_END = ']';
    private static final char ARGS_START = '(';
    private static final char ARGS_END = ')';
    private static final char TABLE_SEP = '.';
    private static final char PAUSE = '_';
    private static final char ENDLINE = '/';
    private static final char ESCAPE = '\\';
    private static final char SPACE = ' ';

    public static Token tokenize(String text) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        List<Token> tokens = new ArrayList<>();
        boolean consumeSpaces = false;
        
        while(index < text.length()) {
            char c = text.charAt(index);
            consumeSpaces = consumeSpaces && c == SPACE;

            if(c == SPACE && consumeSpaces) {
                ++index;
            } else if(c == ESCAPE) {
                char nextChar = text.charAt(index + 1);
                sb.append(nextChar);
                index += 2;
            } else if(c == ENDLINE) {
                deleteTrailingSpaces(sb);
                if(sb.isEmpty()) {
                    throw new TokenizeException("Line separator should not separate blank lines");
                }
                sb.append(ENDLINE);
                consumeSpaces = true;
                ++index;
            } else if(c == PAUSE) {
                if(!sb.isEmpty() && sb.charAt(sb.length() - 1) == SPACE) {
                    throw new TokenizeException("Spaces should not precede a pause");
                }
                index = tokenizePause(text, index, sb);
            } else if (c == SYMBOL_START) {
                finishStringToken(sb, tokens);
                sb = new StringBuilder();
                index = tokenizeSymbol(text, index, tokens);
            } else if(c == CONTEXT_START) {
                finishStringToken(sb, tokens);
                sb = new StringBuilder();
                index = tokenizeContext(text, index, tokens);
            } else if(c == LIST_START) {
                finishStringToken(sb, tokens);
                sb = new StringBuilder();
                Ref<TokenList> tokenRef = new Ref<>();
                index = tokenizeList(text, index, tokenRef, LIST_END);
                if(tokenRef.exists()) {
                    tokens.add(tokenRef.get());
                }
            } else {
                sb.append(c);
                ++index;
            }
        }
        if(!sb.isEmpty()) {
            char lastChar = sb.charAt(sb.length() - 1);
            if(lastChar == PAUSE || lastChar == ENDLINE) {
                throw new TokenizeException("Speech line should not end on a pause or line separator");
            }
        }
        finishStringToken(sb, tokens);

        if(tokens.isEmpty()) {
            throw new TokenizeException("Speech line should not be empty");
        }
        return new TokenGroup(tokens);
    }
    
    private static int tokenizeContext(String text, int index, List<Token> tokens) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        String table = null;
        String context;
        char currentChar = 0;
        char nextChar;
        int i;
        
        for(i = index; i < text.length(); ++i) {
            nextChar = charAt(text, i + 1);
            
            if(currentChar != 0) {
                // Not first character
                if(nextChar == 0 && currentChar == TABLE_SEP) {
                    // Last character is a table separator, does not belong in token
                    i -= 1;
                    break;
                }
                sb.append(currentChar);
            }
            
            if(nextChar == TABLE_SEP) {
                if(currentChar == 0) {
                    throw new TokenizeException("Context cannot start with '.'");
                }
                
                if(!isIdChar(currentChar)) {
                    throw new TokenizeException("'.' can only follow a letter, digit, or underscore");
                }
                
                if(table == null) {
                    // Set table
                    table = sb.toString();
                    sb = new StringBuilder();
                    nextChar = charAt(text, ++i + 1);
                    
                    if (table.isEmpty()) {
                        throw new TokenizeException("Table cannot be empty");
                    }
                }
            } else if(!isIdChar(nextChar)) {
                // Invalid token
                break;
            }
            
            currentChar = nextChar;
        }
        
        context = sb.toString();

        if (context.isEmpty()) {
            throw new TokenizeException("Context cannot be empty");
        }
        tokens.add(new TokenContext(table, context));
        return i + 1;
    }
    
    private static int tokenizeSymbol(String text, int index, List<Token> tokens) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        String name;
        int i;
        for(i = index + 1; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            
            if(isIdChar(currentChar)) {
                sb.append(currentChar);
            } else if(currentChar == ARGS_START) {
                name = sb.toString();
                if(name.isEmpty()) {
                    throw new TokenizeException("Function name cannot be empty");
                }
                
                // Read arguments
                Ref<TokenList> tokenRef = new Ref<>();
                int nextIndex = tokenizeList(text, i, tokenRef, ARGS_END);
                TokenList args = tokenRef.get();
                
                // Create token
                tokens.add(new TokenFunction(name, args.getTokens()));
                return nextIndex;
            } else {
                break;
            }
        }
        
        name = sb.toString();
        if (name.isEmpty()) {
            throw new TokenizeException("Symbol name cannot be empty");
        }
        tokens.add(new TokenSymbol(name));
        return i;
    }
    
    private static int tokenizeList(String text, int index, Ref<TokenList> tokenRef, char endChar) throws TokenizeException {
        List<Token> tokens = new ArrayList<>();
        boolean finishedItem = false;
        index += 1;
        while(index < text.length()) {
            char currentChar = text.charAt(index);
            if (currentChar == endChar) {
                tokenRef.set(new TokenList(tokens));
                return index + 1;
            }
            if (finishedItem) {
                // Expect comma or end of list
                if(currentChar == ',') { 
                    finishedItem = false;
                    ++index;
                } else {
                    throw new TokenizeException("Expected comma or end of list but found '" + currentChar + "'");
                }
            } else if (currentChar == CONTEXT_START) {
                index = tokenizeContext(text, index, tokens);
                finishedItem = true;
            } else if(currentChar == SYMBOL_START) {
                index = tokenizeSymbol(text, index, tokens);
                finishedItem = true;
            } else if (Character.isDigit(currentChar) || currentChar == '-') {
                index = tokenizeNumber(text, index, tokens);
                finishedItem = true;
            } else if (currentChar == '"') {
                index = tokenizeString(text, index, tokens);
                finishedItem = true;
            } else if (currentChar == LIST_START) {
                Ref<TokenList> innerList = new Ref<>();
                index = tokenizeList(text, index, innerList, LIST_END);
                if(innerList.exists()) {
                    tokens.add(innerList.get());
                }
            } else if (currentChar == SPACE) {
                ++index;
            } else {
                throw new TokenizeException("Unexpected character in function arguments '" + currentChar + "'");
            }
        }
        
        throw new TokenizeException("Unfinished list, never encountered '" + endChar + "'");
    }
    
    private static int tokenizeNumber(String text, int index, List<Token> tokens) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;
        int i = index;

        if(text.charAt(i) == '-') {
            sb.append('-');
            i += 1;
        }

        while(i < text.length()) {
            char currentChar = text.charAt(i);

            if(Character.isDigit(currentChar)) {
                sb.append(currentChar);
            } else if(currentChar == '.') {
                if(isFloat) {
                    throw new TokenizeException("Number cannot contain multiple decimal points");
                }
                
                isFloat = true;
                sb.append(currentChar);
            } else {
                break;
            }
            ++i;
        }
        
        String str = sb.toString();
        if(str.isEmpty()) {
            throw new TokenizeException("Empty number");
        }
        if (isFloat) {
            tokens.add(new TokenFloat(Float.parseFloat(str)));
        } else {
            tokens.add(new TokenInt(Integer.parseInt(str)));
        }
        return i;
    }
    
    private static int tokenizeString(String text, int index, List<Token> tokens) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        for (int i = index + 1; i < text.length(); ++i) {
            char currentChar = text.charAt(i);

            if (currentChar == '\\') {
                if (i >= text.length() - 1) {
                    throw new TokenizeException("Invalid use of backslash character");
                }
                sb.append(text.charAt(++i));
            } else if (currentChar == '"') {
                tokens.add(new TokenString(sb.toString()));
                return i + 1;
            } else {
                sb.append(currentChar);
            }
        }
        throw new TokenizeException("Unfinished string");
    }

    private static int tokenizePause(String text, int index, StringBuilder sb) throws TokenizeException {
        for(int i = index; i < text.length(); ++i) {
            if(text.charAt(i) == PAUSE) {
                sb.append(PAUSE);
            } else {
                return i;
            }
        }
        throw new TokenizeException("Pause should not end a speech line");
    }
    
    // Helpers
    
    private static void finishStringToken(StringBuilder sb, List<Token> tokens) {
        if(sb.isEmpty()) {
            return;
        }
        tokens.add(new TokenString(sb.toString()));
    }

    private static void deleteTrailingSpaces(StringBuilder sb) {
        while(sb.length() > 0 && sb.charAt(sb.length() - 1) == SPACE) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private static char charAt(String s, int index) {
        if(index < 0 || index >= s.length()) {
            return 0;
        }
        return s.charAt(index);
    }
    
    private static boolean isIdChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }
}
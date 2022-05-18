package io.github.drakonkinst.contextualdialogue;

import io.github.drakonkinst.contextualdialogue.commonutil.Ref;
import io.github.drakonkinst.contextualdialogue.exception.TokenizeException;
import io.github.drakonkinst.contextualdialogue.speech.text.TextFormat;
import io.github.drakonkinst.contextualdialogue.speech.text.TextFormatBool;
import io.github.drakonkinst.contextualdialogue.speech.text.TextFormatFloat;
import io.github.drakonkinst.contextualdialogue.speech.text.TextFormatInt;
import io.github.drakonkinst.contextualdialogue.speech.text.TextFormatString;
import io.github.drakonkinst.contextualdialogue.token.Token;
import io.github.drakonkinst.contextualdialogue.token.TokenContext;
import io.github.drakonkinst.contextualdialogue.token.TokenFloat;
import io.github.drakonkinst.contextualdialogue.token.TokenFunction;
import io.github.drakonkinst.contextualdialogue.token.TokenGroup;
import io.github.drakonkinst.contextualdialogue.token.TokenInt;
import io.github.drakonkinst.contextualdialogue.token.TokenList;
import io.github.drakonkinst.contextualdialogue.token.TokenString;
import io.github.drakonkinst.contextualdialogue.token.TokenSymbol;

import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {
    private Tokenizer() {}

    private static final char SYMBOL_START = '@';
    private static final char CONTEXT_START = '#';
    private static final char ARGS_START = '(';
    private static final char ARGS_END = ')';
    private static final char ARGS_SEP = ',';
    private static final char TABLE_SEP = '.';
    private static final char PAUSE = '_';
    private static final char LINEBREAK = '/';
    private static final char ESCAPE = '\\';
    private static final char SPACE = ' ';
    private static final char STAR = '*';
    private static final char FORMAT_START = '{';
    private static final char FORMAT_SEP = ',';
    private static final char FORMAT_ASSIGN = '=';
    private static final char FORMAT_END = '}';
    private static final String PAUSE_ATTR = "pause";
    private static final String LINEBREAK_ATTR = "linebreak";
    private static final String BOLD_ATTR = "bold";
    private static final String ITALICS_ATTR = "italics";

    public static TokenGroup tokenize(String text) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        int numStars = 0;
        boolean italics = false;
        boolean bold = false;
        List<Token> tokens = new ArrayList<>();
        boolean consumeSpaces = false;

        while(index < text.length()) {
            char c = text.charAt(index);
            consumeSpaces = consumeSpaces && c == SPACE;

            if(c != STAR && numStars > 0) {
                if(italics) {
                    tokens.add(TextFormatBool.ITALICS_OFF);
                    italics = false;
                } else {
                    tokens.add(TextFormatBool.ITALICS_ON);
                    italics = true;
                }
                numStars = 0;
            }

            if(c == SPACE && consumeSpaces) {
                ++index;
            } else if(c == ESCAPE) {
                if(index < text.length() - 1) {
                    char nextChar = text.charAt(index + 1);
                    sb.append(nextChar);
                    index += 2;
                } else {
                    throw new TokenizeException("Nothing after escape character");
                }
            } else if(c == LINEBREAK) {
                deleteTrailingSpaces(sb);
                if(sb.isEmpty()) {
                    throw new TokenizeException("Line break should not separate blank lines");
                }
                sb.append(TextFormat.LINEBREAK);
                consumeSpaces = true;
                ++index;
            } else if(c == PAUSE) {
                if(!sb.isEmpty() && sb.charAt(sb.length() - 1) == SPACE) {
                    throw new TokenizeException("Spaces should not precede a pause");
                }
                index = tokenizePause(text, index, tokens);
            } else if(c == SYMBOL_START) {
                finishStringToken(sb, tokens);
                index = tokenizeSymbol(text, index, tokens);
            } else if(c == CONTEXT_START) {
                finishStringToken(sb, tokens);
                index = tokenizeContext(text, index, tokens);
            } else if(c == FORMAT_START) {
                finishStringToken(sb, tokens);
                index = tokenizeFormat(text, index, tokens);
                // TODO implement
                // TODO: Prevent use of pause, linebreak, bold, or italics attributes
            } else if(c == STAR) {
                finishStringToken(sb, tokens);
                ++numStars;
                ++index;
                if(numStars == 2) {
                    if(bold) {
                        tokens.add(TextFormatBool.BOLD_OFF);
                        bold = false;
                    } else {
                        tokens.add(TextFormatBool.BOLD_ON);
                        bold = true;
                    }
                    numStars = 0;
                }
            } else {
                sb.append(c);
                ++index;
            }
        }

        if(italics && numStars == 1) {
            tokens.add(TextFormatBool.ITALICS_OFF);
            italics = false;
        }
        finishStringToken(sb, tokens);
        lintTokens(tokens, italics, bold);
        return new TokenGroup(tokens);
    }

    public static void lintTokens(List<Token> tokens, boolean italics, boolean bold) throws TokenizeException {
        if(tokens.isEmpty()) {
            throw new TokenizeException("Speech line should not be empty");
        }
        if(italics) {
            throw new TokenizeException("Italics formatting not closed properly");
        }
        if(bold) {
            throw new TokenizeException("Bold formatting not closed properly");
        }

        for(int i = 0; i < tokens.size() - 1; ++i) {
            Token thisToken = tokens.get(i);
            Token nextToken = tokens.get(i + 1);
            if(thisToken == TextFormatBool.BOLD_ON && nextToken == TextFormatBool.BOLD_OFF) {
                throw new TokenizeException("Bold formatting on empty string is redundant");
            }
            if(thisToken == TextFormatBool.ITALICS_ON && nextToken == TextFormatBool.ITALICS_OFF) {
                throw new TokenizeException("Italics formatting on empty string is redundant");
            }
        }

        Token lastToken = tokens.get(tokens.size() - 1);
        if(lastToken == TextFormat.LINEBREAK) {
            throw new TokenizeException("Speech line should not end on a linebreak");
        }
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
                    sb.setLength(0);
                    nextChar = charAt(text, ++i + 1);

                    if(table.isEmpty()) {
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

        if(context.isEmpty()) {
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
                int nextIndex = tokenizeArgs(text, i, tokenRef);
                TokenList args = tokenRef.get();

                // Create token
                tokens.add(new TokenFunction(name, args.getTokens()));
                return nextIndex;
            } else {
                break;
            }
        }

        name = sb.toString();
        if(name.isEmpty()) {
            throw new TokenizeException("Symbol name cannot be empty");
        }
        tokens.add(new TokenSymbol(name));
        return i;
    }

    private static int tokenizeArgs(String text, int index, Ref<TokenList> tokenRef) throws TokenizeException {
        List<Token> tokens = new ArrayList<>();
        boolean finishedItem = false;
        index += 1;
        while(index < text.length()) {
            char currentChar = text.charAt(index);
            if(currentChar == ARGS_END) {
                tokenRef.set(new TokenList(tokens));
                return index + 1;
            }
            if(finishedItem) {
                // Expect comma or end of list
                if(currentChar == ARGS_SEP) {
                    finishedItem = false;
                    ++index;
                } else {
                    throw new TokenizeException("Expected comma or end of list but found '" + currentChar + "'");
                }
            } else if(currentChar == CONTEXT_START) {
                index = tokenizeContext(text, index, tokens);
                finishedItem = true;
            } else if(currentChar == SYMBOL_START) {
                index = tokenizeSymbol(text, index, tokens);
                finishedItem = true;
            } else if(Character.isDigit(currentChar)
                    // 1-char lookahead needed to differentiate between "-" and the start of a negative number
                    || (currentChar == '-' && index < text.length() - 1 && Character.isDigit(text.charAt(index + 1)))) {
                index = tokenizeNumber(text, index, tokens);
                finishedItem = true;
            } else if(currentChar == SPACE) {
                ++index;
            } else {
                // Assume it is a string
                index = tokenizeString(text, index, tokens);
                finishedItem = true;
            }
        }

        throw new TokenizeException("Unfinished list, never encountered '" + ARGS_END + "'");
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
        if(isFloat) {
            tokens.add(new TokenFloat(Float.parseFloat(str)));
        } else {
            tokens.add(new TokenInt(Integer.parseInt(str)));
        }
        return i;
    }

    private static int tokenizeString(String text, int index, List<Token> tokens) throws TokenizeException {
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < text.length(); ++i) {
            char currentChar = text.charAt(i);

            if(currentChar == '\\') {
                if(i >= text.length() - 1) {
                    throw new TokenizeException("Invalid use of backslash character");
                }
                sb.append(text.charAt(++i));
            } else if(currentChar == ARGS_SEP || currentChar == ARGS_END) {
                tokens.add(new TokenString(sb.toString()));
                return i;
            } else {
                sb.append(currentChar);
            }
        }
        throw new TokenizeException("Unfinished string");
    }

    private static int tokenizePause(String text, int index, List<Token> tokens) throws TokenizeException {
        int pauseLength = 0;
        for(int i = index; i < text.length(); ++i) {
            if(text.charAt(i) == PAUSE) {
                ++pauseLength;
            } else {
                tokens.add(new TextFormatInt(PAUSE_ATTR, pauseLength));
                return i;
            }
        }
        throw new TokenizeException("Speech line should not end on a pause");
    }

    private static int tokenizeFormat(String text, int index, List<Token> tokens) throws TokenizeException {
        index += 1;
        StringBuilder sb = new StringBuilder();
        boolean consumeSpaces = true;
        String attribute = null;
        while(index < text.length()) {
            char currentChar = text.charAt(index);
            if(currentChar != SPACE) {
                consumeSpaces = false;
            }
            if(currentChar == FORMAT_ASSIGN) {
                deleteTrailingSpaces(sb);
                if(attribute != null) {
                    throw new TokenizeException("Cannot set two attributes at once");
                }
                if(sb.isEmpty()) {
                    throw new TokenizeException("Empty attribute");
                }
                attribute = sb.toString();
                validateAttributeName(attribute);
                sb.setLength(0);
                consumeSpaces = true;
                ++index;
            } else if(currentChar == FORMAT_SEP || currentChar == FORMAT_END) {
                deleteTrailingSpaces(sb);
                // Expect comma or end of list
                if(sb.isEmpty()) {
                    if(attribute == null) {
                        throw new TokenizeException("Empty attribute");
                    } else {
                        throw new TokenizeException("Empty value");
                    }
                }
                String str = sb.toString();
                sb.setLength(0);
                if(attribute == null) {
                    validateAttributeName(str);
                    tokens.add(new TextFormat(str));
                } else {
                    // Decide if boolean, int, float, or string
                    tokens.add(createTextFormatToken(attribute, str));
                    attribute = null;
                }

                if(currentChar == FORMAT_END) {
                    return index + 1;
                } else {
                    ++index;
                    consumeSpaces = true;
                }
            } else if(currentChar == SPACE && consumeSpaces) {
                ++index;
            } else if(currentChar == ESCAPE) {
                if(index < text.length() - 1) {
                    char nextChar = text.charAt(index + 1);
                    sb.append(nextChar);
                    index += 2;
                } else {
                    throw new TokenizeException("Nothing after escape character");
                }
            } else {
                sb.append(currentChar);
                ++index;
            }
        }

        throw new TokenizeException("Formatting block was not closed properly");
    }

    // Helpers

    private static void validateAttributeName(String attribute) throws TokenizeException {
        switch (attribute) {
            case ITALICS_ATTR -> throw new TokenizeException("Italics formatting should use '*' instead of being explicit");
            case BOLD_ATTR -> throw new TokenizeException("Bold formatting should use '**' instead of being explicit");
            case LINEBREAK_ATTR -> throw new TokenizeException("Linebreak formatting should use '/' instead of being explicit");
            case PAUSE_ATTR -> throw new TokenizeException("Pause formatting should use '_' instead of being explicit");
        }
    }

    private static TextFormat createTextFormatToken(String attribute, String value) {
        if(value.equals("true") || value.equals("false")) {
            return new TextFormatBool(attribute, Boolean.parseBoolean(value));
        }
        try {
            int intVal = Integer.parseInt(value);
            return new TextFormatInt(attribute, intVal);
        } catch(NumberFormatException e) {
            // Not an integer
        }
        try {
            float floatVal = Float.parseFloat(value);
            return new TextFormatFloat(attribute, floatVal);
        } catch(NumberFormatException e) {
            // Not a float
        }
        return new TextFormatString(attribute, value);
    }
    private static void finishStringToken(StringBuilder sb, List<Token> tokens) {
        if(sb.isEmpty()) {
            return;
        }
        tokens.add(new TokenString(sb.toString()));
        sb.setLength(0);
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
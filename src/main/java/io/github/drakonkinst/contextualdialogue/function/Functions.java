package io.github.drakonkinst.contextualdialogue.function;

import io.github.drakonkinst.contextualdialogue.commonutil.FastMath;
import io.github.drakonkinst.contextualdialogue.exception.SpeechException;
import io.github.drakonkinst.contextualdialogue.speech.SpeechQuery;
import io.github.drakonkinst.contextualdialogue.util.StringCache;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public final class Functions {
    private Functions() {}

    private static final String GENDER_MALE = "male";
    private static final String GENDER_FEMALE = "female";
    private static final String GENDER_NONE = "none";

    private static final String[] SUBJECTIVE = { "he", "she", "they" };
    private static final String[] OBJECTIVE = { "him", "her", "them" };
    private static final String[] POSSESSIVE = { "his", "hers", "theirs" };
    private static final String[] REFLEXIVE = { "himself", "herself", "themself" };

    private static int genderToInt(String gender) {
        if(gender.equals(GENDER_MALE)) {
            return 0;
        }
        if(gender.equals(GENDER_FEMALE)) {
            return 1;
        }
        if(gender.equals(GENDER_NONE)) {
            return 2;
        }
        return -1;
    }

    public static String capitalize(String s) {
        if(s.length() >= 2) {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        return s.toUpperCase();
    }
    
    public static String decapitalize(String s) {
        if(s.length() >= 2) {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
        return s.toLowerCase();
    }

    public static String upper(String s) {
        return s.toUpperCase();
    }

    public static String lower(String s) {
        return s.toLowerCase();
    }

    public static String subjective(String gender, SpeechQuery query) throws SpeechException {
        int index = genderToInt(gender);
        if(index > -1) {
            return SUBJECTIVE[index];
        }
        throw new SpeechException("Custom genders not yet supported");
    }
    
    public static String objective(String gender, SpeechQuery query) throws SpeechException {
        int index = genderToInt(gender);
        if(index > -1) {
            return OBJECTIVE[index];
        }
        throw new SpeechException("Custom genders not yet supported");
    }
    
    public static String possessive(String gender, SpeechQuery query) throws SpeechException {
        int index = genderToInt(gender);
        if(index > -1) {
            return POSSESSIVE[index];
        }
        throw new SpeechException("Custom genders not yet supported");
    }
    
    public static String reflexive(String gender, SpeechQuery query) throws SpeechException {
        int index = genderToInt(gender);
        if(index > -1) {
            return REFLEXIVE[index];
        }
        throw new SpeechException("Custom genders not yet supported");
    }
    
    public static IntList listConcat(IntList first, IntList... more) {
        IntList result = new IntArrayList(first);
        for(IntList other : more) {
            result.addAll(other);
        }
        return result;
    }
    
    public static String prev(int index, SpeechQuery query) throws SpeechException {
        return query.getPrevChoice(index);
    }

    public static String prevMatch(int index, IntList list, SpeechQuery query) throws SpeechException {
        int choiceIndex = query.getPrevChoiceIndex(index);
        if(index >= list.size()) {
            throw new SpeechException("Index out of bounds: " + index);
        }
        int matchingChoice = list.getInt(choiceIndex);
        return StringCache.lookup(matchingChoice);
    }

    public static String pluralize(int count, String singular, String plural) {
        if(count == 1) {
            return singular;
        }
        return plural;
    }

    public static int count(IntList list) {
        return list.size();
    }

    public static String concat(String first, String... more) {
        StringBuilder sb = new StringBuilder(first);
        for(String other : more) {
            sb.append(other);
        }
        return sb.toString();
    }

    public static float add(float a, float b) {
        return a + b;
    }

    public static float sub(float a, float b) {
        return a - b;
    }

    public static float mult(float a, float b) {
        return a * b;
    }

    public static float div(float a, float b) throws SpeechException {
        if(b == 0) {
            throw new SpeechException("Division by zero");
        }
        return a / b;
    }

    public static int divInt(int a, int b) throws SpeechException {
        if(b == 0) {
            throw new SpeechException("Division by zero");
        }
        return a / b;
    }

    public static int mod(int a, int b) throws SpeechException {
        if(b == 0) {
            throw new SpeechException("Division by zero");
        }
        return a % b;
    }

    public static int randInt(int max) {
        return FastMath.randInt(max);
    }

    public static int toInt(float number) {
        return (int) number;
    }

    public static String gender(String gender, String maleStr, String femaleStr, String neutralStr) throws SpeechException {
        int index = genderToInt(gender);
        return switch (index) {
            case 0 -> maleStr;
            case 1 -> femaleStr;
            case 2 -> neutralStr;
            default -> throw new SpeechException("Custom genders not yet supported");
        };
    }

    public static String ifElse(boolean condition, String ifTrue, String ifFalse) {
        if(condition) {
            return ifTrue;
        }
        return ifFalse;
    }

    public static boolean and(boolean a, boolean b) {
        return a && b;
    }

    public static boolean or(boolean a, boolean b) {
        return a || b;
    }

    public static boolean not(boolean a) {
        return !a;
    }
}

package io.github.drakonkinst.contextualdialogue.util;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;

import java.util.HashMap;
import java.util.Map;

// Utility class that provides methods to convert natural numbers into their text version
public final class NumericalSpeech {
    private static final String ZERO = "zero";
    private static final String HUNDRED = "hundred ";
    private static final String THOUSAND = "thousand ";
    private static final String MILLION = "million ";
    private static final String BILLION = "billion ";
    private static final String[] BELOW_TWENTY = {
            "", "one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };
    private static final String[] TENS = {
            "", "ten", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety"
    };
    private static final int VAL_TEN = 10;
    private static final int VAL_TWENTY = 20;
    private static final int VAL_HUNDRED = 100;
    private static final int VAL_THOUSAND = 1000;
    private static final int VAL_MILLION = 1000000;
    private static final int VAL_BILLION = 1000000000;

    private static final Map<String,String> ORDINAL_MAP = new HashMap<>();
    static {
        ORDINAL_MAP.put("one", "first");
        ORDINAL_MAP.put("two", "second");
        ORDINAL_MAP.put("three", "third");
        ORDINAL_MAP.put("five", "fifth");
        ORDINAL_MAP.put("eight", "eighth");
        ORDINAL_MAP.put("nine", "ninth");
        ORDINAL_MAP.put("twelve", "twelfth");
    }

    private NumericalSpeech() {}

    // https://evelynn.gitbooks.io/facebook-interview/content/integer_to_english_words.html
    public static String integerToWord(int num) throws SpeechException {
        if(num < 0) {
            throw new SpeechException("Cannot convert negative numbers to words!");
        }

        if(num == 0) {
            return ZERO;
        }
        return helper(num).trim();
    }

    // https://rosettacode.org/wiki/Spelling_of_ordinal_numbers
    public static String integerToOrdinal(int num) throws SpeechException {
        String numWord = integerToWord(num);
        return integerWordToOrdinal(numWord);
    }

    public static String integerWordToOrdinal(String numWord) {
        String[] split = numWord.split(" ");
        String last = split[split.length - 1];
        String replace = "";
        if(last.contains("-") ) {
            String[] lastSplit = last.split("-");
            String lastWithDash = lastSplit[1];
            String lastReplace = "";
            if(ORDINAL_MAP.containsKey(lastWithDash) ) {
                lastReplace = ORDINAL_MAP.get(lastWithDash);
            }
            else if( lastWithDash.endsWith("y") ) {
                lastReplace = lastWithDash.substring(0, lastWithDash.length() - 1) + "ieth";
            }
            else {
                lastReplace = lastWithDash + "th";
            }
            replace = lastSplit[0] + "-" + lastReplace;
        }
        else {
            if(ORDINAL_MAP.containsKey(last) ) {
                replace = ORDINAL_MAP.get(last);
            }
            else if(last.endsWith("y") ) {
                replace = last.substring(0, last.length() - 1) + "ieth";
            }
            else {
                replace = last + "th";
            }
        }
        split[split.length - 1] = replace;
        return String.join(" ", split);
    }

    private static String helper(final int num) {
        if(num <= 0) {
            return "";
        } else if(num < VAL_TWENTY) {
            return BELOW_TWENTY[num] + ' ';
        } else if(num < VAL_HUNDRED) {
            String result = TENS[num / VAL_TEN];
            int remainder = num % VAL_TEN;
            if(remainder > 0) {
                result +=  '-' + helper(remainder);
            }
            return result;
        } else if(num < VAL_THOUSAND) {
            return helper(num / VAL_HUNDRED) + HUNDRED + helper(num % VAL_HUNDRED);
        } else if(num < VAL_MILLION) {
            return helper(num / VAL_THOUSAND) + THOUSAND + helper(num % VAL_THOUSAND);
        } else if(num < VAL_BILLION) {
            return helper(num / VAL_MILLION) + MILLION + helper(num % VAL_MILLION);
        } else {
            return helper(num / VAL_BILLION) + BILLION + helper(num % VAL_BILLION);
        }
    }
}

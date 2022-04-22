package io.github.drakonkinst.contextualdialogue.util;

import io.github.drakonkinst.contextualdialogue.exception.SpeechException;

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

    private NumericalSpeech() {}

    // Wait, was an interview question actually helpful for once?
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

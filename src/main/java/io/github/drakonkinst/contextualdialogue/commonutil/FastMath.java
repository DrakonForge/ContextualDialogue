package io.github.drakonkinst.contextualdialogue.commonutil;

import java.util.Random;

/**
 * Utility class for more helpful math functions focused on single-precision numbers,
 * uses lookup tables and sacrifices small amounts of accuracy for speed.
 * - Basically copypasted from LibGDX's MathUtils class
 *      (https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/MathUtils.java)
 * - Sin/Cos implementation based on Riven's implementation with augments from LibGDX
 *      (http://riven8192.blogspot.com/2009/08/fastmath-sincos-lookup-tables.html)
 */
public final class FastMath {
    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static final float PI = 3.1415927f;
    public static final float DOUBLE_PI = PI * 2.0f;
    public static final float HALF_PI = PI / 2.0f;
    public static final float RADIANS_FULL = DOUBLE_PI;
    public static final float DEGREES_FULL = 360.0f;
    public static final float DEGREES_HALF_PI = 90.0f;
    public static final float DEGREES_PI = 180.0f;
    public static final float ZERO = 0.0f;
    public static final float RADIANS_TO_DEGREES = DEGREES_PI / PI;
    public static final float DEGREES_TO_RADIANS = PI / DEGREES_PI;
    public static final float RAD_2 = FastMath.sqrt(2.0f);

    private static final int SIN_BITS = 14; // 16KB, adjust for accuracy
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;
    private static final float RADIANS_TO_INDEX = SIN_COUNT / RADIANS_FULL;
    private static final float DEGREES_TO_INDEX = SIN_COUNT / DEGREES_FULL;

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    public static Random random = new RandomXS128();

    private static class Sin {
        public static final float[] table = new float[SIN_COUNT];

        static {
            for(int i = 0; i < SIN_COUNT; i++) {
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * RADIANS_FULL);
            }

            // set exact cardinal directions
            for(int i = 0; i < DEGREES_FULL; i += DEGREES_HALF_PI) {
                table[(int) (i * DEGREES_TO_INDEX) & SIN_MASK] = (float) Math.sin(i * DEGREES_TO_RADIANS);
            }
        }
    }

    /** Wraps an angle between -PI2 and PI2 */
    public static float normalizeAngle(float angle) {
        while(angle > DOUBLE_PI) {
            angle -= DOUBLE_PI;
        }
        while(angle < -DOUBLE_PI) {
            angle += DOUBLE_PI;
        }
        return angle;
    }

    /** Wraps an angle in degrees between -360 and 360 degrees */
    public static float normalizeAngleDeg(float angle) {
        while(angle > DEGREES_FULL) {
            angle -= DEGREES_FULL;
        }
        while(angle < -DEGREES_FULL) {
            angle += DEGREES_FULL;
        }
        return angle;
    }

    /** Returns the sine in radians from a lookup table. For optimal precision, use radians between -PI2 and PI2 (both
     * inclusive). */
    public static float sin(float radians) {
        return Sin.table[(int) (radians * RADIANS_TO_INDEX) & SIN_MASK];
    }

    /** Returns the sine in degrees from a lookup table. For optimal precision, use radians between -360 and 360 (both
     * inclusive). */
    public static float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * DEGREES_TO_INDEX) & SIN_MASK];
    }

    /** Returns the cosine in radians from a lookup table. For optimal precision, use radians between -PI2 and PI2 (both
     * inclusive). */
    public static float cos(float radians) {
        return Sin.table[(int) ((radians + HALF_PI) * RADIANS_TO_INDEX) & SIN_MASK];
    }

    /** Returns the cosine in degrees from a lookup table. For optimal precision, use radians between -360 and 360 (both
     * inclusive). */
    public static float cosDeg(float degrees) {
        return Sin.table[(int) ((degrees + DEGREES_HALF_PI) * DEGREES_TO_INDEX) & SIN_MASK];
    }

    /** Returns atan2 in radians, less accurate than Math.atan2 but may be faster. Average error of 0.00231 radians (0.1323
     * degrees), largest error of 0.00488 radians (0.2796 degrees). */
    public static float atan2(float y, float x) {
        if(x == ZERO) {
            if(y > ZERO) {
                return HALF_PI;
            }
            if(y == ZERO) {
                return ZERO;
            }
            return -HALF_PI;
        }
        final float atan;
        final float z = y / x;
        if(Math.abs(z) < 1.0f) {
            atan = z / (1.0f + 0.28f * z * z);
            if(x < ZERO) {
                return atan + (y < ZERO ? -PI : PI);
            }
            return atan;
        }
        atan = HALF_PI - z / (z * z * 0.28f);
        return y < ZERO ? atan - PI : atan;
    }

    /** Returns acos in radians, less accurate than Math.acos but may be faster. */
    public static float acos(float a) {
        return 1.5707963267948966f - (a * (1.0f + (a *= a) * (-0.141514171442891431f + a * -0.719110791477959357f)))
                / (1.0f + a * (-0.439110389941411144f + a * -0.471306172023844527f));
    }

    /** Returns asin in radians, less accurate than Math.asin but may be faster. */
    public static float asin(float a) {
        return (a * (1.0f + (a *= a) * (-0.141514171442891431f + a * -0.719110791477959357f)))
                / (1.0f + a * (-0.439110389941411144f + a * -0.471306172023844527f));
    }

    public static float sqrt(float n) {
        return (float) Math.sqrt(n);
    }

    /** Returns a random integer between 0 (inclusive) and the specified value (exclusive). */
    public static int randInt(int max) {
        return random.nextInt(max);
    }

    /** Returns a random integer between 0 (inclusive) and the specified value (inclusive). */
    public static int randIntInclusive(int max) {
        return random.nextInt(max + 1);
    }

    /** Returns a random integer between min (inclusive) and max (exclusive). */
    public static int randInt(int min, int max) {
        return min + random.nextInt(max - min);
    }

    /** Returns a random integer between min (inclusive) and max (inclusive). */
    public static int randIntInclusive(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /** Returns a random long between 0 (inclusive) and the specified value (inclusive). */
    public static long randInt(long max) {
        return (long) (random.nextDouble() * max);
    }

    /** Returns a random long between min (inclusive) and max (inclusive). */
    public static long randInt(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min));
    }

    /** Returns a random boolean value. */
    public static boolean randBoolean() {
        return random.nextBoolean();
    }

    /** Returns true if a random value between 0 and 1 is less than the specified value. */
    public static boolean randBoolean(float chance) {
        return random() < chance;
    }

    /** Returns random number between 0.0 (inclusive) and 1.0 (exclusive). */
    public static float random() {
        return random.nextFloat();
    }

    /** Returns a random number between 0 (inclusive) and the specified value (exclusive). */
    public static float random(float max) {
        return random.nextFloat() * max;
    }

    /** Returns a random number between start (inclusive) and end (exclusive). */
    public static float random(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /** Returns -1 or 1, randomly. */
    public static int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    /** Returns the largest integer less than or equal to the specified float. This method will only properly floor floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14). */
    public static int floor(float value) {
        return (int) (value + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /** Returns the largest integer less than or equal to the specified float. This method will only properly floor floats that are
     * positive. Note this method simply casts the float to int. */
    public static int floorPositive(float value) {
        return (int) value;
    }

    /** Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14). */
    public static int ceil (float value) {
        return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - value);
    }

    /** Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil floats that
     * are positive. */
    public static int ceilPositive(float value) {
        return (int) (value + CEIL);
    }

    /** Returns the closest integer to the specified float. This method will only properly round floats from -(2^14) to
     * (Float.MAX_VALUE - 2^14). */
    public static int round(float value) {
        return (int)(value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /** Returns the closest integer to the specified float. This method will only properly round floats that are positive. */
    public static int roundPositive(float value) {
        return (int) (value + 0.5f);
    }

    /** Returns true if the value is zero (using the default tolerance as upper bound) */
    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /** Returns true if the value is zero.
     * @param tolerance represent an upper bound below which the value is considered zero. */
    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /** Returns true if a is nearly equal to b. The function uses the default floating error tolerance.
     * @param a the first value.
     * @param b the second value. */
    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    /** Returns true if a is nearly equal to b.
     * @param a the first value.
     * @param b the second value.
     * @param tolerance represent an upper bound below which the two values are considered equal. */
    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static int clampInt(int value, int min, int max) {
        if(value < min) {
            return min;
        }
        if(value > max) {
            return max;
        }
        return value;
    }

    // Only works on positive integers and positive exponents
    //https://stackoverflow.com/questions/35666078/fast-integer-powers-in-java
    public static int powInt(int base, int exp) {
        if(base <= 0) {
            // Error
            return base;
        }

        int result = 1;
        while(exp > 0) {
            if((exp & 1) == 0) {
                base *= base;
                exp >>>= 1;
            } else {
                result *= base;
                --exp;
            }
        }
        return result;
    }

    private FastMath() {}
}
package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.commonutil.FastMath;
import io.github.drakonkinst.commonutil.MyLogger;

public class CriterionFail implements Criterion {
    private final float chanceToFail;

    public static CriterionFail withChance(final float chance) {
        return new CriterionFail(chance);
    }

    // Should only work between 0 and 1, but other values are technically supported so why not
    private CriterionFail(final float chanceToFail) {
        this.chanceToFail = chanceToFail;
    }

    public boolean evaluate() {
        boolean passed = FastMath.random() >= chanceToFail;
        if(passed) {
            MyLogger.finest("PASS: Fail chance succeeded");
        } else {
            MyLogger.finest("FAIL: Fail chance failed");
        }
        return passed;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String toString() {
        return chanceToFail + " fail chance";
    }
}

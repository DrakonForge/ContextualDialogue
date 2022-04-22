package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;

public class CriterionDummy implements Criterion {
    private final int value;

    public static CriterionDummy of(final int value) {
        return new CriterionDummy(value);
    }

    private CriterionDummy(final int value) {
        this.value = value;
    }

    public boolean evaluate() {
        MyLogger.finest("PASS: Dummy criterion always succeeds");
        return true;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String toString() {
        return "dummy " + value;
    }
}

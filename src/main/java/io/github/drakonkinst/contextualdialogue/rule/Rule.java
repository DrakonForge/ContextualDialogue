package io.github.drakonkinst.contextualdialogue.rule;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

public class Rule implements Serializable {
    public static Rule EMPTY = new Rule(new CriterionTuple[0], 0);
    public static Builder builder(final int size) {
        return new Builder(size);
    }

    private final CriterionTuple[] criteria;
    private final int priority;

    private Rule(final CriterionTuple[] criteria, final int priority) {
        this.criteria = criteria;
        this.priority = priority;
    }

    public CriterionTuple getTupleAt(final int index) {
        return criteria[index];
    }

    public int getPriority() {
        return priority;
    }

    public int getSize() {
        return criteria.length;
    }

    public CriterionTuple[] getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "criteria=" + Arrays.toString(criteria) +
                ", priority=" + priority +
                '}';
    }

    // Builder class to make construction of Rules easier.
    public static class Builder {
        private final CriterionTuple[] criteria;
        private int currentIndex = -1;

        private Builder(final int size) {
            this.criteria = new CriterionTuple[size];
        }

        public Builder add(final CriterionTuple tuple) {
            ++currentIndex;
            if(currentIndex >= criteria.length) {
                throw new IllegalStateException("Error: Rule is limited to " + criteria.length + " items!");
            }

            criteria[currentIndex] = tuple;
            return this;
        }

        private void sortDictionary() {
            Arrays.sort(criteria, Collections.reverseOrder());
        }

        private int calculatePriority() {
            int priority = criteria.length;

            for(final CriterionTuple criterionTuple : criteria) {
                if (criterionTuple.getCriterion() instanceof CriterionDummy criterionDummy) {
                    // Minus one so we don't count dummy as its own criteria
                    priority += criterionDummy.getValue() - 1;
                }
            }

            return priority;
        }

        public Rule build() {
            if(currentIndex < criteria.length - 1) {
                throw new IllegalStateException("Error: Dictionary must have exactly " + criteria.length + " items!");
            }
            sortDictionary();

            return new Rule(criteria, calculatePriority());
        }
    }

}

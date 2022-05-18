package io.github.drakonkinst.contextualdialogue.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        private final List<CriterionTuple> criteria;
        private int priorityBonus = 0;

        private Builder(final int size) {
            this.criteria = new ArrayList<>(size);
        }

        public Builder add(final CriterionTuple tuple) {
            if(tuple.getCriterion() instanceof CriterionDummy criterionDummy) {
                priorityBonus += criterionDummy.getValue();
            } else {
                criteria.add(tuple);
            }
            return this;
        }

        public Rule build() {
            // Priority is number of criteria + dummy bonuses
            int priority = criteria.size() + priorityBonus;

            // Sort criteria and convert to array
            CriterionTuple[] criteriaArr = new CriterionTuple[criteria.size()];
            criteria.toArray(criteriaArr);
            Arrays.sort(criteriaArr, Collections.reverseOrder());

            return new Rule(criteriaArr, priority);
        }
    }

}

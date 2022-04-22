package io.github.drakonkinst.contextualdialogue.rule;

import io.github.drakonkinst.contextualdialogue.json.CriteriaParser;

import java.io.Serializable;

public class CriterionTuple implements Comparable<CriterionTuple>, Serializable {
    private final String key;
    private final String table;
    private final Criterion criterion;

    public CriterionTuple(final String key, final String table, final Criterion criterion) {
        this.key = key;
        this.table = table;
        this.criterion = criterion;
    }

    public String getKey() {
        return key;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public String getTable() {
        return table;
    }

    @Override
    public int compareTo(CriterionTuple o) {
        // Sort alphabetically
        //return key.compareTo(o.getKey());

        // Sort by criterion priority
        return criterion.getPriority() - o.getCriterion().getPriority();
    }

    @Override
    public String toString() {
        if(key.equals(CriteriaParser.KEY_DUMMY)
                || key.equals(CriteriaParser.KEY_FAIL)) {
            return criterion.toString();
        }
        if(table != null) {
            return table + "." + key + " " + criterion;
        }
        return key + " " + criterion;
    }
}
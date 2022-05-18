package io.github.drakonkinst.contextualdialogue.speech;

import io.github.drakonkinst.contextualdialogue.commonutil.FastMath;
import io.github.drakonkinst.contextualdialogue.commonutil.MyLogger;
import io.github.drakonkinst.contextualdialogue.context.ContextTable;
import io.github.drakonkinst.contextualdialogue.rule.Criterion;
import io.github.drakonkinst.contextualdialogue.rule.CriterionDynamic;
import io.github.drakonkinst.contextualdialogue.rule.CriterionExist;
import io.github.drakonkinst.contextualdialogue.rule.CriterionFail;
import io.github.drakonkinst.contextualdialogue.rule.CriterionTuple;
import io.github.drakonkinst.contextualdialogue.rule.FloatCriterion;
import io.github.drakonkinst.contextualdialogue.rule.ListCriterion;
import io.github.drakonkinst.contextualdialogue.rule.Rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Speechbank implements Serializable {

    private static boolean match(Rule rule, Map<String, ContextTable> contexts) {
        int numCriteria = rule.getSize();
        for(int i = 0; i < numCriteria; ++i) {
            if(!evaluateCriterion(rule.getTupleAt(i), contexts)) {
                return false;
            }
        }
        return true;
    }

    private static boolean evaluateCriterion(CriterionTuple info, Map<String, ContextTable> contexts) {
        String key = info.getKey();
        String table = info.getTable();
        Criterion criterion = info.getCriterion();

        if(criterion instanceof CriterionFail criterionFail) {
            return criterionFail.evaluate();
        } else if(criterion instanceof CriterionDynamic criterionDynamic) {
            return criterionDynamic.evaluate(key, table, contexts);
        } else if(criterion instanceof CriterionExist criterionExist) {
            return criterionExist.evaluate(key, table, contexts);
        } else if(criterion instanceof ListCriterion listCriterion) {
            return listCriterion.evaluate(key, table, contexts);
        } else if(criterion instanceof FloatCriterion floatCriterion) {
            return floatCriterion.evaluate(key, table, contexts);
        }
        throw new IllegalStateException("Unknown criterion type " + criterion.getClass().getName());
    }

    private static SpeechbankEntry pickRandomEntry(List<SpeechbankEntry> options) {
        if(options.size() == 0) {
            return null;
        } else if(options.size() == 1) {
            return options.get(0);
        } else {
            return options.get(FastMath.randInt(options.size()));
        }
    }

    private final String parent;
    private final Map<String, SpeechbankEntry[]> categoryToEntryMap;

    public Speechbank(String parent, Map<String, SpeechbankEntry[]> categoryToEntryMap) {
        this.parent = parent;
        this.categoryToEntryMap = categoryToEntryMap;
    }

    public SpeechbankEntry selectEntry(String category, Map<String, ContextTable> contexts) {
        SpeechbankEntry[] entries = categoryToEntryMap.get(category);
        if(entries == null) {
            MyLogger.severe("Unknown speech category \"" + category + "\"");
            return null;
        }

        List<SpeechbankEntry> candidates = new ArrayList<>();
        int highestMatchingPriority = -999;
        for(SpeechbankEntry entry : entries) {
            Rule rule = entry.getRule();
            int priority = rule.getPriority();
            if(priority < highestMatchingPriority) {
                break;
            }

            MyLogger.finest("Checking " + rule);
            if(match(rule, contexts)) {
                if(priority > highestMatchingPriority) {
                    highestMatchingPriority = priority;
                    candidates.clear();
                }

                candidates.add(entry);
            }
        }
        return pickRandomEntry(candidates);
    }

    public String getParent() {
        return parent;
    }
}

package it.kdde.sequentialpatterns.model;

import java.util.List;

/**
 * Created by fabiofumarola on 23/11/14.
 */
public class FrequentSequence extends Sequence {

    private int support;

    public FrequentSequence(int support, Itemset... itemsets){
        super(itemsets);
        this.support = support;
    }

    public FrequentSequence(int support, List<Itemset> elements){
        super();
        this.support = support;
        elements.forEach(e -> this.add(e));
    }

    public int getSupport() {
        return support;
    }

    @Override
    public String toString() {
        return super.toString() + " #SUP " + support;
    }
}

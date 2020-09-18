package it.kdde.sequentialpatterns.model;

import java.util.List;

/**
 * Created by fabiofumarola on 20/11/14.
 */
public class FrequentItemset extends Itemset {

    private int support = 0;

    public FrequentItemset(int support, String... items){
        super(items);
        this.support = support;
    }

    public FrequentItemset(int support, List<String> items){
        super(items);
        this.support = support;
    }

    public int getSupport() {
        return support;
    }

    @Override
    public String toString() {
        return super.toString() + " #SUP " + support;
    }
}

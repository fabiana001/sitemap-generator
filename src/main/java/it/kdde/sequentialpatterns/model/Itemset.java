/*
 * Copyright (c) 2005, University of Sydney
 * All rights reserved.
 * 
 * 
 */
package it.kdde.sequentialpatterns.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The Itemset class. A set of item.
 * <p/>
 * <pre>
 * eg. {a,b,c}
 * </pre>
 * <p/>
 * Each item is represented by a String
 *
 * @author Fabio Fumarola
 */
public class Itemset implements Iterable<String>, Cloneable, Comparable<Itemset>, Serializable {

    protected final List<String> elements = new ArrayList<>();

    public Itemset(String... items){
        for (String i : items){
            elements.add(i);
        }
    }

    public Itemset(Collection<String> items){
        elements.addAll(items);
    }

    /**
     * add one or more items to the itemset
     *
     * @param items
     */
    public void addItem(String... items) {
        for (String item : items) {
            elements.add(item);
        }
    }

    @Override
    public Itemset clone() {
        Itemset other = new Itemset();
        other.elements.addAll(this.elements);
        return other;
    }

    /**
     * @param item
     * @return true if containsItemset the item
     */
    public boolean contains(String item) {
        return elements.contains(item);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Itemset)) return false;

        Itemset other = (Itemset) o;

        if (elements != null ? !elements.equals(other.elements) : other.elements != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return elements != null ? elements.hashCode() : 0;
    }

    /**
     * Get the size of the itemset. Defined as number of items in the set
     *
     * @return size of itemset
     */
    public int size() {
        return elements.size();
    }

    /**
     * @return a string which represent the concatenation of the item in the
     * itemset
     */
    public String concatenate() {
        return elements.stream().reduce("", (s1,s2) -> s1 + " " + s2).trim();
    }

    /**
     * Get a text representation of the item set If the item set is empty,
     * return {-} otherwise, it should look something like: {a, b, c, d}
     *
     * @return a string representation of the item set
     */
    public String toString() {
        return concatenate();
    }

    @Override
    public Iterator<String> iterator() {
        return elements.iterator();
    }

    public String getLast() {
        return elements.get(elements.size() - 1);
    }

    /**
     *
     * @param other
     * @return true if this containsItemset the other itemset
     */
    public boolean contains(Itemset other) {

        for (String s : other) {
            if (!elements.contains(s))
                return false;
        }
        return true;
    }

    @Override
    public int compareTo(Itemset o) {
        return this.concatenate().compareTo(o.concatenate());
    }

    public List<String> getElements() {
        return elements;
    }


}

package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.sid.SparseIdList;

import java.util.ArrayList;
import java.util.List;

public class ItemsetNode {

    /**
     * represents the position of the treenode in the parent children list
     */
    private int position;
    private List<ItemsetNode> children = new ArrayList<>();
    private ItemsetNode parent;
    private Itemset itemset;
    private SparseIdList sil;

    /**
     * generates new itemesetNode root
     */
    ItemsetNode() {
        position = -1;
    }

    ItemsetNode(Itemset itemset, ItemsetNode parent, SparseIdList sil, int position) {
        this.parent = parent;
        this.sil = sil;
        this.position = position;
        this.itemset = itemset;
    }


    public List<ItemsetNode> getChildren() {
        return children;
    }


    public ItemsetNode getParent() {
        return parent;
    }

    public int getPosition() {
        return position;
    }

    public Itemset getItemset() {
        return itemset;
    }

    public SparseIdList getSil() {
        return sil;
    }

    @Override
    public String toString() {
        return itemset.toString();
    }


}

package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.sid.SparseIdList;

public class ItemsetTree {

    private ItemsetNode root = new ItemsetNode();

    /**
     * Adds new node into tree as child of parent
     *
     * @param parent   node parent of node to add
     * @param itemset
     * @param position position of new node into children list of parent node
     * @return
     */
    public ItemsetNode addChild(ItemsetNode parent, Itemset itemset, SparseIdList sil, int position) {
        ItemsetNode newNode = new ItemsetNode(itemset, parent, sil, position);
        parent.getChildren().add(newNode);
        return newNode;
    }

    public ItemsetNode getRoot() {
        return root;
    }

}

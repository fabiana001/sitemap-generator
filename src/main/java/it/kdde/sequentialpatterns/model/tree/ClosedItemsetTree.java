package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.sid.SparseIdList;

import java.util.HashMap;
import java.util.List;

public class ClosedItemsetTree {

	private ClosedItemsetNode root;

	private HashMap<Integer, List<Itemset>> closedTable;

	public ClosedItemsetTree() {
		root = new ClosedItemsetNode();
		closedTable = new HashMap<Integer, List<Itemset>>();
	}

	/**
	 *
	 * @param parent
	 * @param itemset
	 * @param position
	 * @param sil
	 * @return
	 */
	public ClosedItemsetNode addChild(ClosedItemsetNode parent,Itemset itemset, SparseIdList sil, int position) {
		ClosedItemsetNode newNode = new ClosedItemsetNode(parent,itemset, sil, position);
		parent.getChildren().add(newNode);
		return newNode;
	}


	public ClosedItemsetNode getRoot() {
		return root;
	}
}

package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SequenceNode implements Serializable{

	/**
	 * represents the position of the treenode in the parent children list
	 */
	private VerticalIdList verticalIdList;
	private List<SequenceNode> children = new LinkedList<>();
	private SequenceNode parent;
	private Sequence sequence;
	private int absSupport;

	/**
	 *
	 * @param verticalIdList
	 * @param sequence
	 * @param parent
	 * @param absSupport
	 */
	 SequenceNode(VerticalIdList verticalIdList, Sequence sequence, SequenceNode parent, int absSupport) {
		this.verticalIdList = verticalIdList;
		this.parent = parent;
		this.absSupport = absSupport;
		this.sequence = sequence;
	}


	public int getAbsSupport(){
		return absSupport;
	}

	public List<SequenceNode> getChildren() {
		return children;
	}

	public SequenceNode getParent() {
		return parent;
	}

	public VerticalIdList getVerticalIdList() {
		return verticalIdList;
	}

	public Sequence getSequence() {
		return sequence;
	}

	@Override
	public String toString() {
		return sequence.toString() + " #SUP " + this.getAbsSupport();
	}
}
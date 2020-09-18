package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ClosedSequenceNode implements Serializable{

	/**
	 * represents the position of the treenode in the parent children list
	 */
	private VerticalIdList vil;
	private List<ClosedSequenceNode> children = new LinkedList<>();
	private ClosedSequenceNode parent;
	private Sequence sequence;
	private NodeType type = NodeType.toCheck;
	private int absoluteSupport;

	/**
	 * For SequenceNode root
	 * 
	 * @param sizePositionList
	 */
	ClosedSequenceNode(int sizePositionList) {
		sequence = new Sequence();
		this.absoluteSupport = sizePositionList;
	}

	ClosedSequenceNode(ClosedSequenceNode parent, Sequence sequence, VerticalIdList vil, int absoluteSupport) {
		this.vil = vil;
		this.parent = parent;
		this.sequence = sequence;
		this.absoluteSupport = absoluteSupport;
	}

	public List<ClosedSequenceNode> getChildren() {
		return children;
	}

    public void setChildren(List<ClosedSequenceNode> children) {
        this.children = children;
    }

	public ClosedSequenceNode getParent() {
		return parent;
	}

	public VerticalIdList getVerticalIdList(){
		return vil;
	}

	public Sequence getSequence() {
		return sequence;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public int getAbsoluteSupport() {
		return absoluteSupport;
	}

	@Override
	public String toString() {
		return sequence.toString() + " #SUP " + this.absoluteSupport;
	}

    public ClosedSequenceNode clone(){
        return new ClosedSequenceNode(this.parent, this.sequence, this.vil, this.absoluteSupport);
    }

    //TODO verificare se è corretto usare equals anzichè contains
	public boolean containsLastItemset(ClosedSequenceNode n) {
		if (sequence.getLastItemset().equals(n.sequence.getLastItemset()))
			return false;
		
		return sequence.getLastItemset().contains(n.getSequence().getLastItemset());
	}
}
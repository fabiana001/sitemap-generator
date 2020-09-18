package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ClosedSequenceTree implements Serializable {

    private ClosedSequenceNode root;
    private int absSupport;

    public ClosedSequenceTree(int sizePosList) {
        root = new ClosedSequenceNode(sizePosList);
        absSupport = sizePosList;

    }

    public ClosedSequenceNode addChild(ClosedSequenceNode parent, Sequence sequence,
                                       VerticalIdList vil, int support) {
        ClosedSequenceNode newNode = new ClosedSequenceNode(parent, sequence, vil, support);
        parent.getChildren().add(newNode);
        return newNode;
    }

    public ClosedSequenceNode getRoot() {
        return root;
    }

    public void setRoot(ClosedSequenceNode root) {this.root = root;}

    public static List<ClosedSequenceNode> visit(ClosedSequenceTree closedTree) {
        Queue<ClosedSequenceNode> queue = new LinkedList<>();
        List<ClosedSequenceNode> res = new ArrayList<>();
        queue.addAll(closedTree.getRoot().getChildren());

        ClosedSequenceNode currentNode;
        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            res.add(currentNode);
            queue.addAll(currentNode.getChildren());
        }
        return res;
    }

    /**
     * minimum absolute support defined in input by the user
     * @return
     */
    public int getAbsSupport() {
        return absSupport;
    }
}
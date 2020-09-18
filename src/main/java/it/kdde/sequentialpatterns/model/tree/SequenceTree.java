package it.kdde.sequentialpatterns.model.tree;

import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SequenceTree implements Serializable{

    private SequenceNode root;
    private long absSupport;

    public SequenceTree(long numSequences) {

        root = new SequenceNode(null,new Sequence(),null,(int) numSequences);
        absSupport = numSequences;
    }

    public SequenceNode addChild(SequenceNode parent, Sequence sequence, VerticalIdList vil, int absoluteSupport) {
        SequenceNode newNode = new SequenceNode(vil, sequence, parent, absoluteSupport);
        parent.getChildren().add(newNode);
        return newNode;
    }

    public SequenceNode getRoot() {
        return root;
    }

    /**
     *
     * @param tree
     * @return
     */
    public static List<SequenceNode> visit(SequenceTree tree) {

        Queue<SequenceNode> queue = new LinkedList<>();
        List<SequenceNode> result = new ArrayList<>();
        queue.addAll(tree.getRoot().getChildren());

        while (!queue.isEmpty()) {
            SequenceNode currentNode = queue.remove();
            result.add(currentNode);
            queue.addAll(currentNode.getChildren());
        }
        return result;
    }

    public long getAbsSupport() {
        return absSupport;
    }

}
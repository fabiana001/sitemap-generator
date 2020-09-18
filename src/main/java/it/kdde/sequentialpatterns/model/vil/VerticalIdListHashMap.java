package it.kdde.sequentialpatterns.model.vil;

import it.kdde.sequentialpatterns.model.ListNode;

import java.io.Serializable;
import java.util.*;

/**
 * Created by fabiana 6/07/2015
 */
public class VerticalIdListHashMap implements Serializable, VerticalIdList {

    private HashMap<Integer,ListNode> elements;
    private long absoluteSupport;

    public VerticalIdListHashMap(HashMap<Integer,ListNode> elements, long absoluteSupport){
        this.elements = elements;
        this.absoluteSupport = absoluteSupport;

    }
    @Deprecated
    private HashMap<Integer,ListNode> getElements() {
        return elements;
    }

    public ArrayList<Integer> getValidRows(){
        return new ArrayList<>(elements.keySet());
    }

    public ListNode getElement(int row){
        return elements.get(row);
    }


    public long getAbsoluteSupport(){return absoluteSupport;}

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        List<Integer> list = new ArrayList<Integer>(elements.keySet());
        Collections.sort(list);
        for(Integer key: list) {
            buf.append(elements.get(key).toString());

        }

        return buf.toString();
    }

    /**
     * realize SStep between the current vertical id list and the vertical id list "bVil"
     * @param bVil
     * @return
     */
    public VerticalIdList SStep(VerticalIdList bVil) {

        long support = 0;
        VerticalIdListHashMap b = (VerticalIdListHashMap) bVil;
        Set<Integer> rows = null;
        HashMap<Integer, ListNode> newVilElements = new HashMap<>();

        if(this.elements.keySet().size() <= b.elements.keySet().size()){
            rows = this.elements.keySet();
        }else {
            rows = b.elements.keySet();
        }

        for (int row: rows) {
            ListNode aNode = this.elements.get(row);
            ListNode bNode = b.elements.get(row);

            // when i found a null element I exit the for
            if ((aNode == null) || (bNode == null)) {
                continue;
            }

            // case 1:
            if ((aNode.getColumn() < bNode.getColumn())) {
                newVilElements.put(row, bNode);
                support ++;
                // case 2:
            } else if ((aNode.getColumn() >= bNode.getColumn())) {
                while ((bNode != null) && (aNode.getColumn() >= bNode.getColumn())) {
                    bNode = bNode.next();
                }
                if (bNode != null) {
                    newVilElements.put(row, bNode);
                    support ++;
                }
            }
        }
        //finally
        return new VerticalIdListHashMap(newVilElements, support);
    }



}

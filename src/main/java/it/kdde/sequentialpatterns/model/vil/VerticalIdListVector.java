package it.kdde.sequentialpatterns.model.vil;

import it.kdde.sequentialpatterns.model.ListNode;

import java.io.Serializable;
import java.util.ArrayList;

/**
* Created by fabiofumarola on 15/11/14.
*/
public class VerticalIdListVector implements Serializable, VerticalIdList{

    private ListNode[] elements;
    private long absoluteSupport;

    public VerticalIdListVector(ListNode[] elements, long absoluteSupport){
        this.elements = elements;
        this.absoluteSupport = absoluteSupport;
    }
@Deprecated
    private ListNode[] getElements() {
        return elements;
    }

    public long getAbsoluteSupport(){return absoluteSupport;}

    public ListNode getElement(int row){
        return elements[row];
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        for(ListNode el:elements){
            if(el!=null)
                buf.append(el.toString());

        }

        return buf.toString();
    }

    public ArrayList<Integer> getValidRows(){
        ArrayList<Integer> validRows = new ArrayList<>();
        for (int i = 0; i < this.getElements().length; i++) {
            if (this.getElements()[i] != null)
                validRows.add(i);
        }
        return validRows;
    }

    public VerticalIdList SStep(VerticalIdList b) {

        long support = 0;
        ListNode[] newPosList = new ListNode[this.elements.length];
        ListNode listNode, listNodeBrother;

        for (int i = 0; i < this.elements.length; i++) {

            listNode = this.elements[i];
            listNodeBrother = b.getElement(i);
            // when i found a null element I exit the for
            if ((listNode == null) || (listNodeBrother == null)) {
                continue;
            }

            // case 1:
            if ((listNode.getColumn() < listNodeBrother.getColumn())) {
                newPosList[i] = listNodeBrother;
                support ++;
                // case 2:
            } else if ((listNode.getColumn() >= listNodeBrother.getColumn())) {
                while ((listNodeBrother != null) && (listNode.getColumn() >= listNodeBrother.getColumn())) {
                    listNodeBrother = listNodeBrother.next();
                }
                if (listNodeBrother != null) {
                    newPosList[i] = listNodeBrother;
                    support ++;
                }
            }
        }
        //finally
        return new VerticalIdListVector(newPosList, support);
    }
}

package it.kdde.sequentialpatterns.model.sid;

import it.kdde.sequentialpatterns.model.ListNode;
import it.kdde.sequentialpatterns.model.vil.VerticalIdListHashMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;


/**
 * This class implement the list of list of all the positions in which the
 * element occurs in the database
 *
 * @author Eliana
 */
public class SparseIdListHashMap implements  SparseIdList{

    private HashMap<Integer,TransactionIds> vector;

    private int length;
    /**
     * the count of the non null transactionsIds list
     */
    private int absoluteSupport;

    public SparseIdListHashMap(int length) {
        vector = new HashMap<>();
        this.length = length;
    }


    /**
     * add an element in the specific cell of the sparseIdList
     *
     * @param row
     * @param value
     */
    public void addElement(int row, int value, String data) {
        if (!vector.containsKey(row)) {
            TransactionIds tid = new TransactionIds();
            tid.add(new ListNode(value, data));
            vector.put(row, tid);
            //when vector of row is null then we should increment the absolute support
            absoluteSupport++;
        }else
            vector.get(row).add(new ListNode(value, data));
    }

    /**
     *
     * @param row
     * @param col
     * @return the listNode in the position [row, col] of the SparseIdList
     */
    public ListNode getElement(int row, int col) {
        if (vector.containsKey(row)) {
            if (col < vector.get(row).size()) {
                return vector.get(row).get(col);
            }
        }
        return null;
    }

//    /**
//     * compute an IStep on 2 sparseIdList a and b
//     *  @param a
//     * @param b
//     */
//    public static SparseIdListHashMap IStep(SparseIdListHashMap a, SparseIdListHashMap b) {
//
//        SparseIdListHashMap sparseIdList = new SparseIdListHashMap(a.length);
//        ListNode aNode, bNode;
//        Set<Integer> rows = null;
//
//        if(a.vector.keySet().size() <= b.vector.keySet().size()){
//            rows = a.vector.keySet();
//        }else {
//            rows = b.vector.keySet();
//        }
//        for(Integer row: rows){
//            aNode = a.getElement(row, 0);
//            bNode = b.getElement(row, 0);
//            while ((aNode != null) && (bNode != null)) {
//                if (aNode.getColumn() == bNode.getColumn()) {
//                    sparseIdList.addElement(row, bNode.getColumn(),bNode.getData());
//                    aNode = aNode.next();
//                    bNode = bNode.next();
//                } else if (aNode.getColumn() > bNode.getColumn()) {
//                    bNode = bNode.next();
//                } else {
//                    aNode = aNode.next();
//                }
//            }
//        }
//
//
//
//        return sparseIdList;
//    }

    /**
     * compute an IStep between this and a SparseIdList "sidB"
     *  @param sidB
     */
    public SparseIdList IStep(SparseIdList sidB) {

        SparseIdListHashMap sparseIdList = new SparseIdListHashMap(this.length);
        SparseIdListHashMap b = (SparseIdListHashMap) sidB;
        ListNode aNode, bNode;
        Set<Integer> rows = null;

        if(this.vector.keySet().size() <= b.vector.keySet().size()){
            rows = this.vector.keySet();
        }else {
            rows = b.vector.keySet();
        }
        for(Integer row: rows){
            aNode = this.getElement(row, 0);
            bNode = b.getElement(row, 0);
            while ((aNode != null) && (bNode != null)) {
                if (aNode.getColumn() == bNode.getColumn()) {
                    sparseIdList.addElement(row, bNode.getColumn(),bNode.getData());
                    aNode = aNode.next();
                    bNode = bNode.next();
                } else if (aNode.getColumn() > bNode.getColumn()) {
                    bNode = bNode.next();
                } else {
                    aNode = aNode.next();
                }
            }
        }



        return sparseIdList;
    }



    public int length() {
        return length;
    }
    /**
     *
     * @return return the first VIL from a given SparseIdList
     */
    public VerticalIdListHashMap getStartingVIL(){

//        ListNode[] vilElements = new ListNode[this.length];
//
//        for (int i = 0; i < vilElements.length; i++) {
//            vilElements[i] = this.getElement(i, 0);
//        }
        HashMap<Integer, ListNode> vilElements = new HashMap<>();
        for(Integer row: vector.keySet()){
            vilElements.put(row, this.getElement(row, 0));
        }

        return new VerticalIdListHashMap (vilElements,this.absoluteSupport);
    }


    /**
     *
     * @return the absolute support of the sparseIdList
     */
    public int getAbsoluteSupport() {
        return absoluteSupport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SparseIdListHashMap)) return false;

        SparseIdListHashMap that = (SparseIdListHashMap) o;
        TransactionIds those, these;

        if(this.vector.keySet().size()!= that.vector.keySet().size())
            return false;

        for(Integer row: this.vector.keySet()){
            these = vector.get(row);
            those = that.vector.get(row);
            if(these == null)
                return false;
            if(these.size() != those.size())
                return false;
            for (int j = 0; j < these.size(); j++){
                //if (!these.get(j).equals(those.get(j)))
                if (these.get(j).getColumn() != those.get(j).getColumn()) {
                    return false;
                }
            }
        }

        return true;
    }

//    @Override
//    public int hashCode() {
//        return vector != null ? HashMap.hashCode(vector) : 0;
//    }

    /**
     * return a string representation of the sparse matrix of position
     *
     * <pre>
     * eg. 	[1:2][1:3][1:4][1:5]
     * 		[2:1][2:4][2:6][2:7]
     *      [3:2][3:4][3:6][3:8]
     * </pre>
     *
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < this.length; i++) {
            TransactionIds currList = vector.get(i);
            if (currList != null) {
                // iterate on the MyLinkedList
                for (int j = 0; j < currList.size(); j++) {
                    buf.append(currList.get(j).toString() + " ");
                }
                buf.append("\n");
            } else {
                buf.append("null \n");
            }
        }
        return buf.toString();
    }

    class TransactionIds extends LinkedList<ListNode> {

        private static final long serialVersionUID = 1L;

        /**
         * decorated with a link to the nextElement
         * @param e
         * @return
         */
        @Override
        public boolean add(ListNode e) {
            if (this.size() != 0){
                ListNode last = this.getLast();
                last.setNext(e);
            }
            return super.add(e);
        }


        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < this.size(); i++) {
                buf.append(this.get(i).toString());
            }
            return buf.toString();
        }

    }
}

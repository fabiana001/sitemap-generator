package it.kdde.sequentialpatterns.model.sid;

import it.kdde.sequentialpatterns.model.ListNode;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;
import it.kdde.sequentialpatterns.model.vil.VerticalIdListVector;

import java.util.Arrays;
import java.util.LinkedList;

/**
* This class implement the list of list of all the positions in which the
* element occurs in the database
*
* @author Eliana
*/
public class SparseIdListVector implements SparseIdList{

    private TransactionIds[] vector;
    /**
     * the count of the non null transactionsIds list
     */
    private int absoluteSupport;

    public SparseIdListVector(int rows) {
        vector = new TransactionIds[rows];
    }

    /**
     *
     * @return the size of sparse id list
     */
    public int length() {
        return vector.length;
    }


    /**
     * add an element in the specific cell of the sparseIdList
     *
     * @param row
     * @param value
     */
    public void addElement(int row, int value, String data) {
        if (vector[row] == null) {
            vector[row] = new TransactionIds();
            //when vector of row is null then we should increment the absolute support
            absoluteSupport++;
        }
        vector[row].add(new ListNode(value, data));
    }

    /**
     *
     * @param row
     * @param col
     * @return the listNode in the position [row, col] of the SparseIdList
     */
    public ListNode getElement(int row, int col) {
        if (vector[row] != null) {
            if (col < vector[row].size()) {
                return vector[row].get(col);
            }
        }
        return null;
    }


    /**
     * compute an IStep on 2 sparseIdList a and b
     *
     * @param b
     */
    public SparseIdList IStep( SparseIdList b) {

        SparseIdListVector sparseIdList = new SparseIdListVector(this.length());
        ListNode aNode, bNode;
        for (int i = 0; i < this.length(); i++) {
            aNode = this.getElement(i, 0);
            bNode = b.getElement(i, 0);

            while ((aNode != null) && (bNode != null)) {
                if (aNode.getColumn() == bNode.getColumn()) {
                    sparseIdList.addElement(i, bNode.getColumn(),bNode.getData());
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

    /**
     *
     * @return return the first VIL from a given SparseIdList
     */
    public VerticalIdList getStartingVIL(){

        ListNode[] vilElements = new ListNode[this.length()];

        for (int i = 0; i < vilElements.length; i++) {
            vilElements[i] = this.getElement(i, 0);
        }
        return new VerticalIdListVector(vilElements,this.absoluteSupport);
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
        if (!(o instanceof SparseIdListVector)) return false;

        SparseIdListVector that = (SparseIdListVector) o;

        TransactionIds those, these;

        for (int i =0; i < vector.length; i++){
            these = vector[i];
            those = that.vector[i];

            if (these == null && those == null)
                continue;

            if (these == null || those == null)
                return false;

            if (these.size() != those.size())
                return false;

            for (int j = 0; j < these.size(); j++){
                //if (!these.get(j).equals(those.get(j)))
                if (these.get(j).getColumn()!=those.get(j).getColumn()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return vector != null ? Arrays.hashCode(vector) : 0;
    }

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

        for (int i = 0; i < vector.length; i++) {
            TransactionIds currList = vector[i];
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
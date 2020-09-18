package it.kdde.sequentialpatterns.model.sid;

import it.kdde.sequentialpatterns.model.ListNode;
import it.kdde.sequentialpatterns.model.vil.VerticalIdList;

/**
 * Created by fabiana on 7/6/15.
 */
public interface SparseIdList {
    public void addElement(int row, int value, String data);

    public ListNode getElement(int row, int col);


    public SparseIdList IStep(SparseIdList a);

    public int length();

    public VerticalIdList getStartingVIL();

    public int getAbsoluteSupport();





}

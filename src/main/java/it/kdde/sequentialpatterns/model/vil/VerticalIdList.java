package it.kdde.sequentialpatterns.model.vil;

import it.kdde.sequentialpatterns.model.ListNode;

import java.util.ArrayList;

/**
 * Created by fabiana on 7/6/15.
 */
public interface VerticalIdList {

    public long getAbsoluteSupport();

    public ListNode getElement(int row);

    public ArrayList<Integer> getValidRows();

    public VerticalIdList SStep(VerticalIdList b);

}

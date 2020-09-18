package it.kdde.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiofumarola on 15/11/14.
 */
public class Utils {

    /**
     *
     * @param relativeSupport
     * @param totalCount
     * @return the absolute support for the given relative support
     */
    public static int absoluteSupport(float relativeSupport, long totalCount){
        return (int) Math.ceil((relativeSupport * totalCount));
    }

    /**
     *
     * @param absoluteSupport
     * @param totalCount
     * @return return the relative support for a given absoluteSupport
     */
    public static float relativeSupport(int absoluteSupport, long totalCount){
        return (float) Math.ceil((((double) absoluteSupport) / totalCount));
    }

    public static double toSeconds(long milliseconds){
        return ((double) milliseconds) / 1000;
    }

    /**
     * this method generates all the possible ordered sub-sequence of length k from the given list of strings
     * @param input
     * @param k
     * @return
     */
    //TODO implement this method to optimize aprioriAll
    public static List<List<String>> orderedSubStringGenerator(List<String> input, int k){

        if (input.size() < k)
            throw new RuntimeException(input.toString() + " cannot have size lower than " + k);

        List<List<String>> result = new ArrayList<>();

        return result;
    }
}

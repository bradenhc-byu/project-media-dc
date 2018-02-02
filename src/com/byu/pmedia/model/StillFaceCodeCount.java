/*
 * ---------------------------------------------------------------------------------------------------------------------
 *                            Brigham Young University - Project MEDIA StillFace DataCenter
 * ---------------------------------------------------------------------------------------------------------------------
 * The contents of this file contribute to the ProjectMEDIA DataCenter for managing and analyzing data obtained from the
 * results of StillFace observational experiments.
 *
 * This code is free, open-source software. You may distribute or modify the code, but Brigham Young University or any
 * parties involved in the development and production of this code as downloaded from the remote repository are not
 * responsible for any repercussions that come as a result of the modifications.
 */
package com.byu.pmedia.model;

/**
 * Inner-class that holds information about StillFaceCodes and their counts within a dataset. This is a
 * necessary wrapper for filling the three JavaFX TableViews that hold summative code information.
 * @author Braden Hitchcock
 */
public class StillFaceCodeCount implements Comparable<StillFaceCodeCount>{

    /* Holds the name of the Code */
    private String name;
    /* Holds the number of occurrences of the code in the dataset*/
    private int count;

    /**
     * Constructor for a StillFaceCodeCount object. Creates a new instance.
     *
     * @param name The name of the code
     * @param count The number of occurances of the code
     */
    public StillFaceCodeCount(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    /**
     * Allows a collection of StillFaceCodeCount objects to be sorted in descending order
     *
     * @param stillFaceCodeCounts The object to compare this object to
     * @return 0 if the two are equal, greater than one if this object's counts are less than the
     *         comparison object, and less than one if this object's counts are greater than the
     *         comparison object
     */
    @Override
    public int compareTo(StillFaceCodeCount stillFaceCodeCounts) {
        int compareCount = stillFaceCodeCounts.getCount();
        return compareCount - this.count;
    }
}
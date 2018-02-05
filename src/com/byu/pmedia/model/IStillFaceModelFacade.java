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

import java.util.List;

/**
 * IStillFaceModelFacade
 * This provides a facade wrapper to the data model used in the DataCenter. However, at this time, it is not currently
 * in use in the program. Should the program grow more complex, it could be incorporated for simplicity
 */
public interface IStillFaceModelFacade {

    /**
     * Method to enforce the singleton pattern in accessing in-memory data
     *
     * @return An instance of an object that fully implements this interface
     */
    IStillFaceModelFacade getInstance();

    /**
     * Provides access to read data from the in-memory data structure holding information from the
     * StillFace database
     *
     * @return A list of StillFaceImport objects
     */
    List<StillFaceImport> getImports();

    /**
     * Provides access to data that conforms to the constraints placed on the data request by the provided
     * StillFaceImport data object
     *
     * @param importData A StillFaceImport object populated with data that all elements of the returned list will
     *                   conform to
     * @return A list of StillFaceImport objects
     */
    List<StillFaceImport> getImports(StillFaceImport importData);

    /**
     * Provides access to coded video data from the database that has been read into memory.
     *
     * @return A list if StillFaceData objects
     */
    List<StillFaceData> getData();

    /**
     * Provides access to coded video data from the database that conforms to constraints placed on it by the values
     * of the StillFaceData object passed as a parameter.
     *
     * @param data A StillFaceData object populated with data that all elements of the returned list will conform to
     * @return A list of StillFaceData objects
     */
    List<StillFaceData> getData(StillFaceData data);

    /**
     * Provides access to coded video data from the database that conforms to constraints placed on it by the values of
     * both the StillFaceData and StillFaceImport objects. Provides more fine-tuned querying of data from the in memory
     * structure.
     *
     * @param importData A StillFaceImport object that has data that all elements of the returned list will conform to
     * @param data A StillFaceData object that has data that all elements of the returned list will conform to
     * @return A list of StillFaceData objects
     */
    List<StillFaceData> getData(StillFaceImport importData, StillFaceData data);

    /**
     * Provides access to all codes associated with data in the StillFace database
     *
     * @return A list of StillFaceCode objects
     */
    List<StillFaceCode> getCodes();

    /**
     * Provides access to codes that conform to constraints placed on them by the provided StillFaceCode object. The
     * values of this object are used to query the internal data structure for matching entries.
     *
     * @param code The StillFaceCode object that acts as a constrainer on the returned data
     * @return A list of StillFaceCode objects
     */
    List<StillFaceCode> getCodes(StillFaceCode code);

    /**
     * Provides access to all tags associated with data in the StillFace database
     *
     * @return A list of StillFaceTag objects
     */
    List<StillFaceTag> getTags();

    /**
     * Provides access to tags that conform to constraints placed on them by the provided StillFaceTag object. The
     * values of this object are used to query the internal data structure for matching entries.
     *
     * @param tag The StillFaceTag object that acts as a constrainer on the returned data
     * @return A list of StillFaceTag objects
     */
    List<StillFaceTag> getTags(StillFaceTag tag);

    /**
     * Updates the import data that has the same ID as the provided object
     *
     * @param importData the import data entry to update
     * @return True if the update was successful, false otherwise
     */
    boolean updateImport(StillFaceImport importData);

    /**
     * Updates the video data that has the same ID as the provided object
     *
     * @param data the video data entry to update
     * @return True if the update was successful, false otherwise
     */
    boolean updateData(StillFaceData data);

    /**
     * Updates the code data that has the same ID as the provided object
     *
     * @param code the code data entry to update
     * @return True if the update was successful, false otherwise
     */
    boolean updateCode(StillFaceCode code);

    /**
     * Updates the tag data that has the same ID as the provided object
     *
     * @param tag the import data entry to update
     * @return True if the update was successful, false otherwise
     */
    boolean updateTag(StillFaceTag tag);

}

package com.byu.pmedia.model;

import java.util.List;

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

    List<StillFaceTag> getTags();

    List<StillFaceTag> getTags(StillFaceTag tag);

    boolean updateImport(StillFaceImport importData);

    boolean updateData(StillFaceData data);

    boolean updateCode(StillFaceCode code);

    boolean updateTag(StillFaceTag tag);

}

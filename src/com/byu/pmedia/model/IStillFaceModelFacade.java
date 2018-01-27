package com.byu.pmedia.model;

import java.util.ArrayList;

public interface IStillFaceModelFacade {

    ArrayList<StillFaceImport> getImportDataAsArrayList();

    ArrayList<StillFaceData> getCodeDataAsArrayList();

    ArrayList<StillFaceCode> getCodeAsArrayList();

    ArrayList<StillFaceTag> getTagAsArrayList();

    StillFaceImport getImportData(int importID);

    StillFaceVideoData getVideoData(int importID);

    StillFaceData getCodeData(int dataID);

    StillFaceCode getCode(int codeID);

    StillFaceTag getTag(int tagID);

}

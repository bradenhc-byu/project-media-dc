package com.byu.pmedia.model;

import java.util.ArrayList;

public interface IStillFaceModelFacade {

    ArrayList<StillFaceImportData> getImportDataAsArrayList();

    ArrayList<StillFaceCodeData> getCodeDataAsArrayList();

    ArrayList<StillFaceCode> getCodeAsArrayList();

    ArrayList<StillFaceTag> getTagAsArrayList();

    StillFaceImportData getImportData(int importID);

    StillFaceVideoData getVideoData(int importID);

    StillFaceCodeData getCodeData(int dataID);

    StillFaceCode getCode(int codeID);

    StillFaceTag getTag(int tagID);

}

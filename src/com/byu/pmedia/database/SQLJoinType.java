package com.byu.pmedia.database;

public enum SQLJoinType {

    // Enum type names
    INNER,
    LEFT_OUTER,
    RIGHT_OUTER,
    FULL_OUTER;

    // String conversion function
    String getName(){
        switch(this){
            case INNER:
                return "INNER JOIN";
            case LEFT_OUTER:
                return "LEFT OUTER JOIN";
            case RIGHT_OUTER:
                return "RIGHT OUTER JOIN";
            case FULL_OUTER:
                return "FULL OUTER JOIN";
            default:
                return "UNKNOWN JOIN";
        }
    }
}

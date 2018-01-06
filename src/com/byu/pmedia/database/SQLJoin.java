package com.byu.pmedia.database;

public class SQLJoin {

    private SQLJoinType type;
    private String table;
    private String onLeft;
    private String onRight;

    public SQLJoin(SQLJoinType type){
        this.type = type;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setLeft(String left){
        this.onLeft = left;
    }

    public void setRight(String right){
        this.onRight = right;
    }

    @Override
    public String toString() {
        return this.type.getName() + " " + this.table + " ON " + this.onLeft + " = " + this.onRight + " ";
    }
}

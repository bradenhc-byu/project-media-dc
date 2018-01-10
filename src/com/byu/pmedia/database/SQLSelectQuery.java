package com.byu.pmedia.database;

import com.byu.pmedia.log.PMLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLSelectQuery implements ISQLDatabaseQuery {

    private String table;
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<SQLJoin> joins = new ArrayList<>();
    private ArrayList<String> conditions = new ArrayList<>();
    private ArrayList<String> order = new ArrayList<>();

    public SQLSelectQuery(){

    }

    public void setTable(String name){
        this.table = name;
    }

    public void addField(String field){
        this.fields.add(field);
    }

    public void addJoin(SQLJoin join){
        this.joins.add(join);
    }

    public void addCondition(String condition){
        this.conditions.add(condition);
    }

    public void addOrder(String order){
        this.order.add(order);
    }

    @Override
    public ResultSet execute(DatabaseConnection connection) {

        if(!connection.isConnectionEstablished()){
            PMLogger.getInstance().warn("Cannot execute query. Database Connection has not been established");
            return null;
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        for(String f : this.fields){
            queryBuilder.append(f).append(" ");
        }
        queryBuilder.append("FROM ").append(this.table).append(" ");
        for(SQLJoin join : this.joins){
            queryBuilder.append(join.toString());
        }
        if(this.conditions.size() > 0){
            queryBuilder.append("WHERE ").append(this.conditions.get(0));
            int i;
            for(i = 1; i < this.conditions.size(); i++){
                queryBuilder.append(" AND ").append(this.conditions.get(i));
            }
        }
        if(this.order.size() > 0){
            queryBuilder.append("ORDER BY ").append(String.join(",", this.order));
        }

        try{
            Statement statement = connection.getConnection().createStatement();
            return statement.executeQuery(queryBuilder.toString());
        }
        catch(SQLException e){
            PMLogger.getInstance().error("Failed to execute select query");
            return null;
        }

    }
}

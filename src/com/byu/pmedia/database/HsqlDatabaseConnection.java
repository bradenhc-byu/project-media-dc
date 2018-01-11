package com.byu.pmedia.database;

import com.byu.pmedia.log.PMLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HsqlDatabaseConnection implements IDatabaseConnection {

    private String filepath;
    private String dbname;
    private String user;
    private String password;
    private String url;
    private Connection connection;
    private boolean connectionEstablished;

    public HsqlDatabaseConnection(String filepath, String dbname){
        this.filepath = filepath;
        this.dbname = dbname;
        this.url = String.format("jdbc:hsqldb:file:%s/%s", filepath, dbname);
    }

    public HsqlDatabaseConnection(String filepath, String dbname, String user, String password) {
        this.filepath = filepath;
        this.dbname = dbname;
        this.user = user;
        this.password = password;
        this.url = String.format("jdbc:hsqldb:file:%s/%s;user=%s;password=%s", filepath, dbname, user, password);
    }

    @Override
    public boolean establish() {
        this.connectionEstablished = false;
        try{
            this.connection = DriverManager.getConnection(this.url);
            String schema = this.connection.getSchema();

            PMLogger.getInstance().info("Database connection successful");
            PMLogger.getInstance().debug("Connection schema: " + schema);

        }
        catch(SQLException e){
            PMLogger.getInstance().error("Failed to establish database connection, SQL error code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
        this.connectionEstablished = true;

        return true;
    }

    @Override
    public boolean close() {
        PMLogger.getInstance().info("Closing database connection");
        try{
            this.connection.close();
        }
        catch(SQLException e){
            PMLogger.getInstance().error("Failed to close database connection, SQL error code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
        PMLogger.getInstance().info("Database connection closed successfully");
        this.connectionEstablished = false;
        return true;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getDbname() {
        return dbname;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public Connection getConnection() {
        return connection;
    }
}

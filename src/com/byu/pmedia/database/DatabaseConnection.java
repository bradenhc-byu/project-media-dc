package com.byu.pmedia.database;

import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.util.ErrorCode;

import java.sql.*;

public class DatabaseConnection {

    private String hostname;
    private String port;
    private String dbname;
    private String user;
    private String password;
    private String url;
    private Connection connection;
    private boolean connectionEstablished;


    public DatabaseConnection(String hostname, String port, String dbname, String user, String password){
        this.hostname = hostname;
        this.port = port; //1433 for Azure
        this.dbname = dbname;
        this.user = user;
        this.password = password;
        this.url = String.format("jdbc:sqlserver://%s:%s;database=%s;user=%s;password=%s;" +
                        "encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
                this.hostname, this.port, this.dbname, this.user, this.password);
        this.connectionEstablished = false;
    }

    public void establish(){
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
            return;
        }
        this.connectionEstablished = true;

    }

    public boolean close(){
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

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
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

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }
}
package com.byu.pmedia.database;

import com.byu.pmedia.log.PMLogger;

import java.sql.*;

public class AzureDatabaseConnection implements IDatabaseConnection{

    private String hostname;
    private String port;
    private String dbname;
    private String user;
    private String password;
    private String url;
    private Connection connection;
    private boolean connectionEstablished;


    public AzureDatabaseConnection(String hostname, String port, String dbname, String user, String password) {
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

    public boolean establish() throws SQLException {
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
            throw e;
        }
        this.connectionEstablished = true;

        return true;
    }

    public boolean close() throws SQLException {
        PMLogger.getInstance().info("Closing database connection");
        try{
            this.connection.close();
        }
        catch(SQLException e){
            PMLogger.getInstance().error("Failed to close database connection, SQL error code: " + e.getErrorCode());
            e.printStackTrace();
            throw e;
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

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean connectionIsEstablished() {
        return connectionEstablished;
    }
}

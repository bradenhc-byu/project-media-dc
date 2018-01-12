package com.byu.pmedia.database;

import com.byu.pmedia.log.PMLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyDatabaseConnection implements IDatabaseConnection {

    private String host = "localhost";
    private String port = "1527";
    private String dbname;
    private String user;
    private String password;
    private String url;

    private boolean connectionEstablished = false;
    private Connection connection;

    public DerbyDatabaseConnection(String host, String port, String dbname){
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.url = String.format("jdbc:derby://%s:%s/%s",
                this.host, this.port, this.dbname);
    }

    public DerbyDatabaseConnection(String host, String port, String dbname, String user, String password){
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.user = user;
        this.password = password;
        this.url = String.format("jdbc:derby://%s:%s/%s;user=%s;password=%s",
                this.host, this.port, this.dbname, this.user, this.password);
    }

    @Override
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

    @Override
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

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean connectionIsEstablished() {
        return this.connectionEstablished;
    }
}

/*
 * ---------------------------------------------------------------------------------------------------------------------
 *                            Brigham Young University - Project MEDIA StillFace DataCenter
 * ---------------------------------------------------------------------------------------------------------------------
 * The contents of this file contribute to the ProjectMEDIA DataCenter for managing and analyzing data obtained from the
 * results of StillFace observational experiments.
 *
 * This code is free, open-source software. You may distribute or modify the code, but Brigham Young University or any
 * parties involved in the development and production of this code as downloaded from the remote repository are not
 * responsible for any repercussions that come as a result of the modifications.
 */
package com.byu.pmedia.database;

import com.byu.pmedia.log.PMLogger;

import java.sql.*;

/**
 * AzureDatabaseConnection
 * Secure wrapper for creating and maintaining a connection with a Microsoft Azure Database. Implements the
 * IDatabaseConnection interface, allowing the application to be configured to use a new database on the fly.
 *
 * @author Braden Hitchcock
 */
public class AzureDatabaseConnection implements IDatabaseConnection {

    /* The host IP address, or 'localhost' */
    private String hostname;
    /* The port to connect to */
    private String port;
    /* The name of the database to connect to */
    private String dbname;
    /* The username associated with the database account */
    private String user;
    /* The password associated with the user */
    private String password;
    /* The JDBC URL to pass to the driver. Built in the constructor based on all
     * the above member variables */
    private String url;
    /* The connection returned by the JDBC driver once it has been successfully created */
    private Connection connection;
    /* True if the connection has been successfully established. False otherwise. */
    private boolean connectionEstablished;


    /**
     * Creates an instance of an AzureDatabaseConnection object with the given parameters.
     *
     * @param hostname The String representation of the host IP address, or 'localhost'
     * @param port The port on the host to connect to
     * @param dbname The name of the database
     * @param user The username for the database
     * @param password The password associated with the username
     */
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

    /**
     * Establishes a connection with a Microsoft Azure Database using the appropriate JDBC driver. This method must be
     * called before attempting to make any queries on the connection in this class.
     *
     * @return True if the connection is established successfully. False otherwise.
     * @throws SQLException Thrown if an error is encountered while trying to establish a connection
     */
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

    /**
     * Closes the database connection if the connection was previously established.
     *
     * @return True if the connection was successfully closed. False otherwise.
     * @throws SQLException Thrown if an error was encountered while trying to close the database connection
     */
    public boolean close() throws SQLException {
        PMLogger.getInstance().info("Closing database connection");
        try{
            if(this.connectionEstablished) this.connection.close();
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

    /* Getters for the member variables. There are no setters, since the URL is created in the constructor. If
     * the developer needs to create a different connection or modify an existing one, they need to use the
     * constructor. */
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

    /**
     * Provides access to the internal Connection object. Should only be called after establish()
     * @return The connection created by the JDBC driver with the class's URL
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * Provides status of the internal Connection object
     * @return True if the connection object has been initialized and a connection is up. False otherwise.
     */
    @Override
    public boolean connectionIsEstablished() {
        return connectionEstablished;
    }
}

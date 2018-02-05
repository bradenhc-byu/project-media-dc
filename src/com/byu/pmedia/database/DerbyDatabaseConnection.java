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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * DerbyDatabaseConnection
 * Secure wrapper for creating and maintaining a connection with an Apache Derby Database. Implements the
 * IDatabaseConnection interface, allowing the application to be configured to use a new database on the fly.
 *
 * @author Braden Hitchcock
 */
public class DerbyDatabaseConnection implements IDatabaseConnection {

    /* The host IP address, or 'localhost' */
    private String host;
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
     * Constructs a new instance of a DerbyDatabaseConnection that does not require a username and password to access
     * the database
     *
     * @param host The String host IP address of the database, or 'localhost'
     * @param port The port providing access to the database on the host
     * @param dbname The name of the database
     */
    public DerbyDatabaseConnection(String host, String port, String dbname){
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.url = String.format("jdbc:derby://%s:%s/%s;create=true",
                this.host, this.port, this.dbname);
    }

    /**
     * Constructs a new instance of a DerbyDatabaseConnection that requires a username and password
     *
     * @param host The String host IP address of the database, or 'localhost'
     * @param port The port providing access to the database on the host
     * @param dbname The name of the database
     * @param user The username accessing the database
     * @param password The password associated with the username
     */
    public DerbyDatabaseConnection(String host, String port, String dbname, String user, String password){
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.user = user;
        this.password = password;
        this.url = String.format("jdbc:derby://%s:%s/%s;user=%s;password=%s;create=true",
                this.host, this.port, this.dbname, this.user, this.password);
    }

    /**
     * Establishes a connection with a Apache Derby Database using the appropriate JDBC driver. This method must be
     * called before attempting to make any queries on the connection in this class.
     *
     * @return True if the connection is established successfully. False otherwise.
     * @throws SQLException Thrown if an error is encountered while trying to establish a connection
     */
    @Override
    public boolean establish() throws SQLException {
        this.connectionEstablished = false;
        try{
            this.connection = DriverManager.getConnection(this.url);

            PMLogger.getInstance().info("Database connection successful");
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
        this.connectionEstablished = false;
        return true;
    }

    /**
     * Provides access to the internal Connection object. Should only be called after establish()
     * @return The connection created by the JDBC driver with the class's URL
     */
    @Override
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Provides status of the internal Connection object
     * @return True if the connection object has been initialized and a connection is up. False otherwise.
     */
    @Override
    public boolean connectionIsEstablished() {
        return this.connectionEstablished;
    }
}

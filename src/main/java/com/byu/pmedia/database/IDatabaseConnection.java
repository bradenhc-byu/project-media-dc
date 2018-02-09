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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * IDatabaseConnection
 * Interface for the all database connections. Helps provide a simplified method of using several different
 * Database connections for the application
 */
public interface IDatabaseConnection {

    /**
     * Interface method to be implemented for establishing a connection with a database server
     *
     * @return True if the connection is successful, false otherwise
     */
    boolean establish() throws SQLException;

    /**
     * Interface method to be implemented to close a connection with a database server
     *
     * @return True if the connection is successfully closed, false otherwise
     */
    boolean close() throws SQLException;

    /**
     * Provides access to the implementation's internal Connection object
     * @return The internal Connection object if it has been instantiated. Null otherwise.
     */
    Connection getConnection();

    /**
     * Provides status on whether the database connection is up or down
     * @return True if the connection has been established and is up, false otherwise.
     */
    boolean connectionIsEstablished();
}

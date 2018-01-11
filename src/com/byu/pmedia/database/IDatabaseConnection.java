package com.byu.pmedia.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
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

    Connection getConnection();

    boolean connectionIsEstablished();
}

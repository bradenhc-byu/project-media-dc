package com.byu.pmedia.database;

import java.sql.ResultSet;

public interface ISQLDatabaseQuery {

    /**
     * When called it executes the SQL database query using the provided java SQL connection object
     *
     * @return True if the query executes successfully, false otherwise
     */
    ResultSet execute(AzureDatabaseConnection connection);
}

package com.byu.pmedia.database;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class HsqlDatabaseConnectionTest {

    private String filepath = "/home/braden/Apps/hsqldb-2.4.0/servers/development";
    private String dbname = "devsql";
    private String user = "";
    private String password = "";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void establish() {

        try {
            boolean result = new HsqlDatabaseConnection(filepath, dbname).establish();
            assertTrue(result);
        }
        catch(SQLException e){
            e.printStackTrace();
            assertTrue(false);
        }

    }

    @Test
    public void close() {
        try {
            HsqlDatabaseConnection databaseConnection = new HsqlDatabaseConnection(filepath, dbname);
            boolean result = databaseConnection.establish();
            assertTrue(result);
            result = databaseConnection.close();
            assertTrue(result);
        }
        catch(SQLException e){
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
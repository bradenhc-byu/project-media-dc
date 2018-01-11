package com.byu.pmedia.database;

import org.junit.Before;
import org.junit.Test;

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

        boolean result = new HsqlDatabaseConnection(filepath, dbname).establish();
        assertTrue(result);

    }

    @Test
    public void close() {
    }
}
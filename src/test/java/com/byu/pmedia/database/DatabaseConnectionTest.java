package com.byu.pmedia.database;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    String host = "hitch-dev.database.windows.net";
    String port = "1433";
    String dbname = "project_media";
    String user = "hitch";
    String password = "$wtHr0fPryr";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void establish() {

        try {
            boolean result = new AzureDatabaseConnection(this.host, this.port, this.dbname, this.user, this.password).establish();
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
            AzureDatabaseConnection dbc = new AzureDatabaseConnection(this.host, this.port, this.dbname, this.user, this.password);
            dbc.establish();
            boolean result = dbc.close();
            assertTrue(result);
        }
        catch(SQLException e){
            e.printStackTrace();
            assertTrue(false);
        }

    }
}
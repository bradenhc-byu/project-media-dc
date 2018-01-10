package com.byu.pmedia.database;

import org.junit.Before;
import org.junit.Test;

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

        boolean result = new DatabaseConnection(this.host, this.port, this.dbname, this.user, this.password).establish();
        assertTrue(result);

    }

    @Test
    public void close() {

        DatabaseConnection dbc = new DatabaseConnection(this.host, this.port, this.dbname, this.user, this.password);
        dbc.establish();
        boolean result = dbc.close();
        assertTrue(result);

    }
}
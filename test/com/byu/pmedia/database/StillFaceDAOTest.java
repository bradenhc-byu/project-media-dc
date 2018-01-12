package com.byu.pmedia.database;

import com.byu.pmedia.model.StillFaceImportData;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;

public class StillFaceDAOTest {

    IDatabaseConnection databaseConnection;
    StillFaceDAO stillFaceDAO;

    @Before
    public void setUp() throws Exception {

        String host = "localhost";
        String port = "1527";
        String dbname = "project_media";
        String user = "hitch";
        String password = "$wtHr0fPryr";
        databaseConnection = new DerbyDatabaseConnection(host, port, dbname);
        stillFaceDAO = new StillFaceDAO(databaseConnection);

    }

    @Test
    public void insertImportData() {
        StillFaceImportData importData = new StillFaceImportData("testfilename.txt", 1, 2,
                3, "my alias test", new Date(System.currentTimeMillis()));
        int key = stillFaceDAO.insertImportData(importData);
        assertNotEquals(-1, key);
    }

    @Test
    public void getImportData() {
    }

    @Test
    public void updateImportData() {
    }

    @Test
    public void insertCodeData() {
    }

    @Test
    public void getCodeDataFromImport() {
    }

    @Test
    public void getCodeDataFromFamilyID() {
    }

    @Test
    public void updateCodeData() {
    }

    @Test
    public void insertNewCode() {
    }

    @Test
    public void getCode() {
    }

    @Test
    public void updateExistingCode() {
    }

    @Test
    public void deleteExistingCode() {
    }

    @Test
    public void insertNewTag() {
    }

    @Test
    public void getTag() {
    }

    @Test
    public void updateExistingTag() {
    }

    @Test
    public void deleteExistingTag() {
    }

    @Test
    public void initializeDatabase() {
    }

    @Test
    public void createTables() {
        boolean result = this.stillFaceDAO.createTables(DatabaseMode.DERBY);
        assertTrue(result);
    }

    @Test
    public void dropTables() {
    }
}
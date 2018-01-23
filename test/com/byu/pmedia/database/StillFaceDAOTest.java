package com.byu.pmedia.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StillFaceDAOTest {


    StillFaceDAO dao;

    @Before
    public void setUp() throws Exception {

        String host = "localhost";
        String port = "1527";
        String dbname = "project_media";

        dao = new StillFaceDAO(new DerbyDatabaseConnection(host, port, dbname));

    }

    @Test
    public void generateFromConfig() {
    }

    @Test
    public void insertImportData() {
    }

    @Test
    public void getImportData() {
    }

    @Test
    public void updateImportData() {
    }

    @Test
    public void cleanImportData() {
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
    public void createTables() {

        assertTrue(dao.createTables(DatabaseMode.DERBY));

    }

    @Test
    public void dropTables() {

        assertTrue(dao.dropTables());

    }

    @Test
    public void openConnection() {
    }

    @Test
    public void closeConnection() {
    }

    @Test
    public void lockConnection() {
    }

    @Test
    public void unlockConnection() {
    }

    @Test
    public void isDatabaseInitialized() {
    }
}
package com.byu.pmedia.database;

import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceCodeData;
import com.byu.pmedia.model.StillFaceImportData;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.Map;

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
        Map<Integer, StillFaceImportData> dataMap = this.stillFaceDAO.getImportData(0);
        assertTrue(dataMap.get(2).getAlias().equals("my alias test"));
    }

    @Test
    public void updateImportData() {
        StillFaceImportData importData = new StillFaceImportData(2,"testfilename.txt", 1, 2,
                3, "An updated alias", new Date(System.currentTimeMillis()));
        boolean result = stillFaceDAO.updateImportData(importData);
        assertTrue(result);
    }

    @Test
    public void insertCodeData() {
        StillFaceCodeData codeData = new StillFaceCodeData(2, 0, 5000,
                new StillFaceCode(2,"testcode"), "comment");
        int key = stillFaceDAO.insertCodeData(codeData);
        assertNotEquals(-1, key);
    }

    @Test
    public void getCodeDataFromImport() {
        Map<Integer, StillFaceCodeData> codeDataMap = this.stillFaceDAO.getCodeDataFromImport(2);
        assertTrue(codeDataMap.get(1).getComment().equals("comment"));
    }

    @Test
    public void getCodeDataFromFamilyID() {
        Map<Integer, StillFaceCodeData> codeDataMap = this.stillFaceDAO.getCodeDataFromFamilyID(2);
        assertTrue(codeDataMap.get(1).getComment().equals("comment"));
    }

    @Test
    public void updateCodeData() {
        boolean result = this.stillFaceDAO.updateCodeData(new StillFaceCodeData(1, 2, 0,
                7000, new StillFaceCode(2, "testcode"), "new comment"));
        assertTrue(result);
    }

    @Test
    public void insertNewCode() {
        int key = this.stillFaceDAO.insertNewCode(new StillFaceCode(1,"code1"));
        assertNotEquals(-1, key);
        key = this.stillFaceDAO.insertNewCode(new StillFaceCode(2,"code2"));
        assertNotEquals(-1, key);
    }

    @Test
    public void getCode() {
        Map<Integer, StillFaceCode> codeMap = this.stillFaceDAO.getCode(0);
        assertTrue(codeMap.get(1).getName().equals("code1"));
    }

    @Test
    public void updateExistingCode() {
        boolean result = this.stillFaceDAO.updateExistingCode(new StillFaceCode(1, "New Code 1 Name"));
        assertTrue(result);
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
        boolean result = this.stillFaceDAO.createTables(DatabaseMode.DERBY);
        assertTrue(result);
        result = this.stillFaceDAO.createTables(DatabaseMode.DERBY);
        assertFalse(result);
    }

    @Test
    public void dropTables() {
        boolean result = this.stillFaceDAO.dropTables();
        assertTrue(result);
        result = this.stillFaceDAO.dropTables();
        assertFalse(result);

    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.guitarteacher.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author dario
 */
public class HttpConnectorTest {
    
    public HttpConnectorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        DatabaseConnector.createTables();
        //faccio inserimenti su DB per i test
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/616954", "root", "root");
              PreparedStatement psNote = co.prepareStatement("INSERT INTO nota (nome, pathaudio, pathimage) VALUES (?,?,?);");
              PreparedStatement psCanzone = co.prepareStatement("INSERT INTO canzone (idcanzone, titolo, artista, durata) VALUES (?, ?, ?, ?)");
              PreparedStatement psEsecuzioni = co.prepareStatement("INSERT INTO esecuzione (idcanzone, nota, tempo) VALUES (?, ?, ?);")) 
        {
            psCanzone.setInt(1, 1);
            psCanzone.setString(2, "test");
            psCanzone.setString(3, "test");
            psCanzone.setString(4, "00:00");
            psCanzone.executeUpdate();
            
            psNote.setString(1, "DO");
            psNote.setString(2, "test");
            psNote.setString(3, "test");
            
            psEsecuzioni.setInt(1, 1);
            psEsecuzioni.setString(2, "DO");
            psEsecuzioni.setInt(3, 1000);
            
        } catch (SQLException e) {
            System.out.println(e);
            fail("Errore sql");
        }
    }
    
    @AfterAll
    public static void tearDownClass() {
        //elimino il DB
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");)
        {
            Statement st = co.createStatement();
            String sql = "DROP DATABASE 616954";
            st.executeUpdate(sql);
        }catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /*Test di getRequest su /esecuzione/canzone?idcanzone=1 della classe HttpConnector.*/
    @Test
    public void testGetRequest() throws Exception {
        System.out.println("TEST 1: getRequest");
        String uri = "/esecuzione/canzone";
        String urlParameters = "idcanzone=1";
        int expResult = 200;
        HttpResponse result = HttpConnector.getRequest(uri, urlParameters);
        int responseCode = result.getResponseCode();
        assertEquals(expResult, responseCode);
    }
    
    @Test
    public void testDeleteRequest() throws Exception{
        System.out.println("TEST 2: deleteRequest");
        String uri = "/canzoni/remove";
        String urlParameters = "idcanzone=1";
        int expResult = 200;
        HttpResponse result = HttpConnector.deleteRequest(uri, urlParameters);
        int responseCode = result.getResponseCode();
        assertEquals(expResult, responseCode);
        
    }

    
}

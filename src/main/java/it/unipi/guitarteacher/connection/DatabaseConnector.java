package it.unipi.guitarteacher.connection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.ibatis.jdbc.ScriptRunner;


public class DatabaseConnector {
    
    private static final Logger logger = LogManager.getLogger(DatabaseConnector.class.getName());
    
    private static final String DB_USER = "root";
    private static final String DB_PSW = "root";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/616954";
    
    private DatabaseConnector(){}
    
    public static void createTables(){
        try (
             Connection co = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW);
             BufferedReader reader = new BufferedReader(new FileReader(DatabaseConnector.class.getResource("/mysql/tabelle.sql").getPath()));) 
        {  
            ScriptRunner sr = new ScriptRunner(co);
            sr.runScript(reader);
        }
        catch (IOException | SQLException ioe) {
            logger.error(ioe);
        }
    }
    
    public static void popolaTabelle(){
        try (Connection co = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW);
             PreparedStatement psNote = co.prepareStatement("INSERT INTO nota (nome, pathaudio, pathimage) VALUES (?,?,?);");
             PreparedStatement psCanzoni = co.prepareStatement("INSERT INTO canzone (idcanzone, titolo, artista, durata) VALUES (?,?,?,?);");
             PreparedStatement psEsecuzioni = co.prepareStatement("INSERT INTO esecuzione (idcanzone, nota, tempo) VALUES (?, ?, ?);")
             )
        {
            //controllo che la tabella sia vuota (se esecuzione è presente garantisce riempimento delle altre due tabelle)
            Statement st = co.createStatement();
            ResultSet result = st.executeQuery("SELECT EXISTS (SELECT 1 FROM esecuzione);");
            result.next();
            if(result.getInt(1)==1)
                return;
            
            //substring(6) serve per togliere dalla stringa la prima parte ("file:/")
            Path pathNote = Path.of(DatabaseConnector.class.getResource("/json/note.json").toString().substring(6));
            String jsonNote = Files.readString(pathNote);
            
            Path pathCanzoni = Path.of(DatabaseConnector.class.getResource("/json/canzoni.json").toString().substring(6));
            String jsonCanzoni = Files.readString(pathCanzoni);
            
            Path pathEsecuzioni = Path.of(DatabaseConnector.class.getResource("/json/esecuzioni.json").toString().substring(6));
            String jsonEsecuzioni = Files.readString(pathEsecuzioni);
            
            Gson gson = new Gson();
            JsonArray note = gson.fromJson(jsonNote, JsonArray.class);
            for(JsonElement n : note){
                JsonObject nota = n.getAsJsonObject();
                psNote.setString(1, nota.get("nome").getAsString());
                psNote.setString(2, nota.get("pathaudio").getAsString());
                psNote.setString(3, nota.get("pathimage").getAsString());
                psNote.executeUpdate();
            }
            
            JsonArray canzoni = gson.fromJson(jsonCanzoni, JsonArray.class);
            for(JsonElement c : canzoni){
                JsonObject canzone = c.getAsJsonObject();
                psCanzoni.setInt(1, canzone.get("idcanzone").getAsInt());
                psCanzoni.setString(2, canzone.get("titolo").getAsString());
                psCanzoni.setString(3, canzone.get("artista").getAsString());
                psCanzoni.setString(4, canzone.get("durata").getAsString());
                psCanzoni.executeUpdate();
            }
            
            JsonArray esecuzioni = gson.fromJson(jsonEsecuzioni, JsonArray.class);
            for(JsonElement e : esecuzioni){
                JsonObject esec = e.getAsJsonObject();
                psEsecuzioni.setInt(1, esec.get("idcanzone").getAsInt());
                psEsecuzioni.setString(2, esec.get("nota").getAsString());
                psEsecuzioni.setInt(3, esec.get("tempo").getAsInt());
                psEsecuzioni.executeUpdate();
            }
                     
        }
        catch (IOException | SQLException ioe) {
            logger.error(ioe);
        }
    }
    
}

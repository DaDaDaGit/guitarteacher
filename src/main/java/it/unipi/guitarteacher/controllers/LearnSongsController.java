package it.unipi.guitarteacher.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unipi.guitarteacher.App;
import it.unipi.guitarteacher.Canzone;
import it.unipi.guitarteacher.connection.HttpConnector;
import it.unipi.guitarteacher.connection.HttpResponse;
import it.unipi.guitarteacher.connection.SessionData;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LearnSongsController {
    
     private static final Logger logger = LogManager.getLogger(HttpConnector.class.getName());
    
    @FXML Button buttonHomepage;
    @FXML Button buttonAccordi;
    
    @FXML TableView<Canzone> canzoniTable;
    @FXML TableColumn<Canzone,String> titoloCol;
    @FXML TableColumn<Canzone,String> artistaCol;
    @FXML TableColumn<Canzone,String> durataCol;
    @FXML TableColumn<Canzone,String> azioneCol;
    
    private ObservableList<Canzone> ol = FXCollections.observableArrayList();
     
    @FXML
    public void initialize(){
        canzoniTable.setPrefWidth(500);
        canzoniTable.setMaxWidth(500);
        titoloCol.setPrefWidth(150);
        titoloCol.setMaxWidth(150);
        titoloCol.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        
        artistaCol.setPrefWidth(150);
        artistaCol.setMaxWidth(150);
        artistaCol.setCellValueFactory(new PropertyValueFactory<>("artista"));
        
        durataCol.setPrefWidth(100);
        durataCol.setMaxWidth(100);
        durataCol.setCellValueFactory(new PropertyValueFactory<>("durata"));
        
        azioneCol.setPrefWidth(100);
        azioneCol.setMaxWidth(100);
        azioneCol.setCellValueFactory(new PropertyValueFactory<>("btnImpara"));
        
        Task task = new Task<Void>() { 
           @Override public Void call() {
            try{
                String jsonCanzoni;
                HttpResponse response = HttpConnector.getRequest("/canzoni/all", "");
                jsonCanzoni = response.getResponseBody();

                Gson gson = new Gson();
                JsonArray canzoni = gson.fromJson(jsonCanzoni, JsonArray.class);
                for(JsonElement c : canzoni){
                    JsonObject song = c.getAsJsonObject();
                    Button btnImpara = new Button("Impara");
                    int idCanzone = song.get("idcanzone").getAsInt();
                    btnImpara.setOnAction(ae -> { //lambda function per caricare nuova pagina passando come parametri l'id della canzone
                        try{
                            //salvo id in una variabile di sessione che richiamerò al caricamento della pagina song.fxml
                            SessionData.getInstance().setIdCanzone(idCanzone);
                            App.setRoot("song");
                        } catch (IOException ioe){
                            logger.error(ioe);
                        }                    
                    });
                    String titolo = song.get("titolo").getAsString();
                    String artista = song.get("artista").getAsString();
                    String durata = song.get("durata").getAsString();
                    Canzone newCanzone = new Canzone(titolo,artista,durata,btnImpara);
                    //associo l'id ad una chiave titolo+artista così da recuperarlo per la rimozione
                    newCanzone.setIdMap(titolo+artista, idCanzone);
                    ol.add(newCanzone);
                }
                canzoniTable.setItems(ol);

            } catch (IOException ioe){
                System.out.println(ioe);
            }
            return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML
    private void remove(){
        Task task = new Task<Void>(){
            @Override public Void call(){
                try{
                    Canzone c = canzoniTable.getSelectionModel().getSelectedItem();
                    int id = c.getIdCanzone(c.getTitolo()+c.getArtista());
                    HttpResponse response = HttpConnector.deleteRequest("/canzoni/remove", "idcanzone=" + id);
                    
                    ol.remove(c);
                    canzoniTable.setItems(ol);
                    canzoniTable.refresh();
                    
                } catch (IOException ioe){
                   logger.error(ioe);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML
    private void switchToHomepage() throws IOException {
        App.setRoot("homepage");
    }
    
    @FXML
    private void switchToAccordi() throws IOException {
        App.setRoot("accordi");
    }
}

package it.unipi.guitarteacher.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unipi.guitarteacher.App;
import it.unipi.guitarteacher.Nota;
import it.unipi.guitarteacher.connection.HttpConnector;
import it.unipi.guitarteacher.connection.HttpResponse;
import java.io.File;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AccordiController {

    @FXML Button buttonHomepage;
    @FXML Button buttonCanzoni;
    
    @FXML TableView<Nota> noteTable;
    @FXML TableColumn<Nota,String> nomeCol;
    @FXML TableColumn<Nota,String> imgCol;
    @FXML TableColumn<Nota,String> audioCol;
    
    @FXML MediaPlayer mediaPlayer;
    
    private ObservableList<Nota> ol = FXCollections.observableArrayList();
    
    @FXML
    public void initialize(){ //funzione chiamata al momento dell'apertura della pagina      
        
        noteTable.setPrefWidth(510);
        noteTable.setMaxWidth(510);
        nomeCol.setPrefWidth(50);
        nomeCol.setMaxWidth(50);
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        imgCol.setPrefWidth(350);
        imgCol.setMaxWidth(350);
        imgCol.setCellValueFactory(new PropertyValueFactory<>("img"));
        
        audioCol.setPrefWidth(100);
        audioCol.setMaxWidth(100);
        audioCol.setCellValueFactory(new PropertyValueFactory<>("btnAudio"));
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {

                    String jsonNote;
                    HttpResponse response = HttpConnector.getRequest("/note/all", "");
                    jsonNote = response.getResponseBody();

                    Gson gson = new Gson();
                    JsonArray note = gson.fromJson(jsonNote, JsonArray.class); //jsonNote sarà la risposta http
                    for(JsonElement n : note){
                        JsonObject nota = n.getAsJsonObject();
                        ImageView imgAccordo =  new ImageView(new Image(nota.get("pathimage").getAsString()));
                        imgAccordo.setFitHeight(330);
                        imgAccordo.setFitWidth(330);
                        Button btnAudio = new Button("Ascolta");
                        btnAudio.setOnAction(ae -> { //lambda function per ascoltare audio diverso
                            String fileName = nota.get("pathaudio").getAsString();
                            playSound(fileName);
                        });
                        Nota newNota = new Nota(nota.get("nome").getAsString(), btnAudio, imgAccordo);
                        ol.add(newNota);
                    }
                    noteTable.setItems(ol);

                } catch (IOException ioe){
                    System.out.println(ioe);
                }
                return null;
            } 
        }; 
        new Thread(task).start();
    }

    private void playSound(String fileName){
        String path = getClass().getResource(fileName).getPath();
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(0);
        mediaPlayer.play();
    }
    
    
    @FXML
    private void switchToHomepage() throws IOException {
        App.setRoot("homepage");
    }
    
    @FXML
    private void switchToLearnSongs() throws IOException {
        App.setRoot("learnSongs");
    }
}
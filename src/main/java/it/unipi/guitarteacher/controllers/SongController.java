package it.unipi.guitarteacher.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unipi.guitarteacher.App;
import it.unipi.guitarteacher.Esecuzione;
import it.unipi.guitarteacher.Nota;
import it.unipi.guitarteacher.connection.HttpConnector;
import it.unipi.guitarteacher.connection.HttpResponse;
import it.unipi.guitarteacher.connection.SessionData;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SongController {
    
    private int idCanzone;
    private int elapsedTime;
    private boolean playing = false;
    
    private Esecuzione es;
    
    @FXML Label titolo;
    
    @FXML TableView<Nota> noteTable;
    @FXML TableColumn<Nota,String> nomeCol;
    @FXML TableColumn<Nota,String> imgCol;
    
    @FXML Label daSuonareText;
    @FXML ImageView daSuonare;
    @FXML Label prossimoAccordoText;
    @FXML ImageView prossimoAccordo;
    @FXML Pane backPane;
    
    @FXML Button btnPlay;
    @FXML Button btnRestart;
    @FXML MediaPlayer mediaPlayer;
    
    private ObservableList<Nota> ol = FXCollections.observableArrayList();
    
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0.05),
            e -> {
              elapsedTime += 50;
              boolean finita = es.isEnded(es.getCurrentInd());
              long time = 0;
              if(!finita){
                   time = es.getTempo(es.getCurrentInd());
              }
              //cambio l'accordo e la nota da 200 millisecondi prima          
              if(elapsedTime + 200 == time){
                    String nota = es.getNota(es.getCurrentInd());
                    daSuonareText.setText(nota);
                    String nextNota;
                    if(!es.isEnded(es.getCurrentInd() + 1)){
                       nextNota = es.getNota(es.getCurrentInd() + 1);
                       prossimoAccordo.setImage(new Image(es.getImg(nextNota)));    
                    }else{
                       nextNota = "FINE";
                       btnPlay.setDisable(true);
                       prossimoAccordoText.setText("FINE");
                       prossimoAccordo.setImage(new Image("/img/applausi.png"));
                    }
                    prossimoAccordoText.setText(nextNota);
                    
                    daSuonare.setImage(new Image(es.getImg(nota)));
                         
              }
              
              //"illumino" la nota da 100 millisecondi prima
              if(elapsedTime + 100 == time){
                  backPane.setStyle("-fx-background-color: #f7d67c;");
                  backPane.setOpacity(0.3);
              }
              
              if(elapsedTime >= time && !finita){
                  String nota = es.getNota(es.getCurrentInd());
                  playSound(es.getAudio(nota));
                  backPane.setOpacity(0);
                  es.incCurrentInd();
              }
              
            }));
    
    @FXML
    public void initialize(){
        noteTable.setPrefWidth(265);
        noteTable.setMaxWidth(265);
        nomeCol.setPrefWidth(50);
        nomeCol.setMaxWidth(50);
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        imgCol.setPrefWidth(200);
        imgCol.setMaxWidth(200);
        imgCol.setCellValueFactory(new PropertyValueFactory<>("img"));
        
        ImageView playIcon = new ImageView(new Image("/img/play-icon.png"));
        playIcon.setFitHeight(30);
        playIcon.setFitWidth(30);
        btnPlay.setGraphic(playIcon);
        ImageView restartIcon = new ImageView(new Image("/img/restart-icon.png"));
        restartIcon.setFitHeight(30);
        restartIcon.setFitWidth(30);
        btnRestart.setGraphic(restartIcon);
        
        //recupero id canzone da variabile di sessione e inizializzo la pagina in base al valore
        idCanzone = SessionData.getInstance().getIdCanzone();
        //non uso task perché mi da errori quando faccio update di elementi della UI
        Platform.runLater(() -> {
            try{
                String param = "idcanzone=" + idCanzone;
                String jsonCanzone;
                HttpResponse response = HttpConnector.getRequest("/canzoni/canzone", param);
                jsonCanzone = response.getResponseBody();

                Gson gson = new Gson();
                JsonObject song = gson.fromJson(jsonCanzone, JsonObject.class);
                //inizializzo titolo della pagina con il titolo e l'artista della canzone
                titolo.setText(song.get("titolo").getAsString() + " - " + song.get("artista").getAsString());

                String jsonEsecuzione;
                response = HttpConnector.getRequest("/esecuzione/canzone", param);
                jsonEsecuzione = response.getResponseBody();

                //raccolgo tutti i dati dell'esecuzione per usarli durante l'attività
                es = new Esecuzione(gson.fromJson(jsonEsecuzione, JsonArray.class));

                //ricavo i dati delle singole note
                List<String> noDuplicates = es.getDistinctNotes();
                for(String n : noDuplicates){
                    String jsonNota;
                    response = HttpConnector.getRequest("/note/nota", "nome=" + n); //passo il nome della nota come parametro
                    jsonNota = response.getResponseBody();
                    JsonObject nota = gson.fromJson(jsonNota, JsonObject.class);
                    String imgPath = nota.get("pathimage").getAsString();
                    //mapping per recuperare dopo le immagini e gli audio senza dover fare altre richieste http
                    es.setMapImgNota(n, imgPath); 
                    es.setMapAudioNota(n, nota.get("pathaudio").getAsString());
                    ImageView imgAccordo =  new ImageView(new Image(imgPath));
                    imgAccordo.setFitHeight(150);
                    imgAccordo.setFitWidth(150);
                    Nota newNota = new Nota(nota.get("nome").getAsString(), imgAccordo);
                    ol.add(newNota);
                }
                noteTable.setItems(ol);

                //imposto inizialmente le prime due note che suono
                noteIniziali();

            } catch(IOException ioe){
                System.out.println(ioe);
            }
        });
    } 

    @FXML
    private void playSong(){
        if(!playing){
            pause();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
        else{
            resume();
            timeline.pause();
        }
        
    }
    
    @FXML
    private void restartSong(){
        es.resetCurrentInd();
        resume();
        noteIniziali();
        btnPlay.setDisable(false);
        playing = false;
        elapsedTime = 0;
        backPane.setOpacity(0);
        timeline.playFromStart();
        timeline.pause();
    }
    
    private void playSound(String fileName){
        String path = getClass().getResource(fileName).getPath();
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(0);
        mediaPlayer.play();
    }
    
    //fa si che al prossimo click il bottone metta in pausa la canzone
    private void pause(){
        ImageView playIcon = new ImageView(new Image("/img/pause-icon.png"));
        playIcon.setFitHeight(30);
        playIcon.setFitWidth(30);
        btnPlay.setGraphic(playIcon);
        playing = true;
    }
    
    //fa si che al prossimo click il bottone faccia ripartire la canzone
    private void resume(){
        ImageView playIcon = new ImageView(new Image("/img/play-icon.png"));
        playIcon.setFitHeight(30);
        playIcon.setFitWidth(30);
        btnPlay.setGraphic(playIcon);
        playing = false;
    }
    
    //carica le prime due note sulla pagina
    private void noteIniziali(){
        String primaNota = es.getNota(es.getCurrentInd());
        daSuonareText.setText(primaNota);
        String secondaNota = es.getNota(es.getCurrentInd() + 1);
        prossimoAccordoText.setText(secondaNota);
        daSuonare.setImage(new Image(es.getImg(primaNota)));
        prossimoAccordo.setImage(new Image(es.getImg(secondaNota)));
    }
    
    @FXML
    private void switchToHomepage() throws IOException {
        //quando cambio pagina fermo esecuzione
        timeline.stop();
        App.setRoot("homepage");
    }
    
    @FXML
    private void switchToLearnSongs() throws IOException {
        //quando cambio pagina fermo esecuzione
        timeline.stop();
        App.setRoot("learnSongs");
    }
}

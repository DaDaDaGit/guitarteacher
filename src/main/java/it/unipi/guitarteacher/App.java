package it.unipi.guitarteacher;

import it.unipi.guitarteacher.connection.DatabaseConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //creo le tabelle e le popolo
        DatabaseConnector.createTables();
        DatabaseConnector.popolaTabelle();
     
        scene = new Scene(loadFXML("homepage"), 1280, 720);
        
        App.addStyle(scene, "homepage.css");
        App.addStyle(scene, "tableView.css");
        stage.setTitle("Guitar Teacher");
        stage.getIcons().add(new Image(App.class.getResource("/img/acusticGuitar_icon.png").toString()));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
  

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    

    public static void addStyle(Scene scene, String stylesheet) {
        String css = App.class.getResource("/styles/" + stylesheet).toExternalForm();
        scene.getStylesheets().add(css);
    }

    public static void main(String[] args) {
        launch();
    }
    
    public static Scene getScene(){
        return scene;
    }

}
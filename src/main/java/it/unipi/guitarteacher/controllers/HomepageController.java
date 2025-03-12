package it.unipi.guitarteacher.controllers;

import it.unipi.guitarteacher.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomepageController {

    @FXML Button buttonAccordi;
    @FXML Button buttonLearnSongs;
    
    @FXML
    private void switchToAccordi() throws IOException {
        App.setRoot("accordi");
    }
    
    @FXML
    private void switchToLearnSongs() throws IOException {
        App.setRoot("learnSongs");
    }
}

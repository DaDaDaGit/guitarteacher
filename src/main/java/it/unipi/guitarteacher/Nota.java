package it.unipi.guitarteacher;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class Nota {
    private String nome;
    private Button btnAudio;
    private ImageView img;
    
    public Nota(String n, Button ba, ImageView i){
        nome = n;
        btnAudio = ba;
        img = i;
    }
    
    //costruttore senza bottone
    public Nota(String n, ImageView i){
        nome = n;
        img = i;
        btnAudio = null;
    }

    //GETTERS & SETTERS
    public String getNome(){
        return nome;
    }
    
    public void setNome(String nome){
        this.nome = nome;
    }
    
    public Button getBtnAudio(){
        return btnAudio;
    }
    
    public void setBtnAudio(Button btnAudio){
        this.btnAudio = btnAudio;
    }
    
    public ImageView getImg(){
        return img;
    }
    
    public void setImg(ImageView img){
        this.img = img;
    }
}

package it.unipi.guitarteacher;

import java.util.HashMap;
import javafx.scene.control.Button;

public class Canzone {
    private String titolo;
    private String artista;
    private String durata;
    private Button btnImpara;
    private HashMap<String, Integer> idMap;
    
    public Canzone(String t, String a, String d, Button b){
        titolo = t;
        artista = a;
        durata = d;
        btnImpara = b;
        idMap = new HashMap<>();
    }

    public int getIdCanzone(String key){
        return idMap.get(key);
    }
    
    public void setIdMap(String key, int id){
        idMap.put(key, id);
    }

    //GETTERS & SETTERS
    public String getTitolo(){
        return titolo;
    }
    
    public void setTitolo(String titolo){
        this.titolo = titolo;
    }
    
    public String getArtista(){
        return artista;
    }
    
    public void setArtista(String artista){
        this.artista = artista;
    }
    public String getDurata(){
        return durata;
    }
    
    public void setDurata(String durata){
        this.durata = durata;
    }
    
    public Button getBtnImpara(){
        return btnImpara;
    }
    
    public void setBtnImpara(Button btnImpara){
        this.btnImpara = btnImpara;
    }
    
}

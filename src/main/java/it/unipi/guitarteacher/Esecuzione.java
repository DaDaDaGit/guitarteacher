package it.unipi.guitarteacher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Esecuzione {
    private ArrayList<String> note;
    private ArrayList<Long> tempi;
    //mi salvo anche il path delle immagini e degli audio degli accordi mappandolo con la nota
    private HashMap<String,String> imgNota;
    private HashMap<String,String> audioNota;
    
    private int currentInd;
    
    public Esecuzione(JsonArray ja){
        note = new ArrayList<>();
        tempi = new ArrayList<>();
        imgNota = new HashMap<>();
        audioNota = new HashMap<>();
        for(JsonElement e : ja){
            JsonObject esec = e.getAsJsonObject();
            note.add(esec.get("nota").getAsString());
            tempi.add(esec.get("tempo").getAsLong());
        }
    }
    
    public List<String> getDistinctNotes(){
        //prendo la lista delle note nella canzone per metterle nell'interfaccia
        List<String> noDuplicates = note.stream().distinct().collect(Collectors.toList());
        return noDuplicates;
    }
    
    public void setMapImgNota(String nota, String imgPath){
        imgNota.put(nota, imgPath);
    }
    
    public void setMapAudioNota(String nota, String audioPath){
        audioNota.put(nota, audioPath);
    }
    
    public int getCurrentInd(){
        return currentInd;
    }
    
    public void incCurrentInd(){
        currentInd++;
    }
    
    public void resetCurrentInd(){
        currentInd = 0;
    }
    
    public String getNota(int i){
        return note.get(i);
    }
    
    public long getTempo(int i){
        return tempi.get(i);
    }
    
    public String getImg(String key){
        return imgNota.get(key);
    }
    
    public String getAudio(String key){
        return audioNota.get(key);
    }
    
    public boolean isEnded(int i){
        if(tempi.size() <= i){
            return true;
        }
        return false;
    }
}

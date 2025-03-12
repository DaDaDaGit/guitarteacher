package it.unipi.guitarteacher.connection;

//singleton class
public class SessionData {
    
    private int idCanzone;
    
    // Istanza unica dell'oggetto
    private static SessionData sd;
    
    private SessionData(){
        
    }; 
    
    public static SessionData getInstance(){
        if(sd == null)
            sd = new SessionData();
        return sd;
    }
    
    public int getIdCanzone(){
        return idCanzone;
    }
    
    public void setIdCanzone(int idCanzone){
        this.idCanzone = idCanzone;
    }
}

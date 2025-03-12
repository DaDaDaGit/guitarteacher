package it.unipi.guitarteacher.connection;

public class HttpResponse {
    private int responseCode;
    private String responseBody;
    
    public HttpResponse(int responseCode, String responseBody){
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }
    
    public int getResponseCode(){
        return responseCode;
    }
    
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

}

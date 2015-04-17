/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author andreas
 */
public class SenseRESTClient {
    
    private String host;
    private int port;
    
    private String sensorURL;
    
    public static final String HOST_DEFAULT = "localhost";
    public static final int PORT_DEFAULT = 1337;
    
    protected SenseRESTClient(String host, int port) {
        this.host = host;
        this.port = port;
        
        this.sensorURL = "http://" + host + ":" + port + "/sensor";
    }
    
    protected SenseRESTClient() {
        this(HOST_DEFAULT, PORT_DEFAULT);
    }
    
    protected SensorPub get(String id) {
        SensorPub result;
        try {
            HttpResponse<JsonNode> response = Unirest.get(sensorURL + "/" + id)
                    .header("accept", "application/json")
                    .asJson();
            JSONObject ro = response.getBody().getObject().getJSONObject("_source");
            
            result = new SensorPub(
                    ro.getString(SensorPub.FIELD_NAME), 
                    ro.getString(SensorPub.FIELD_DESCRIPTION), 
                    ro.getString(SensorPub.FIELD_VALUE_TYPE), 
                    ro.get(SensorPub.FIELD_VALUE)){};
        } catch (UnirestException ex) {
            Logger.getLogger(SenseRESTClient.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        }
        
        return result;
    }
    
    protected String publishNew(SensorPub sp) {
        String id = null;
        try {
            HttpResponse<JsonNode> response = Unirest.post(sensorURL)
                    .header("accept", "application/json")
                    .field(SensorPub.FIELD_NAME, sp.getName())
                    .field(SensorPub.FIELD_DESCRIPTION, sp.getDescription())
                    .field(SensorPub.FIELD_VALUE_TYPE, sp.getValueType())
                    .field(SensorPub.FIELD_VALUE, sp.getValue())
                    .asJson();
            id = (String) response.getBody().getObject().get("_id");
            System.out.println("Response from publish:");
            System.out.println(response.getBody().getObject().get("_id"));
        } catch (UnirestException ex) {
            Logger.getLogger(SenseRESTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    protected boolean publishUpdate(Object value, String id) {
        int rCode = 0;
        try {
            System.out.println("Making request to " + sensorURL + "/" + id);
            HttpResponse<JsonNode> response = Unirest.put(sensorURL + "/" + id)
                    .header("accept", "application/json")
                    .field(SensorPub.FIELD_VALUE, value)
                    .asJson();
            System.out.println("Update response code: " + response.getStatus() + ":  " + response.getStatusText());
            rCode = response.getStatus();
        } catch (UnirestException ex) {
            Logger.getLogger(SenseRESTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rCode == 200;
    }
}

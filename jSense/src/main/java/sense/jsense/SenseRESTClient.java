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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is used to communicate with the Sense server, using its REST API.
 * It can be used to add/remove/find sensor records.
 * @author andreas
 */
public class SenseRESTClient {
    
    private String host;
    private int port;
    
    private String sensorURL;
    
    public static final String HOST_DEFAULT = "localhost";
    public static final int PORT_DEFAULT = 1337;
    
    /**
     * Initialize the client to communicate with a server at http://host:port
     * @param host
     * @param port 
     */
    public SenseRESTClient(String host, int port) {
        this.host = host;
        this.port = port;
        
        this.sensorURL = "http://" + host + ":" + port + "/sensor";
    }
    
    /**
     * Use the default HOST and PORT. I.e http://localhost:1337/
     */
    public SenseRESTClient() {
        this(HOST_DEFAULT, PORT_DEFAULT);
    }
    
    /**
     * Get a sensor update with the given ID. Returns null if no sensor is found.
     * @param id
     * @return 
     */
    public SensorPub get(String id) {
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
    
    /**
     * Publish a new sensor update. This method should only be used to create
     * new records. If you want to update an existing resource use publishUpdate.
     * @param sp
     * @return 
     */
    public String publishNew(SensorPub sp) {
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
    
    /**
     * Update the value of an existing sensor.
     * @param value
     * @param id
     * @return 
     */
    public boolean publishUpdate(Object value, String id) {
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
    
    /**
     * Perform a search for a set of sensors. Example: search("name:NameOfSensor AND value:>20").
     * Returns a list of results, or empty list if no results were found.
     * @param query
     * @return 
     */
    public List<SensorPub> search(String query) {
        List<SensorPub> result = new ArrayList<>();
        try {
            HttpResponse<JsonNode> response = Unirest.get(sensorURL + "/search")
                    .header("accept", "application/json")
                    .queryString("q", query)
                    .asJson();
            
            if(response.getBody().isArray()) {
                System.out.println("Response body is array.");
                JSONArray resultArray = response.getBody().getArray();
                for(int i=0; i<resultArray.length(); i++) {
                    JSONObject jsonResult = resultArray.getJSONObject(i).getJSONObject("_source");
                    System.out.println(jsonResult);
                    String name = jsonResult.getString(SensorPub.FIELD_NAME);
                    String description = jsonResult.getString(SensorPub.FIELD_DESCRIPTION);
                    String valueType = jsonResult.getString(SensorPub.FIELD_VALUE_TYPE);
                    Object value = jsonResult.get(SensorPub.FIELD_VALUE);
                    result.add(new SensorPub(name, description, valueType, value) {});
                }
            }
            else
                System.out.println("Response NOT array");
        } catch (UnirestException ex) {
            Logger.getLogger(SenseRESTClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}

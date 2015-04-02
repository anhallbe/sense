package sense.jsense;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is not an actual sensor. It will simply just send a random value to the 
 * Sense server once every second, simulating the behavior of a sensor.
 * @author dev
 */
public class PeriodicSensor {
    
    private final String SENSE_SERVER = "http://localhost:1337";
    private final String SENSOR_REST = "/sensor";

    public PeriodicSensor() throws UnirestException, InterruptedException {
        Random rand = new Random();
        while(true) {
            System.out.println("Posting new value...");
            postValue(rand.nextInt(100));
            Thread.sleep(1000);
        }
    }
    
    /**
     * Send a HTTP POST with the fields {name:'PeriodicSensor', value:value}
     * @param value
     * @throws UnirestException 
     */
    private void postValue(int value) throws UnirestException {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("name", "PeriodicSensor");
        fields.put("value", ""+value);
        
        int res = Unirest.post(SENSE_SERVER + SENSOR_REST)
                .header("accept", "application/json")
                .fields(fields)
                .asJson()
                .getStatus();
        if(res == 201)
            System.out.println("Successfully posted value " + value);
        else
            System.err.println("Something isn't right.. response was " + res);
    }
    
    public static void main(String[] args) {
        try {
            new PeriodicSensor();
        } catch (UnirestException ex) {
            Logger.getLogger(PeriodicSensor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(PeriodicSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

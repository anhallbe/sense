/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import sense.jsense.util.UpdateListener;
import sense.jsense.util.SensorPub;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andreas
 */
public class SenseService implements Runnable {
    SenseRESTClient client;
    private final long pollInterval;
    private final Map<String, UpdateListener> queries;
    private Date lastPollDate;
    private boolean running;
    
    public static long INTERVAL_FAST = 1000;
    public static long INTERVAL_SLOW = 5000;

    public SenseService(String host, int port, long pollInterval, boolean startNow) {
        this.pollInterval = pollInterval;
        queries = new ConcurrentHashMap<>();
        running = false;
        client = new SenseRESTClient(host, port);
        
        if(startNow)
            start();
    }
    
    /**
     * Start the (polling) service in a new thread.
     */
    public void start() {
        System.out.println("Starting SenseService...");
        if(!running) {
            running = true;
            new Thread(this).start();
            System.out.println("SenseService started");
        } else {
            System.out.println("SenseService is already running. Not starting it again...");
        }
    }
    
    /**
     * Kill the service.
     */
    public void stop() {
        if(running) {
            System.out.println("Stopping SenseService...");
            running = false;
            System.out.println("Stopped service.");
        } else {
            System.out.println("The service is not running.");
        }
    }
    
    /**
     * Subscribe to a topic, for instance: 'name:NameOfSensor AND value:[10 TO 20]'.
     * A callback will be made to the provided listener.
     * @param query
     * @param listener 
     */
    public void subscribe(String query, UpdateListener listener) {
        queries.put(query, listener);
    }
    
    public void removeSubscription(String query) {
        queries.remove(query);
    }
    
    /**
     * Publish a new sensor.
     * @param update 
     * @return The id of the sensor
     */
    public String publish(final SensorPub update) {
        String id = client.publishNew(update);
        return id;
    }
    
    /**
     * Publish an update of an existing sensor. This should be faster than 
     * publish/1 and require less network resources.
     * @param id id of the sensor, given by publish/1
     * @param newValue New value
     */
    public void publish(final String id, final Object newValue) {
        client.publishUpdate(newValue, id);
    }

    @Override
    public void run() {
        while(running) {
            try {
                lastPollDate = new Date();
                Thread.sleep(pollInterval);
                if(!running)
                    break;

                for(String query : queries.keySet()) {
                    String queryWithTimestamp = query + " AND updatedAt:>" + lastPollDate.getTime();  //Only interested in recent updates.
                    List<SensorPub> result = client.search(queryWithTimestamp);
                    
                    for(SensorPub res : result) {
                        queries.get(query).onUpdate(res);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SenseService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Polling stopped");
    }
    
}

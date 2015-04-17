/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;
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
    private long pollInterval;
    private Map<String, UpdateListener> queries;
    private Date lastPollDate;
    private boolean running;
    
    public static long INTERVAL_FAST = 1000;
    public static long INTERVAL_SLOW = 5000;

    public SenseService(String host, int port, long pollInterval, boolean startNow) {
        this.pollInterval = pollInterval;
        queries = new ConcurrentHashMap<>();
        running = false;
//        try {
        client = new SenseRESTClient(host, port);
//        } catch (IOException ex) {
//            Logger.getLogger(SenseService.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        if(startNow)
            start();
    }
    
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
    
    public void stop() {
        if(running) {
            System.out.println("Stopping SenseService...");
            running = false;
            System.out.println("Stopped service.");
        } else {
            System.out.println("The service is not running.");
        }
    }
    
    public void subscribe(String query, UpdateListener listener) {
        queries.put(query, listener);
    }
    
    public void removeSubscription(String query) {
        queries.remove(query);
    }
    
    public void publish(final SensorPub update) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
                client.publishNew(update);
//                } catch (SerializationException ex) {
//                    Logger.getLogger(SenseService.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }).start();
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
//                    if(!result.isEmpty())
//                        queries.get(query).onUpdate(result.get(0));     //TODO: Threading, bulk updates
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

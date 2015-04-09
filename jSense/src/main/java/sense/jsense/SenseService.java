/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andreas
 */
public abstract class SenseService extends Thread {
    SenseClient client;
    private long pollInterval;
    private Map<UUID, String> queries;
    private Queue<SensorPub> publicationBuffer;
    
    public static long INTERVAL_FAST = 1000;
    public static long INTERVAL_SLOW = 5000;

    public SenseService(long pollInterval) {
        this.pollInterval = pollInterval;
        queries = new ConcurrentHashMap<>();
        publicationBuffer = new ConcurrentLinkedQueue<>();
        try {
            client = new SenseClient();
        } catch (IOException ex) {
            Logger.getLogger(SenseService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public UUID subscribe(String query) {
        UUID id = UUID.randomUUID();
        queries.put(id, query);
        return id;
    }
    
    public void removeSubscription(UUID id) {
        queries.remove(id);
    }
    
    public void publish(SensorPub publication) {
        publicationBuffer.add(publication);
    }

    @Override
    public void run() {
        while(true) {
            System.out.println("Running...");
            try {
                Thread.sleep(pollInterval);

                //Send publications
                while(!publicationBuffer.isEmpty()) {
                    client.publish(publicationBuffer.poll());   //Should do this in bulk
                }

                for(UUID id : queries.keySet()) {
                    String query = queries.get(id);
                    List<SensorPub> result = client.search(query);
                    if(!result.isEmpty())
                        onUpdate(result.get(0), id);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SenseService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SerializationException ex) {
                Logger.getLogger(SenseService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public abstract void onUpdate(SensorPub update, UUID id);
}

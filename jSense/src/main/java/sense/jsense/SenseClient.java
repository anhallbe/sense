/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 *
 * @author andreas
 */
public class SenseClient {

    private Client client;
    
    private final String HOST_DEFAULT = "localhost";
    private final int PORT_DEFAULT = 9300;
    
    private final String INDEX_SENSE = "sense";
    private final String TYPE_SENSOR = "sensor";
    
    public SenseClient() throws IOException {
        System.out.println("Connecting..");
        client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(HOST_DEFAULT, PORT_DEFAULT));
        System.out.println("Connected to " + HOST_DEFAULT + ":" + PORT_DEFAULT);    }
    
    public void close() {
        client.close();
    }
    
    /**
     * Publish a sensor update.
     * @param sp
     * @return the ID of the publication.
     * @throws sense.jsense.SerializationException if sp could not be serialized to JSON.
     */
    public String publish(SensorPub sp) throws SerializationException {
        String json = sp.toJSON();
        IndexResponse response = client.prepareIndex(INDEX_SENSE, TYPE_SENSOR)
                .setSource(json)
                .execute()
                .actionGet();
        return response.getId();
    }
    
    /**
     * Get a sensor with a given ID.
     * @param id
     * @return 
     */
    public SensorPub get(String id) {
        GetResponse response = client.prepareGet(INDEX_SENSE, TYPE_SENSOR, id).execute().actionGet();
        String name = (String) response.getSource().get(SensorPub.FIELD_NAME);
        String description = (String) response.getSource().get(SensorPub.FIELD_DESCRIPTION);
        String valueType = (String) response.getSource().get(SensorPub.FIELD_VALUE_TYPE);
        String value = (String) response.getSource().get(SensorPub.FIELD_VALUE);
        
        return new SensorPub(name, description, valueType, value) {};
    }
    
    public List<SensorPub> search(String simpleQuery) {
        //System.out.println("Searching for " + simpleQuery);
        List<SensorPub> result = new ArrayList();
        
        SearchResponse response = client.prepareSearch()
                .setTypes(TYPE_SENSOR)
                .setQuery(QueryBuilders.queryStringQuery(simpleQuery))
                .execute()
                .actionGet();
        //System.out.println("Search response: " + response);
        for(SearchHit hit : response.getHits().getHits()) {
            String name = hit.getFields().get(SensorPub.FIELD_NAME).getValue();
            String description = hit.getFields().get(SensorPub.FIELD_DESCRIPTION).getValue();
            String valueType = hit.getFields().get(SensorPub.FIELD_VALUE_TYPE).getValue();
            String value = hit.getFields().get(SensorPub.FIELD_VALUE).getValue();
            SensorPub sp = new SensorPub(name, description, valueType, value) {};
            result.add(sp);
        }
        return result;
    }
    
    public static void main(String[] args) {
        try {
            new SenseClient();
        } catch (IOException ex) {
            Logger.getLogger(SenseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
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
        System.out.println("Connected to " + HOST_DEFAULT + ":" + PORT_DEFAULT);    
        newIndex();
    }
    
    public void close() {
        client.close();
    }
    
    private void newIndex() throws IOException {
        IndicesExistsResponse res = client.admin().indices().prepareExists(INDEX_SENSE).execute().actionGet();
        if(res.isExists())
            client.admin().indices().prepareDelete(INDEX_SENSE).execute().actionGet();
        
        CreateIndexRequestBuilder cirBuilder = client.admin().indices().prepareCreate(INDEX_SENSE);
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject(TYPE_SENSOR)
                        .startObject("_timestamp")
                            .field("enabled", true)
                        .endObject()
                    .endObject()
                .endObject();
        System.out.println("Mapping: " + mappingBuilder.string());
        cirBuilder.addMapping(TYPE_SENSOR, mappingBuilder);
        cirBuilder.execute().actionGet();
        System.out.println("Index created");
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
        Object value = response.getSource().get(SensorPub.FIELD_VALUE);
        
        return new SensorPub(name, description, valueType, value) {};
    }
    
    /**
     * Perform  a Lucene search. For example: "name:temperature* AND value:[20 TO 100]"
     * @param simpleQuery
     * @return 
     */
    public List<SensorPub> search(String simpleQuery) {
        //System.out.println("Searching for " + simpleQuery);
        List<SensorPub> result = new ArrayList();
        
        SearchResponse response = client.prepareSearch()
                .setTypes(TYPE_SENSOR)
                .setQuery(QueryBuilders.queryStringQuery(simpleQuery))
                .execute()
                .actionGet();
//        System.out.println("Search hits: " + response.getHits());
        for(SearchHit hit : response.getHits().getHits()) {
            String name = (String) hit.getSource().get(SensorPub.FIELD_NAME);
            String description = (String) hit.getSource().get(SensorPub.FIELD_DESCRIPTION);
            String valueType = (String) hit.getSource().get(SensorPub.FIELD_VALUE_TYPE);
            Object value = hit.getSource().get(SensorPub.FIELD_VALUE);
            SensorPub sp = new SensorPub(name, description, valueType, value) {};
            result.add(sp);
        }
        return result;
    }
}
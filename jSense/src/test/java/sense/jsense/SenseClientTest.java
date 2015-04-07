/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sense.jsense.sensors.HomeTemperatureSensor;

/**
 *
 * @author andreas
 */
public class SenseClientTest {
    
    static SenseClient client;
    
    public SenseClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("SETUP__________________________________");
        try {
            client = new SenseClient();
        } catch (IOException ex) {
            Logger.getLogger(SenseClientTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not create client instance");
        }
        System.out.println("/SETUP__________________________________");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("TEARDOWN__________________________________");
        client.close();
        System.out.println("/TEARDOWN__________________________________");
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of publish method, of class SenseClient.
     */
    @Test
    public void testPublish() throws Exception {
        System.out.println("PUBLISH__________________________________");
        SensorPub pub = new HomeTemperatureSensor(10);
        String id = client.publish(pub);
        
        assertNotNull(id);
        System.out.println("/PUBLISH__________________________________");
    }

    /**
     * Test of get method, of class SenseClient.
     */
    @Test
    public void testGet() {
        System.out.println("GET_____________________________________");
        Random rand = new Random();
        SensorPub pub = new HomeTemperatureSensor(rand.nextInt(10000));
        String id = null;
        try {
            id = client.publish(pub);
        } catch (SerializationException ex) {
            Logger.getLogger(SenseClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(id);
        System.out.println("Published with value " + pub.getValue());
        
        SensorPub getResult = client.get(id);
        
        assertEquals(pub.getName(), getResult.getName());
        assertEquals(pub.getDescription(), getResult.getDescription());
        assertEquals(pub.getValueType(), getResult.getValueType());
        assertEquals(pub.getValue(), getResult.getValue());
        System.out.println("Retreived with value " + getResult.getValue());
        System.out.println("/GET_____________________________________");
    }
    
    @Test
    public void testSearch() {
        System.out.println("SEARCH__________________________________");
        List<SensorPub> result = client.search("value:*");
        for(SensorPub r : result)
            System.out.println(r);
        System.out.println("/SEARCH_________________________________");
    }
}

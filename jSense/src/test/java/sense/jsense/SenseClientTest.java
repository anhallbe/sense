/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import java.io.IOException;
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
    
    SenseClient client;
    
    public SenseClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        try {
            client = new SenseClient();
        } catch (IOException ex) {
            Logger.getLogger(SenseClientTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not create client instance");
        }
    }
    
    @After
    public void tearDown() {
        client.close();
    }

    /**
     * Test of publish method, of class SenseClient.
     */
    @Test
    public void testPublish() throws Exception {
        System.out.println("publish");
        SensorPub pub = new HomeTemperatureSensor(10);
        String id = client.publish(pub);
        
        assertNotNull(id);
    }

    /**
     * Test of get method, of class SenseClient.
     */
    @Test
    public void testGet() {
        SensorPub pub = new HomeTemperatureSensor(999);
        String id = null;
        try {
            id = client.publish(pub);
        } catch (SerializationException ex) {
            Logger.getLogger(SenseClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(id);
        
        SensorPub getResult = client.get(id);
        
        assertEquals(pub.getName(), getResult.getName());
        assertEquals(pub.getDescription(), getResult.getDescription());
        assertEquals(pub.getValueType(), getResult.getValueType());
        assertEquals(pub.getValue(), getResult.getValue());
    }
    
}

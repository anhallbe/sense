/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

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
public class SenseRESTClientTest {
    
    public static SenseRESTClient client;
    
    public SenseRESTClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        client = new SenseRESTClient();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of publishNew method, of class SenseRESTClient.
     */
    @Test
    public void testPublish() {
        System.out.println("publish");
        SensorPub sp = new HomeTemperatureSensor(99);
        String result = client.publishNew(sp);
        assertNotNull(result);
    }
    
    /**
     * Test of publishUpdate method, of class SenseRESTClient.
     */
    @Test
    public void testUpdate() {
        System.out.println("publish/update");
        SensorPub sp = new HomeTemperatureSensor(50);
        String id = client.publishNew(sp);
        assertNotNull(id);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SenseRESTClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        sp.setValue(""+60);
        boolean success = client.publishUpdate(999, id);
        assertTrue(success);
    }
    
    /**
     * Test of get method, of class SenseRESTClient.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        SensorPub sp = new HomeTemperatureSensor(10000);
        String id = client.publishNew(sp);
        assertNotNull(id);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SenseRESTClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        SensorPub res = client.get(id);
        
        assertEquals(res.getName(), sp.getName());
        assertEquals(res.getValueType(), sp.getValueType());
        assertEquals(res.getValue(), sp.getValue());
        assertEquals(res.getDescription(), sp.getDescription());
    }
}

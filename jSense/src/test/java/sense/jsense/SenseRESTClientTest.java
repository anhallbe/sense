/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

import sense.jsense.util.SensorPub;
import sense.jsense.util.GeoLoc;
import java.util.Date;
import java.util.List;
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
        client = new SenseRESTClient("ec2.hallnet.eu", 1337);
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
    
    /**
     * Test of get method, of class SenseRESTClient.
     */
    @Test
    public void testSearch() {
        System.out.println("search");
        Date then = new Date();
        SensorPub sp = new HomeTemperatureSensor(9000);
        String id = client.publishNew(sp);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SenseRESTClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<SensorPub> searchResult = client.search("value:9000 AND name:home");
        assertTrue(searchResult.size() >= 1);
        System.out.println("Search: value:9000 AND name:home ---> success!");
        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SenseRESTClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Date now = new Date();
        List<SensorPub> sr2 = client.search("value:9000 AND name:home AND updatedAt:>" + now.getTime());
        System.out.println("Now: " + now.getTime());
        assertTrue(sr2.isEmpty());
        System.out.println("Search: updatedAt:>" + now.getTime() + " -----> Success!");*/
    }
    
    @Test
    public void testLocation() throws InterruptedException {
        GeoLoc loc = new GeoLoc("59.40326295,17.94443479");
        SensorPub locPub = new SensorPub("TestLocation", "just testing GeoLoc", SensorPub.TYPE_GEOLOC, loc, new Date()) {};
        String id = client.publishNew(locPub);
        
        Thread.sleep(1000);
        
        SensorPub res = client.get(id);
        
        assertEquals(res.getName(), locPub.getName());
        assertEquals(res.getDescription(), locPub.getDescription());
        assertEquals(res.getValueType(), locPub.getValueType());
        assertEquals(res.getValue(), locPub.getValue());
        
        GeoLoc resLoc = new GeoLoc((String) res.getValue());
        
        assertEquals(resLoc, loc);
        
        assertEquals(resLoc.distanceTo(loc), 0.0, 0.1);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense.sensors;

import java.util.Date;
import sense.jsense.util.SensorPub;

/**
 *
 * @author andreas
 */
public class HomeTemperatureSensor extends SensorPub {

    public HomeTemperatureSensor(int value) {
        super("home temperature", 
                "Used to measure the temperature of my home in Kista", 
                SensorPub.TYPE_INTEGER, 
                value,
                new Date());
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.examplepublisher;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import sense.jsense.SenseService;
import sense.jsense.util.SensorPub;

/**
 *
 * @author andreas
 */
public class ExamplePublisher {
    
    private SenseService service;
    
    public ExamplePublisher() {
        System.out.println("Starting publisher.");
//        service = new SenseService(SenseService.INTERVAL_FAST) {
//            @Override
//            public void onUpdate(SensorPub update, UUID id) {
//                System.out.println("Publisher got an update...which it should not.");
//                throw new UnsupportedOperationException("onUpdate is not implemented");
//            }
//        };
        service = new SenseService("ec2.hallnet.eu", 1337, SenseService.INTERVAL_FAST, false);
        
        service.start();
        System.out.println("Publishing..");
        while(true) {
            service.publish(new ExampleSensor(100));
            System.out.println("Published example sensor");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ExamplePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class ExampleSensor extends SensorPub {
        public ExampleSensor(Integer value) {
            super("ExampleSensor", 
                    "Just a sensor used to test the PubSub service.", 
                    SensorPub.TYPE_INTEGER, 
                    value,
                    new Date());
        }
    }
    
    public static void main(String[] args) {
        new ExamplePublisher();
    }
}

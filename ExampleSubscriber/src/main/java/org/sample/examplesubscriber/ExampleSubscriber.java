/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.examplesubscriber;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sense.jsense.SenseService;
import sense.jsense.util.SensorPub;
import sense.jsense.util.UpdateListener;

/**
 *
 * @author andreas
 */
public class ExampleSubscriber {
    private SenseService service;
    
    public ExampleSubscriber() {
        service = new SenseService("ec2.hallnet.eu", 1337, SenseService.INTERVAL_SLOW, true);
        
//        service.start();
        service.subscribe("name:ExampleSensor", new UpdateListener() {
            @Override
            public void onUpdate(SensorPub update) {
                System.out.println("Subscriber received an update:");
                System.out.println(update);
            }
        });
        System.out.println("Subscribed.");
    }
    
    public void waitForUpdates() {
        System.out.println("Waiting for updates...");
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ExampleSubscriber.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        ExampleSubscriber subscriber = new ExampleSubscriber();
        subscriber.waitForUpdates();
    }
}

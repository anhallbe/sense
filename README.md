# Sense
Middleware for sensor networks and ubiquitous computing

##What is this?
A piece of middleware that lets your toaster notify your phone about how your friend's shoes just realized that correlation sometimes imply causation. It's also a project for my Ubuquitous Computing class.

##jSense
A Java client library that lets the user send/receive sensor data in a decoupled way. The idea is that sensors/agents can be added to the system without (re-)configuring any of the existing components. Subscriptions are made through Lucene-like search queries.

###Example subscriber:
```java
  SenseService service = new SenseService(SenseService.INTERVAL_FAST, false);
  service.subscribe("description:(home AND temperature)", new UpdateListener() {
      onUpdate(SensorPup update) {
          print("The temperature in my home is " + update.getValue());
      }
  });
  service.start();
```

###Example publisher:
```java
  SenseService service = new SenseService();
  //Get some sensor reading from hardware/software
  HomeTemperatureUpdate update = new HomeTemperatureUpdate( //This class should extend SensorPub
      "KitchenTemperature",       //name
      "This gives the temperature of the kitchen in my home for some reason", //description
      SensorPup.INTEGER,          //The type of value (integer, boolean, geoLoc etc..)
      100);                       //It's hot in herre
  service.publish(update);
```


##Sense Server
A web server built with Node, Sails and ElasticSearch. The server API allows various sensors to publish and/or subscribe to data changes.

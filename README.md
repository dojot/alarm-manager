# Alarm Manager Server

The Alarm Manager Server handles all the alarm notification from dojot modules, receiving then via RabbitMQ and exposes
a REST API where the active alarms and their history can be queried by applications interested in this kind of
information. It also provides a Websocket where applications can subscribe for notifications regarding alarm namespaces. 

## Dependencies

The Alarm Manager is an application that runs over a JBoss Wildfly Application Server.

To build the Alarm Manager Server the requirements are:

 - Java JDK 8
 - Maven
 
On Ubuntu this dependencies can be installed with the following commands:
```bash
$ sudo apt install openjdk-8-jdk maven
```

## How to build the application

To build the application just run the following command at the root of the repository:
```bash
$ mvn install
```

Just wait until the compilation is completed, the application is generated at "ear/target/alarm-manager.ear"

## How to build the container

To build a container that contains the JBoss Wildfly server already packed and configured 
to run this application, run the command as follows:
```bash
$ docker build -f docker/Dockerfile -t alarm-manager .
```

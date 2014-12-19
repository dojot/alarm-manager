package com.cpqd.vppd.alarmmanager.mongoconnector;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.net.UnknownHostException;

/**
 * CDI Bean responsible for producing a {@link MongoClient} instance based on the
 * properties given by {@link System#getProperties()}. It may also be necessary
 * to provide authentication services per DB and configurability for the db name.
 */
@ApplicationScoped
public class MongoDBProducer {

    private DB db;

    @PostConstruct
    public void init() throws UnknownHostException {
        ConnectionProperties connectionProperties = ConnectionProperties.load(System.getProperties());

        MongoClient mongoClient = new MongoClient(connectionProperties.getServerAddress());
        db = mongoClient.getDB("alarms");
    }

    @Produces
    public DB createDB() {
        return db;
    }

}

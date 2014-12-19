package com.cpqd.vppd.alarmmanager.mongoconnector;

import com.cpqd.vppd.alarmmanager.mongoconnector.annotation.JongoCollection;
import com.cpqd.vppd.alarmmanager.mongoconnector.util.Reflection;
import com.mongodb.DB;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.net.UnknownHostException;

@ApplicationScoped
public class MongoCollectionProducer {

    @Inject
    DB mongoDb;

    Jongo jongo;

    @PostConstruct
    public void initialize() throws UnknownHostException {
        jongo = new Jongo(mongoDb);
    }

    @Produces
    @JongoCollection
    MongoCollection collection(InjectionPoint injectionPoint) {

        JongoCollection jongoCollectionAnnotation = Reflection.annotation(injectionPoint.getQualifiers(),
                JongoCollection.class);

        if (jongoCollectionAnnotation != null) {
            String collectionName = jongoCollectionAnnotation.value();
            return jongo.getCollection(collectionName);
        }

        throw new IllegalArgumentException();
    }

}

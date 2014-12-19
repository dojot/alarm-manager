package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.mongoconnector.annotation.JongoCollection;
import org.jongo.MongoCollection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Map;

/**
 * Repository bean for CRUD operations on alarms.
 */
@Stateless
public class AlarmRepository {

    @JongoCollection("alarms")
    @Inject
    private MongoCollection alarmsCollection;

    public void add(Alarm alarm) {
        alarmsCollection.save(alarm);
    }

    public Alarm findByPrimarySubject(Map<String, Object> primarySubject) {
        return alarmsCollection.findOne("{ primarySubject: # }", primarySubject).as(Alarm.class);
    }

//    public Alarm findByPrimarySubject(Set<DomainSpecificField> primarySubject) {
//        boolean firstParameter = true;
//
//        StringBuilder jpql = new StringBuilder("{ $and: [");
//        for (DomainSpecificField field : primarySubject) {
//            if (!firstParameter) {
//                jpql.append(",");
//            }
//            // FIXME Hibernate OGM with the MongoDB backend currently does not support named
//            // FIXME or positional parameters in queries, so we add them directly into the query.
//            // FIXME Revisit in the future and adjust when it is supported
//            jpql.append("{ primarySubject: { name: \"").append(field.getName()).append("\"");
//            jpql.append(", value: \"").append(field.getValue()).append("\"} }");
//
//            firstParameter = false;
//        }
//        jpql.append("] }");
//
//        Query query = this.getEntityManager().createNativeQuery(jpql.toString(), getPersistentClass());
//
//        try {
//            return (Alarm) query.getSingleResult();
//        } catch (NoResultException e) {
//            return null;
//        }
//    }
}

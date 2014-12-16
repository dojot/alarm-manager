package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField;
import com.cpqd.vppd.alarmmanager.utils.repository.GenericRepository;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Repository bean for CRUD operations on alarms.
 */
@Stateless
public class AlarmRepository extends GenericRepository<Alarm, String> {

    @Override
    protected Class<Alarm> getPersistentClass() {
        return Alarm.class;
    }

    public Alarm findByPrimarySubject(Set<DomainSpecificField> primarySubject) {
        boolean firstParameter = true;

        StringBuilder jpql = new StringBuilder("{ $and: [");
        for (DomainSpecificField field : primarySubject) {
            if (!firstParameter) {
                jpql.append(",");
            }
            // FIXME Hibernate OGM with the MongoDB backend currently does not support named
            // FIXME or positional parameters in queries, so we add them directly into the query.
            // FIXME Revisit in the future and adjust when it is supported
            jpql.append("{ primarySubject: { name: \"").append(field.getName()).append("\"");
            jpql.append(", value: \"").append(field.getValue()).append("\"} }");

            firstParameter = false;
        }
        jpql.append("] }");

        Query query = this.getEntityManager().createNativeQuery(jpql.toString(), getPersistentClass());

        try {
            return (Alarm) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

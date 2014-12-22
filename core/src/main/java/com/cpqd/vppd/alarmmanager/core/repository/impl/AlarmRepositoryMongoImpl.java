package com.cpqd.vppd.alarmmanager.core.repository.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.mongoconnector.annotation.JongoCollection;
import com.google.common.collect.Lists;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Repository bean for CRUD operations on alarms.
 */
@Stateless
public class AlarmRepositoryMongoImpl implements AlarmRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRepositoryMongoImpl.class);

    @JongoCollection("alarms")
    @Inject
    private MongoCollection alarmsCollection;

    @Override
    public void add(Alarm alarm) {
        alarmsCollection.save(alarm);
    }

    @Override
    public void update(Alarm alarm) {
        alarmsCollection.save(alarm);
    }

    @Override
    public List<Alarm> findCurrentAlarms(AlarmSeverity severity, Date from, Date to) {
        List<Object> parameters = new ArrayList<>();

        StringBuilder query = new StringBuilder("{ disappearance: null");
        if (severity != null) {
            query.append(", severity: #");
            parameters.add(severity);
        }
        if (from != null || to != null) {
            query.append(", appearance: { ");
            if (from != null) {
                query.append("$gte: #");
                parameters.add(from);
                if (to != null) {
                    query.append(", ");
                }
            }
            if (to != null) {
                query.append("$lte: #");
                parameters.add(to);
            }
            query.append(" }");
        }
        query.append(" }");

        try (MongoCursor<Alarm> currentAlarmsCursor = alarmsCollection.find(query.toString(),
                parameters.toArray()).as(Alarm.class)) {

            return Lists.newArrayList(currentAlarmsCursor.iterator());
        } catch (IOException e) {
            LOGGER.error("Error executing 'find' operation in MongoDB", e);
            return null;
        }
    }

    @Override
    public Alarm findCurrentByDomainAndPrimarySubject(String domain, Map<String, Object> primarySubject) {
        return alarmsCollection.findOne("{ domain: #, primarySubject: #, disappearance: null }",
                domain, primarySubject).as(Alarm.class);
    }
}

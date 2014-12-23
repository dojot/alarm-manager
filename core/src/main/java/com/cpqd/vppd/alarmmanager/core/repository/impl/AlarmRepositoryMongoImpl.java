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
import java.util.*;

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
        return findCurrentAlarms(Arrays.asList(new AlarmSeverity[] { severity }), from, to);
    }

    @Override
    public List<Alarm> findCurrentAlarms(List<AlarmSeverity> severities, Date from, Date to) {
        List<Object> parameters = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder("{ disappearance: null");
        if (severities != null && !severities.isEmpty()) {
            queryBuilder.append(", severity: { $in: [");
            boolean firstSeverity = true;
            for (AlarmSeverity severity : severities) {
                if (firstSeverity) {
                    firstSeverity = false;
                } else {
                    queryBuilder.append(", ");
                }
                queryBuilder.append("#");
                parameters.add(severity);
            }
            queryBuilder.append("] }");
        }
        if (from != null || to != null) {
            queryBuilder.append(", appearance: { ");
            if (from != null) {
                queryBuilder.append("$gte: #");
                parameters.add(from);
                if (to != null) {
                    queryBuilder.append(", ");
                }
            }
            if (to != null) {
                queryBuilder.append("$lte: #");
                parameters.add(to);
            }
            queryBuilder.append(" }");
        }
        queryBuilder.append(" }");

        String query = queryBuilder.toString();

        LOGGER.debug("Query to run in MongoDB: '{}'", query);

        try (MongoCursor<Alarm> currentAlarmsCursor = alarmsCollection.find(query,
                parameters.toArray()).sort("{ appearance: -1 }").as(Alarm.class)) {

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

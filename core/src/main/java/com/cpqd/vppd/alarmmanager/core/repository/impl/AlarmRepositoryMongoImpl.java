package com.cpqd.vppd.alarmmanager.core.repository.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryParameters;
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
    public List<Alarm> findCurrentAlarms(CurrentAlarmsQueryParameters parameters) {
        List<Object> queryParams = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder("{ disappearance: null");
        if (parameters.getLastId() != null) {
            queryBuilder.append(", _id: { $lt: # }");
            queryParams.add(parameters.getLastId());
        }
        if (parameters.getText() != null) {
            queryBuilder.append(", $text: { $search: # }");
            queryParams.add(parameters.getText());
        }
        if (parameters.getSeverities() != null && !parameters.getSeverities().isEmpty()) {
            queryBuilder.append(", severity: { $in: [");
            boolean firstSeverity = true;
            for (AlarmSeverity severity : parameters.getSeverities()) {
                if (firstSeverity) {
                    firstSeverity = false;
                } else {
                    queryBuilder.append(", ");
                }
                queryBuilder.append("#");
                queryParams.add(severity);
            }
            queryBuilder.append("] }");
        }
        if (parameters.getFrom() != null || parameters.getTo() != null) {
            queryBuilder.append(", appearance: { ");
            if (parameters.getFrom() != null) {
                queryBuilder.append("$gte: #");
                queryParams.add(parameters.getFrom());
                if (parameters.getTo() != null) {
                    queryBuilder.append(", ");
                }
            }
            if (parameters.getTo() != null) {
                queryBuilder.append("$lte: #");
                queryParams.add(parameters.getTo());
            }
            queryBuilder.append(" }");
        }
        queryBuilder.append(" }");

        String query = queryBuilder.toString();

        LOGGER.debug("Query to run in MongoDB: '{}'", query);

        try (MongoCursor<Alarm> currentAlarmsCursor = alarmsCollection
                .find(query, queryParams.toArray())
                .sort("{ _id: -1 }")
                .limit(parameters.getMaxResults() != null ? parameters.getMaxResults().intValue() : 0)
                .as(Alarm.class)) {

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

package com.cpqd.vppd.alarmmanager.core.repository.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.mongoconnector.annotation.JongoCollection;
import com.google.common.collect.Lists;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Repository bean for CRUD operations on alarms.
 */
@Stateless
public class AlarmRepositoryMongoImpl implements AlarmRepository {

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
    public List<Alarm> findCurrentAlarms() {
        MongoCursor<Alarm> currentAlarmsCursor = alarmsCollection.find("{ disappearance: null }").as(Alarm.class);
        return Lists.newArrayList(currentAlarmsCursor.iterator());
    }

    @Override
    public Alarm findCurrentByDomainAndPrimarySubject(String domain, Map<String, Object> primarySubject) {
        return alarmsCollection.findOne("{ domain: #, primarySubject: #, disappearance: null }",
                domain, primarySubject).as(Alarm.class);
    }
}

package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.utils.repository.GenericRepository;

import javax.ejb.Stateless;

/**
 * Created by fabio on 15/12/14.
 */
@Stateless
public class AlarmRepository extends GenericRepository<Alarm, String> {

    @Override
    protected Class<Alarm> getPersistentClass() {
        return Alarm.class;
    }
}

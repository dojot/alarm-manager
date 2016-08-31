package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.Namespace;
import com.cpqd.vppd.alarmmanager.core.model.BasicAlarmData;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmQueryFilters;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Implementation for alarm business services.
 */
@Stateless
public class AlarmServicesImpl implements AlarmServices {

    @Inject
    private AlarmRepository alarmRepository;

    @Override
    public void add(Alarm alarm) {
        alarmRepository.add(alarm);
    }

    @Override
    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }

    @Override
    public List<Alarm> findAlarmsByFilters(AlarmQueryFilters filters) {
        return alarmRepository.findAlarmsByFilters(filters);
    }

    @Override
    public Alarm find(BasicAlarmData alarm) {
        return alarmRepository.find(alarm);
    }

    @Override
    public List<Namespace> findNamespaces() {
        return alarmRepository.findNamespaces();
    }
}

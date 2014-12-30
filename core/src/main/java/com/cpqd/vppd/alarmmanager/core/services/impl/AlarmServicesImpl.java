package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.BasicAlarmData;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryFilters;
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
    public List<Alarm> findCurrentAlarms(CurrentAlarmsQueryFilters parameters) {
        return alarmRepository.findCurrentAlarmsByFilters(parameters);
    }

    @Override
    public Alarm find(BasicAlarmData alarm) {
        return alarmRepository.find(alarm);
    }
}

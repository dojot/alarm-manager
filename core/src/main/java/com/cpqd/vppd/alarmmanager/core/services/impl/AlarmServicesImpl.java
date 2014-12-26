package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryParameters;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

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
    public List<Alarm> findCurrentAlarms(CurrentAlarmsQueryParameters parameters) {
        return alarmRepository.findCurrentAlarms(parameters);
    }

    @Override
    public Alarm findCurrentByDomainAndPrimarySubject(String domain, Map<String, Object> primarySubject) {
        return alarmRepository.findCurrentByDomainAndPrimarySubject(domain, primarySubject);
    }
}

package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Set;

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

    }

    @Override
    public void clear(Set<DomainSpecificField> primarySubject, Long reportedDisappearanceTimestamp) throws AlarmNotPresentException {

    }

    @Override
    public boolean existsByPrimarySubject(Set<DomainSpecificField> primarySubject) {
        return false;
    }

    @Override
    public Alarm findByPrimarySubject(Set<DomainSpecificField> primarySubject) throws AlarmNotPresentException {
        return null;
    }
}

package com.cpqd.vppd.alarmmanager.core.services;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField;

import javax.ejb.Local;
import java.util.Map;
import java.util.Set;

/**
 * Interface for alarm related business services.
 */
@Local
public interface AlarmServices {
    void add(Alarm alarm);

    void update(Alarm alarm);

    void clear(Map<String, Object> primarySubject, Long reportedDisappearanceTimestamp) throws AlarmNotPresentException;

    Alarm findByPrimarySubject(Map<String, Object> primarySubject);
}

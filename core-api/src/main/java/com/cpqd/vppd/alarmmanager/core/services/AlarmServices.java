package com.cpqd.vppd.alarmmanager.core.services;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;

import javax.ejb.Local;
import java.util.Date;
import java.util.Map;

/**
 * Interface for alarm related business services.
 */
@Local
public interface AlarmServices {
    void add(Alarm alarm);

    void update(Alarm alarm);

    void clear(Map<String, Object> primarySubject, Date reportedDisappearanceTimestamp) throws AlarmNotPresentException;

    Alarm findByPrimarySubject(Map<String, Object> primarySubject);
}

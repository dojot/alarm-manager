package com.cpqd.vppd.alarmmanager.core.services;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryParameters;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for alarm related business services.
 */
@Local
public interface AlarmServices {
    void add(Alarm alarm);

    void update(Alarm alarm);

    List<Alarm> findCurrentAlarms(CurrentAlarmsQueryParameters parameters);

    Alarm findCurrentByDomainAndPrimarySubject(String domain, Map<String, Object> primarySubject);
}

package com.cpqd.vppd.alarmmanager.core.repository.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for alarm repository implementations.
 */
@Local
public interface AlarmRepository {
    public void add(Alarm alarm);

    public void update(Alarm alarm);

    public List<Alarm> findCurrentAlarms(AlarmSeverity severity, Date from, Date to);

    public Alarm findCurrentByDomainAndPrimarySubject(String domain, Map<String, Object> primarySubject);
}

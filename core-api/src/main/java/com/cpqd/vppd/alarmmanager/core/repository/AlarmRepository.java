package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.model.BasicAlarmData;
import com.cpqd.vppd.alarmmanager.core.model.metadata.CountByNamespaceAndSeverity;

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

    public List<CountByNamespaceAndSeverity> getAlarmCountersByNamespaceAndSeverity();

    public List<Alarm> findCurrentAlarmsByFilters(CurrentAlarmsQueryFilters parameters);

    public List<Alarm> findCurrentWarningAlarmsOlderThan(Date timestamp);

    public Alarm find(BasicAlarmData alarm);
}

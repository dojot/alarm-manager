package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.Namespace;
import com.cpqd.vppd.alarmmanager.core.model.BasicAlarmData;
import com.cpqd.vppd.alarmmanager.core.model.metadata.CountByNamespaceAndSeverity;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 * Interface for alarm repository implementations.
 */
@Local
public interface AlarmRepository {
    public void add(Alarm alarm);

    public void update(Alarm alarm);

    public List<CountByNamespaceAndSeverity> getCurrentAlarmCountersByNamespaceAndSeverity();

    public List<Alarm> findAlarmsByFilters(AlarmQueryFilters filters);

    public List<Alarm> findCurrentWarningAlarmsOlderThan(Date timestamp);

    public Alarm find(BasicAlarmData alarm);

    public List<Namespace> findNamespaces();

    public List<Alarm> findAllByPrimarySubject(BasicAlarmData alarm);
}

package com.cpqd.vppd.alarmmanager.core.services;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.Namespace;
import com.cpqd.vppd.alarmmanager.core.model.BasicAlarmData;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmQueryFilters;

import javax.ejb.Local;
import java.util.List;

/**
 * Interface for alarm related business services.
 */
@Local
public interface AlarmServices {
    void add(Alarm alarm);

    void update(Alarm alarm);

    List<Alarm> findAlarmsByFilters(AlarmQueryFilters filters);

    Alarm find(BasicAlarmData alarm);

    List<Namespace> findNamespaces();
}

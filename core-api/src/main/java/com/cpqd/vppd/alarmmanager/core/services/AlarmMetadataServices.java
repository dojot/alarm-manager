package com.cpqd.vppd.alarmmanager.core.services;

import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;

import javax.ejb.Local;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fabio on 26/12/14.
 */
@Local
public interface AlarmMetadataServices {
    public Map<AlarmSeverity, AtomicLong> getCurrentAlarmsMetadata(String namespace);
}

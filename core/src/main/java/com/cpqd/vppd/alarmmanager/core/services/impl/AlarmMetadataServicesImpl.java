package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.event.WBAlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.services.AlarmMetadataServices;
import com.google.common.util.concurrent.AtomicLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Map;

/**
 * Class that acts as an in-memory cache for alarm metadata.
 */
@Singleton
@Startup
public class AlarmMetadataServicesImpl implements AlarmMetadataServices {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetadataServicesImpl.class);

    /**
     * Map that stores current alarm counters indexed by severity.
     */
    private static AtomicLongMap<AlarmSeverity> currentCounters = AtomicLongMap.create();

    @Inject
    private AlarmRepository alarmRepository;

    /**
     * When there is an alarm update event, this method catches it and updates
     * the in-memory counters.
     *
     * @param alarmUpdateEvent the observed event.
     */
    public static void onAlarmUpdateEvent(@Observes @WBAlarmUpdateEvent AlarmUpdateEvent alarmUpdateEvent) {
        // alarm event observed, update current alarm counters
        switch (alarmUpdateEvent.getEvent()) {
            case Appearance:
                currentCounters.getAndIncrement(alarmUpdateEvent.getAlarm().getSeverity());
                break;

            case Update:
                currentCounters.getAndDecrement(alarmUpdateEvent.getPreviousSeverity());
                currentCounters.getAndIncrement(alarmUpdateEvent.getAlarm().getSeverity());
                break;

            case Disappearance:
                currentCounters.getAndDecrement(alarmUpdateEvent.getAlarm().getSeverity());
                break;
        }
    }

    @PostConstruct
    @Schedule(hour = "*", persistent = false)
    public void synchronizeCountersWithDb() {
        Map<AlarmSeverity, Long> counters = alarmRepository.getAlarmCountersBySeverity();

        // make sure all possible severity values are inserted into the map
        for (AlarmSeverity severity : AlarmSeverity.values()) {
            Long counter = counters.get(severity);
            currentCounters.put(severity, counter != null ? counter : 0);
        }

        // TODO try to eliminate the need for this
        currentCounters.remove(AlarmSeverity.Clear);
    }

    @Override
    public Map<AlarmSeverity, Long> getCurrentAlarmsMetadata() {
        return currentCounters.asMap();
    }
}

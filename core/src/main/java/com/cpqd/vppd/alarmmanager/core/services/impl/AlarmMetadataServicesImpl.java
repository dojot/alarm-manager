package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.event.WBAlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.model.metadata.CountByNamespaceAndSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.services.AlarmMetadataServices;
import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
     * Map that stores current alarm counters indexed by namespace severity.
     */
    private static Table<String, AlarmSeverity, AtomicLong> currentCounters = HashBasedTable.create();

    @Inject
    private AlarmRepository alarmRepository;

    private static void initializeNamespaces(Set<String> namespaces) {
        for (String namespace : namespaces) {
            for (AlarmSeverity severity : AlarmSeverity.values()) {
                currentCounters.put(namespace, severity, new AtomicLong(0));
            }

            // TODO try to eliminate the need for this
            currentCounters.remove(namespace, AlarmSeverity.Clear);
        }
    }

    /**
     * When there is an alarm update event, this method catches it and updates
     * the in-memory counters.
     *
     * @param alarmUpdateEvent the observed event.
     */
    public static void onAlarmUpdateEvent(@Observes @WBAlarmUpdateEvent AlarmUpdateEvent alarmUpdateEvent) {
        // alarm event observed, update current alarm counters
        Alarm alarm = alarmUpdateEvent.getAlarm();
        switch (alarmUpdateEvent.getEvent()) {

            case Appearance:
                if (!currentCounters.containsRow(alarm.getNamespace())) {
                    initializeNamespaces(new HashSet<>(Arrays.asList(new String[]{alarm.getNamespace()})));
                }
                currentCounters.get(alarm.getNamespace(), alarm.getSeverity()).getAndIncrement();
                break;

            case Update:
                currentCounters.get(alarm.getNamespace(), alarmUpdateEvent.getPreviousSeverity()).getAndDecrement();
                currentCounters.get(alarm.getNamespace(), alarm.getSeverity()).getAndIncrement();
                break;

            case Disappearance:
                currentCounters.get(alarm.getNamespace(), alarm.getSeverity()).getAndDecrement();
                break;
        }
    }

    @PostConstruct
    @Schedule(hour = "*", persistent = false)
    public void synchronizeCountersWithDb() {
        List<CountByNamespaceAndSeverity> counters = alarmRepository.getCurrentAlarmCountersByNamespaceAndSeverity();

        // start the mapping anew
        currentCounters = HashBasedTable.create();

        // get a set of all the known namespaces
        Set<String> namespaces = new HashSet<>(Lists.transform(counters, new Function<CountByNamespaceAndSeverity, String>() {
            @Override
            public String apply(CountByNamespaceAndSeverity count) {
                String namespace = count.getKey().getNamespace();
                return namespace == null ? "" : namespace;
            }
        }));

        // initialize the counters for all possible severity values
        initializeNamespaces(namespaces);

        // overwrite the received values
        for (CountByNamespaceAndSeverity count : counters) {
            currentCounters.put(count.getKey().getNamespace(),
                    count.getKey().getSeverity(), new AtomicLong(count.getCount()));
        }
    }

    @Override
    public Map<AlarmSeverity, AtomicLong> getCurrentAlarmsMetadata(String namespace) {
        return currentCounters.row(namespace == null ? "" : namespace);
    }
}

package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.event.WBAlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.model.*;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmDeleteQueryFilters;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmQueryFilters;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.services.AlarmMetadataServices;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that handles alarm query requests.
 */
@Named
@ApplicationScoped
public class AlarmQueryHandler {
    @Inject
    private AlarmServices alarmServices;

    @Inject
    private AlarmMetadataServices alarmMetadataServices;

    @Inject
    private AlarmRepository alarmRepository;

    @Inject
    @WBAlarmUpdateEvent
    Event<AlarmUpdateEvent> alarmEventDispatcher;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmQueryEndpoint.class);

    Map<String, Object> getCurrentAlarmsByFilterWithMetadata(AlarmQueryFilters filters) {
        Map<String, Object> result = new HashMap<>();
        Map<AlarmSeverity, AtomicLong> metadata = new HashMap<AlarmSeverity, AtomicLong>();
        metadata.put(AlarmSeverity.Warning, new AtomicLong(0));
        metadata.put(AlarmSeverity.Minor, new AtomicLong(0));
        metadata.put(AlarmSeverity.Major, new AtomicLong(0));
        metadata.put(AlarmSeverity.Critical, new AtomicLong(0));

        List<Alarm> alarms = alarmServices.findAlarmsByFilters(filters);

        // get metadata for all namespaces
        if (filters.getNamespace() != null && filters.getNamespace().equals("all")) {
            List<Namespace> namespaces = alarmServices.findNamespaces();

            for (Namespace namespace : namespaces) {
                Map<AlarmSeverity, AtomicLong> tmp_metadata = alarmMetadataServices.getCurrentAlarmsMetadata(namespace.namespace);
                for (Map.Entry<AlarmSeverity, AtomicLong> instance : tmp_metadata.entrySet()) {
                    long sum = instance.getValue().get() + metadata.get(instance.getKey()).get();
                    metadata.put(instance.getKey(), new AtomicLong(sum));
                }
            }
        }

        // get metadata for a single namespace
        else {
            metadata = alarmMetadataServices.getCurrentAlarmsMetadata(filters.getNamespace());
        }

        result.put("alarms", alarms);
        result.put("metadata", metadata);

        return result;
    }

    List<Alarm> getAlarmsByFilters(AlarmQueryFilters filters) {
        return alarmServices.findAlarmsByFilters(filters);
    }

    String deleteAlarmsByFilters(AlarmQueryFilters filters, AlarmDeleteQueryFilters delete_filters) {
        List<Alarm> alarms = alarmServices.findAlarmsByFilters(filters);

        for (Alarm alarm : alarms) {

            if ( ( delete_filters.getInstance_id() == null ||
                    delete_filters.getInstance_id().equals(alarm.getPrimarySubject().get("instance_id")) )
                    && ( delete_filters.getModule_name() == null ||
                    delete_filters.getModule_name().equals(alarm.getPrimarySubject().get("module_name")) ) ) {
                Date disappearance = new DateTime(alarm.getAppearance().getTime(), DateTimeZone.UTC).plusMinutes(5).toDate();
                alarm.setDisappearance(disappearance);
                alarm.setReason(AlarmDisappearanceReason.SystemReset);
                alarm.setLastModified(disappearance);

                LOGGER.info("Cleared: '{}'", alarm);
                alarmRepository.update(alarm);
            }

            // fire an event indicating there's been an alarm update so interested parties are notified
            alarmEventDispatcher.fire(new AlarmUpdateEvent(AlarmOccurrence.Disappearance, null, alarm));
        }

        return "Alarms from " + filters.getNamespace() + " cleared";
    }

    List<Namespace> getNamespaces() {
        return alarmServices.findNamespaces();
    }
}

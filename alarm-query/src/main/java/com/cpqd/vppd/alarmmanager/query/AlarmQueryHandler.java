package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.Namespace;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmQueryFilters;
import com.cpqd.vppd.alarmmanager.core.services.AlarmMetadataServices;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
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

    List<Namespace> getNamespaces() {
        return alarmServices.findNamespaces();
    }
}

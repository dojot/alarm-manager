package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryParameters;
import com.cpqd.vppd.alarmmanager.core.services.AlarmMetadataServices;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Map<String, Object> getCurrentAlarmsAndMetadata(CurrentAlarmsQueryParameters parameters) {
        Map<String, Object> result = new HashMap<>();

        List<Alarm> alarms = alarmServices.findCurrentAlarms(parameters);
        Map<AlarmSeverity, Long> metadata = alarmMetadataServices.getCurrentAlarmsMetadata();

        result.put("alarms", alarms);
        result.put("metadata", metadata);

        return result;
    }
}

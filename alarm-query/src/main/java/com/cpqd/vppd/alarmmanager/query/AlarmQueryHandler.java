package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

/**
 * Class that handles alarm query requests.
 */
@Named
@ApplicationScoped
public class AlarmQueryHandler {
    @Inject
    AlarmServices alarmServices;

    List<Alarm> getCurrentAlarms(List<AlarmSeverity> severities, Date from, Date to) {
        return alarmServices.findCurrentAlarms(severities, from, to);
    }
}

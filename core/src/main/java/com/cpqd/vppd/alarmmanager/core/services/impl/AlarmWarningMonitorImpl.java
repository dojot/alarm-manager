package com.cpqd.vppd.alarmmanager.core.services.impl;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmDisappearanceReason;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmRepository;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryFilters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by fabio on 23/12/14.
 */
@Singleton
@Startup
public class AlarmWarningMonitorImpl {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmWarningMonitorImpl.class);

    @Inject
    private AlarmRepository alarmRepository;

    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void runWarningDisappearanceJob() {
        LOGGER.info("Running warning disappearance job");

        // TODO this works for a fixed amount of minutes. If the interval is configurable,
        // TODO find all the current warnings and calculate the time difference for each one
        Date maxAppearanceDate = DateTime.now(DateTimeZone.UTC).minusMinutes(5).toDate();

        // find current warning alarms that appeared at least five minutes ago
        List<Alarm> currentWarnings = alarmRepository.findCurrentWarningAlarmsOlderThan(maxAppearanceDate);

        for (Alarm alarm : currentWarnings) {
            Date disappearance = new DateTime(alarm.getAppearance().getTime(), DateTimeZone.UTC).plusMinutes(5).toDate();
            alarm.setDisappearance(disappearance);
            alarm.setReason(AlarmDisappearanceReason.NormalClearance);
            alarm.setLastModified(disappearance);

            alarmRepository.update(alarm);
        }

        LOGGER.info("{} warning alarms were normalized", currentWarnings.size());
    }
}

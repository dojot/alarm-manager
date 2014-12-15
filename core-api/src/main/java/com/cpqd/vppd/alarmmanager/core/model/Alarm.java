package com.cpqd.vppd.alarmmanager.core.model;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Abstract class that represents an alarm. This class holds the common data between ClearableAlarms and Warnings.
 */
@MappedSuperclass
public abstract class Alarm extends BasicAlarmData {
    private Long appearance;
    private Long reportedAppearance;
    private Long lastModified;

    public Alarm() {

    }

    public Alarm(BasicAlarmData basicAlarmData) {
        super(basicAlarmData);
    }

    public Long getAppearance() {
        return appearance;
    }

    public void setAppearance(Long appearance) {
        this.appearance = appearance;
    }

    public Long getReportedAppearance() {
        return reportedAppearance;
    }

    public void setReportedAppearance(Long reportedAppearance) {
        this.reportedAppearance = reportedAppearance;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public static Alarm fromAlarmEvent(AlarmEvent alarmEvent, boolean isNew) {
        Alarm alarm = null;
        long currentTimestamp = new Date().getTime();

        switch (alarmEvent.getSeverity()) {
            case Warning:
                alarm = new WarningAlarm(alarmEvent);
                break;

            case Minor:
            case Major:
            case Critical:
                alarm = new ClearableAlarm(alarmEvent);
                break;

            case Clear:
                // TODO
                return null;
        }

        alarm.setLastModified(currentTimestamp);
        if (isNew) {
            alarm.setAppearance(currentTimestamp);
            alarm.setReportedAppearance(alarmEvent.getEventTimestamp());
        }

        return alarm;
    }
}

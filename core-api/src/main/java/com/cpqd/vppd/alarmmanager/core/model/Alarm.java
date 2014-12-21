package com.cpqd.vppd.alarmmanager.core.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Class that represents an alarm.
 */
@Entity
public class Alarm extends BasicAlarmData {
    @NotNull
    private Date appearance;

    @NotNull
    private Date reportedAppearance;

    @NotNull
    private Date lastModified;

    private AlarmDisappearanceReason reason;

    private String reportedReason;

    private Date disappearance;

    private Date reportedDisappearance;

    public Alarm() {

    }

    public Alarm(BasicAlarmData basicAlarmData) {
        super(basicAlarmData);
    }

    public Date getAppearance() {
        return appearance;
    }

    public void setAppearance(Date appearance) {
        this.appearance = appearance;
    }

    public Date getReportedAppearance() {
        return reportedAppearance;
    }

    public void setReportedAppearance(Date reportedAppearance) {
        this.reportedAppearance = reportedAppearance;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public AlarmDisappearanceReason getReason() {
        return reason;
    }

    public void setReason(AlarmDisappearanceReason reason) {
        this.reason = reason;
    }

    public String getReportedReason() {
        return reportedReason;
    }

    public void setReportedReason(String reportedReason) {
        this.reportedReason = reportedReason;
    }

    public Date getDisappearance() {
        return disappearance;
    }

    public void setDisappearance(Date disappearance) {
        this.disappearance = disappearance;
    }

    public Date getReportedDisappearance() {
        return reportedDisappearance;
    }

    public void setReportedDisappearance(Date reportedDisappearance) {
        this.reportedDisappearance = reportedDisappearance;
    }

    public static Alarm fromAlarmEvent(AlarmEvent alarmEvent, boolean isNew) {
        Date currentTimestamp = DateTime.now(DateTimeZone.UTC).toDate();

        Alarm alarm = new Alarm(alarmEvent);
        alarm.setLastModified(currentTimestamp);
        if (isNew) {
            alarm.setAppearance(currentTimestamp);
            alarm.setReportedAppearance(alarmEvent.getEventTimestamp());
        }

        return alarm;
    }
}

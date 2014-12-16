package com.cpqd.vppd.alarmmanager.core.model;

import com.cpqd.vppd.alarmmanager.utils.repository.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * Class that represents an alarm.
 */
@Entity
public class Alarm extends BasicAlarmData {
    private Long appearance;
    private Long reportedAppearance;
    private Long lastModified;
    private AlarmDisappearanceReason reason;
    private String reportedReason;
    private Long disappearance;
    private Long reportedDisappearance;

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

    public Long getDisappearance() {
        return disappearance;
    }

    public void setDisappearance(Long disappearance) {
        this.disappearance = disappearance;
    }

    public Long getReportedDisappearance() {
        return reportedDisappearance;
    }

    public void setReportedDisappearance(Long reportedDisappearance) {
        this.reportedDisappearance = reportedDisappearance;
    }

    public static Alarm fromAlarmEvent(AlarmEvent alarmEvent, boolean isNew) {
        long currentTimestamp = new Date().getTime();

        Alarm alarm = new Alarm(alarmEvent);
        alarm.setLastModified(currentTimestamp);
        if (isNew) {
            alarm.setAppearance(currentTimestamp);
            alarm.setReportedAppearance(alarmEvent.getEventTimestamp());
        }

        return alarm;
    }

//    @Override
//    protected BasicAlarmData getThis() {
//        return this;
//    }
//
//    @Override
//    public Serializable getPk() {
//        return getId();
//    }
//
//    @Override
//    protected Object[] getHashCodeData() {
//        return new Object[] { getPrimarySubject() };
//    }
//
//    @Override
//    protected boolean dataEquals(BasicAlarmData other) {
//        return getPrimarySubject().equals(other.getPrimarySubject());
//    }
}

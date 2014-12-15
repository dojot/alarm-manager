package com.cpqd.vppd.alarmmanager.core.model;

import javax.persistence.Entity;

/**
 * Created by fabio on 05/12/14.
 */
@Entity
public class ClearableAlarm extends Alarm {
    private AlarmDisappearanceReason reason;
    private String reportedReason;
    private Long disappearance;
    private Long reportedDisappearance;

    public ClearableAlarm() {

    }

    public ClearableAlarm(BasicAlarmData alarm) {
        super(alarm);
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
}

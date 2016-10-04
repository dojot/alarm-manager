package com.cpqd.vppd.alarmmanager.core.model;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Class that represents the alarm events sent by the clients.
 */
public class AlarmEvent extends BasicAlarmData {
    /**
     * Timestamp that represents the moment an event occurred from the reporter's perspective.
     */
    @NotNull
    private Date eventTimestamp;

    private Boolean clear_all;

    /**
     * Free text describing what caused the alarm to disappear. May be sent by the client.
     */
    private String disappearanceReason;

    public Date getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getDisappearanceReason() {
        return disappearanceReason;
    }

    public Boolean getClear_all() { return clear_all; }

    public void setDisappearanceReason(String disappearanceReason) {
        this.disappearanceReason = disappearanceReason;
    }
}

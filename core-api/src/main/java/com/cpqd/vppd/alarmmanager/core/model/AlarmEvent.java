package com.cpqd.vppd.alarmmanager.core.model;

import javax.validation.constraints.NotNull;

/**
 * Created by fabio on 09/12/14.
 */
public class AlarmEvent extends BasicAlarmData {
    /**
     * Timestamp that represents the moment an event occurred from the reporter's perspective.
     */
    @NotNull
    private Long eventTimestamp;

    public Long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}

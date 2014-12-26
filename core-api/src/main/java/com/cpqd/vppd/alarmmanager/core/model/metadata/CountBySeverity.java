package com.cpqd.vppd.alarmmanager.core.model.metadata;

import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 26/12/14.
 */
public class CountBySeverity {
    @JsonProperty("_id")
    private AlarmSeverity severity;
    private Long count;

    public AlarmSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlarmSeverity severity) {
        this.severity = severity;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

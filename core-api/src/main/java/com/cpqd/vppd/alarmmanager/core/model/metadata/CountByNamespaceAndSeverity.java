package com.cpqd.vppd.alarmmanager.core.model.metadata;

import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by fabio on 26/12/14.
 */
public class CountByNamespaceAndSeverity {
    @JsonProperty("_id")
    private NamespaceAndSeverity key;
    private Long count;

    public NamespaceAndSeverity getKey() {
        return key;
    }

    public void setKey(NamespaceAndSeverity key) {
        this.key = key;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
